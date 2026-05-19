package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.events.ImmediateRenderCurrentLayer;
import net.enchantoutline.mixin_accessors.VertexConsumerProvider_ImmediateAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.SequencedMap;

@Mixin(VertexConsumerProvider.Immediate.class)
public class VertexConsumerProvider_ImmediateMixin implements VertexConsumerProvider_ImmediateAccessor {

    @Shadow
    @Final
    protected SequencedMap<RenderLayer, BufferAllocator> layerBuffers;

    @WrapOperation(method = "drawCurrentLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw(Lnet/minecraft/client/render/RenderLayer;)V"))
    void enchantOutline$OnDrawCurrentLayer(VertexConsumerProvider.Immediate instance, RenderLayer layer, Operation<Void> original){

        ActionResult result = ImmediateRenderCurrentLayer.Before.EVENT.invoker().callback(instance, layer);

        if(result != ActionResult.FAIL){
            original.call(instance, layer);

            ImmediateRenderCurrentLayer.After.EVENT.invoker().post(instance, layer);
        }
    }

    //@Inject(method = "draw(Lnet/minecraft/client/render/RenderLayer;)V", at = @At(value = "HEAD"))
    //void enchantOutline$drawLayer(RenderLayer layer, CallbackInfo ci){
    //    LogUtils.getLogger().info("layer drawn: {}", layer);
    //}

    @Unique
    Map<Object, Integer> dirty = new HashMap<>();

    @Override
    public SequencedMap<RenderLayer, BufferAllocator> enchantOutline$getLayerBuffers() {
        return layerBuffers;
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
