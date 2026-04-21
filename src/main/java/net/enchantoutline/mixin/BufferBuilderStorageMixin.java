package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.enchantoutline.events.BufferBuilderModifyReturnValue;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderBuffers.class)
public class BufferBuilderStorageMixin {
    @ModifyReturnValue(method = "bufferSource", at = @At("RETURN"))
    private MultiBufferSource.BufferSource enchantOutline$getEntityVertexConsumers(MultiBufferSource.BufferSource original)
    {
        MultiBufferSource.BufferSource result = BufferBuilderModifyReturnValue.EVENT.invoker().getVertexProvider(original);

        if(result != null){
            return result;
        }

        return original;
    }
}
