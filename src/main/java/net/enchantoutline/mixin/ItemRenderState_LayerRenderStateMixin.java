package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.events.LayerRenderStateRenderSpecial;
import net.enchantoutline.events.RenderQuads;
import net.enchantoutline.mixin_accessors.ItemRenderState_LayerRenderStateAccessor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public class ItemRenderState_LayerRenderStateMixin implements ItemRenderState_LayerRenderStateAccessor {
    @WrapOperation(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/special/SpecialModelRenderer;submit(Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V"))
    <T>void enchantOutline$renderItemSpecial(SpecialModelRenderer<T> instance, @Nullable T t, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, int light, int overlay, boolean glint, int i, Operation<Void> original){
        InteractionResult result = LayerRenderStateRenderSpecial.Callback.EVENT.invoker().renderItem((ItemStackRenderState.LayerRenderState)(Object)this, instance, t, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);

        if(result != InteractionResult.FAIL){
            original.call(instance, t, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);

            LayerRenderStateRenderSpecial.Post.EVENT.invoker().renderItem((ItemStackRenderState.LayerRenderState)(Object)this, instance, t, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);
        }
    }

    @WrapOperation(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitItem(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemDisplayContext;III[ILjava/util/List;Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;)V"))
    void enchantOutline$renderItemNormal(SubmitNodeCollector instance, PoseStack matrixStack, ItemDisplayContext itemDisplayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, ItemStackRenderState.FoilType glintType, Operation<Void> original){
        ItemStackRenderState.LayerRenderState castLayerRenderState = (ItemStackRenderState.LayerRenderState)(Object)(this);
        InteractionResult result = RenderQuads.Normal.Callback.EVENT.invoker().renderItem(castLayerRenderState, instance, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, null, glintType);

        if(result != InteractionResult.FAIL){
            original.call(instance, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, glintType);

            RenderQuads.Normal.Post.EVENT.invoker().renderItem(castLayerRenderState, instance, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, null, glintType);
        }
    }

    @Unique
    @Nullable
    ItemStackRenderState owningItemRenderState;

    @Override
    public @Nullable ItemStackRenderState enchantOutline$getOwningRenderState() {
        return this.owningItemRenderState;
    }

    @Override
    public void enchantOutline$setOwningItemRenderState(@Nullable ItemStackRenderState itemRenderState) {
        this.owningItemRenderState = itemRenderState;
    }
}
