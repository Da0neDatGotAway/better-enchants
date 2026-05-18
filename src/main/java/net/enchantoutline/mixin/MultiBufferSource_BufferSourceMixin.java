package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.EnchantmentGlintOutline;
import net.enchantoutline.events.ImmediateRenderCurrentLayer;
import net.enchantoutline.mixin_accessors.MultiBufferSource_BufferSourceAccessor;
import net.fabricmc.loader.impl.util.log.Log;
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

    //endLastBatch is the equivalent of DrawCurrentLayer
    @WrapOperation(method = "endLastBatch", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/rendertype/RenderType;)V"))
    void enchantOutline$OnDrawCurrentLayer(MultiBufferSource.BufferSource instance, RenderType layer, Operation<Void> original){

        InteractionResult result = ImmediateRenderCurrentLayer.Before.EVENT.invoker().callback(instance, layer);

        if(result != InteractionResult.FAIL){
            original.call(instance, layer);

            ImmediateRenderCurrentLayer.After.EVENT.invoker().post(instance, layer);
        }
    }

    @Inject(method = "endBatch()V", at = @At("HEAD"))
    void enchantOutline$Debug2(CallbackInfo ci){
        EnchantmentGlintOutline.Log1();
    }

    @Inject(method = "endBatch(Lnet/minecraft/client/renderer/rendertype/RenderType;)V", at = @At("HEAD"))
    void enchantOutline$Debug1(RenderType type, CallbackInfo ci){
        EnchantmentGlintOutline.Log2(type);
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
