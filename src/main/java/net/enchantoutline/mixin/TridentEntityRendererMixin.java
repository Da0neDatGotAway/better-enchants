package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.enchantoutline.events.TridentEntityRendererQueueEnchantedCallback;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownTridentRenderer.class)
public class TridentEntityRendererMixin {
    @WrapOperation(method = "submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    <S> void enchantOutline$SubmittedModel(OrderedSubmitNodeCollector instance, Model<? super S> model, S s, PoseStack matrixStack, RenderType renderLayer, int light, int overlay, int tintColor, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlayCommand, Operation<Void> original, @Local(argsOnly = true) SubmitNodeCollector orderedRenderCommandQueue){
        InteractionResult result = TridentEntityRendererQueueEnchantedCallback.EVENT.invoker().onQueue(orderedRenderCommandQueue, instance, model, s, matrixStack, renderLayer, light, overlay, tintColor, null, 0, crumblingOverlayCommand);

        if(result != InteractionResult.FAIL){
            original.call(instance, model, s, matrixStack, renderLayer, light, overlay, tintColor, crumblingOverlayCommand);
        }
    }
}
