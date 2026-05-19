package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.events.WorldRendererFirstRenderMainPassCallback;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @WrapOperation(method = "lambda$addMainPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V", ordinal = 0))
    void enchantOutline$addMainPassSkipSoild(MultiBufferSource.BufferSource instance, Operation<Void> original){
        InteractionResult result = WorldRendererFirstRenderMainPassCallback.EVENT.invoker().tryRenderPass();

        if(result != InteractionResult.FAIL){
            original.call(instance);
        }
    }
}
