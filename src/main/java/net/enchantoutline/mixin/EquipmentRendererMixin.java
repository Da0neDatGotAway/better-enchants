package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.enchantoutline.events.EquipmentRendererQueueEnchantedCallback;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentRendererMixin {
    @WrapOperation(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", ordinal = 1))
    <S> void enchantOutline$renderArmorEnchanted(OrderedSubmitNodeCollector instance, Model<? super S> model, S s, PoseStack poseStack, RenderType renderType, int light, int overlay, int tintColor, @Nullable TextureAtlasSprite textureAtlasSprite, int outlineColor, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, Operation<Void> original, @Local(name = "layerTexture") Identifier layerTexture, @Local(argsOnly = true) SubmitNodeCollector submitNodeCollector, @Local(argsOnly = true) ItemStack itemStack){
        InteractionResult result = EquipmentRendererQueueEnchantedCallback.EVENT.invoker().onQueue(submitNodeCollector, itemStack, instance, layerTexture, model, s, poseStack, renderType, light, overlay, tintColor, textureAtlasSprite, outlineColor, crumblingOverlay);

        if(result != InteractionResult.FAIL){
            original.call(instance, model, s, poseStack, renderType, light, overlay, tintColor, textureAtlasSprite, outlineColor, crumblingOverlay);
        }
    }
}
