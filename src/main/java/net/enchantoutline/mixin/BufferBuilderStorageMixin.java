package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.enchantoutline.events.BufferBuilderModifyReturnValue;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BufferBuilderStorage.class)
public class BufferBuilderStorageMixin {
    @ModifyReturnValue(method = "getEntityVertexConsumers", at = @At("RETURN"))
    private VertexConsumerProvider.Immediate enchantOutline$getEntityVertexConsumers(VertexConsumerProvider.Immediate original)
    {
        VertexConsumerProvider.Immediate result = BufferBuilderModifyReturnValue.EVENT.invoker().getVertexProvider(original);

        if(result != null){
            return result;
        }

        return original;
    }
}
