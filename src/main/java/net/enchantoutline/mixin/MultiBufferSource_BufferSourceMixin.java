package net.enchantoutline.mixin;

import net.enchantoutline.events.ImmediateRenderCurrentLayer;
import net.enchantoutline.mixin_accessors.MultiBufferSource_BufferSourceAccessor;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.SequencedMap;

@Mixin(MultiBufferSource.BufferSource.class)
public class MultiBufferSource_BufferSourceMixin implements MultiBufferSource_BufferSourceAccessor {

    @Shadow
    @Final
    protected SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers;

    @Unique
    private boolean enchantOutline$inEndBatchCallback = false;

    @Inject(method = "endBatch(Lnet/minecraft/client/renderer/rendertype/RenderType;)V", at = @At("HEAD"), cancellable = true)
    void enchantOutline$OnDrawCurrentLayerHead(RenderType layer, CallbackInfo ci){
        // Guard against re-entrance: listeners may themselves call endBatch on other layers;
        // firing the event again would recurse without terminating.
        if (enchantOutline$inEndBatchCallback) return;
        enchantOutline$inEndBatchCallback = true;
        try {
            MultiBufferSource.BufferSource self = (MultiBufferSource.BufferSource)(Object)this;
            InteractionResult result = ImmediateRenderCurrentLayer.Before.EVENT.invoker().callback(self, layer);
            if (result == InteractionResult.FAIL) {
                ci.cancel();
            }
        } finally {
            enchantOutline$inEndBatchCallback = false;
        }
    }

    @Inject(method = "endBatch(Lnet/minecraft/client/renderer/rendertype/RenderType;)V", at = @At("RETURN"))
    void enchantOutline$OnDrawCurrentLayerReturn(RenderType layer, CallbackInfo ci){
        if (enchantOutline$inEndBatchCallback) return;
        enchantOutline$inEndBatchCallback = true;
        try {
            MultiBufferSource.BufferSource self = (MultiBufferSource.BufferSource)(Object)this;
            ImmediateRenderCurrentLayer.After.EVENT.invoker().post(self, layer);
        } finally {
            enchantOutline$inEndBatchCallback = false;
        }
    }

    //@Inject(method = "draw(Lnet/minecraft/client/renderer/rendertype/RenderType;)V", at = @At(value = "HEAD"))
    //void enchantOutline$drawLayer(RenderType layer, CallbackInfo ci){
    //    LogUtils.getLogger().info("layer drawn: {}", layer);
    //}

    @Unique
    Map<Object, Integer> dirty = new HashMap<>();

    @Override
    public SequencedMap<RenderType, ByteBufferBuilder> enchantOutline$getLayerBuffers() {
        return fixedBuffers;
    }

    @Override
    public Map<Object, Integer> enchantOutline$getDirtyMap() {
        return dirty;
    }

    @Override
    @Nullable
    public Integer enchantOutline$getDirty(Object o) {
        return dirty.get(o);
    }

    @Override
    public void enchantOutline$setDirty(Object o, int newDirty) {
        dirty.put(o, newDirty);
    }
}
