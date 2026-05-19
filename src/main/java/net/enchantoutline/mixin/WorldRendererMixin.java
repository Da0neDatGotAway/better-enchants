package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @WrapOperation( method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw(Lnet/minecraft/client/render/RenderLayer;)V"))
    private void enchantOutline$RenderMainItemTranslucent(VertexConsumerProvider.Immediate instance, RenderLayer layer, Operation<Void> original)
    {
        ActionResult result = net.enchantoutline.events.WorldRenderer.RenderLayer.Callback.EVENT.invoker().renderLayer(instance, layer);

        if(result != ActionResult.FAIL){
        original.call(instance, layer);

        net.enchantoutline.events.WorldRenderer.RenderLayer.Post.EVENT.invoker().renderLayer(instance, layer);
        }
    }
}
