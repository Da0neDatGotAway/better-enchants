package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.events.LayerRenderStateRenderSpecial;
import net.enchantoutline.events.RenderQuads;
import net.enchantoutline.mixin_accessors.ItemRenderState_LayerRenderStateAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemRenderState.LayerRenderState.class)
public class ItemRenderState_LayerRenderStateMixin implements ItemRenderState_LayerRenderStateAccessor {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/special/SpecialModelRenderer;render(Ljava/lang/Object;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IIZI)V"))
    <T>void enchantOutline$renderItemSpecial(SpecialModelRenderer<T> instance, @Nullable T t, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, int overlay, boolean glint, int i, Operation<Void> original){
        ActionResult result = LayerRenderStateRenderSpecial.Callback.EVENT.invoker().renderItem((ItemRenderState.LayerRenderState)(Object)this, instance, t, itemDisplayContext, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);

        if(result != ActionResult.FAIL){
            original.call(instance, t, itemDisplayContext, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);

            LayerRenderStateRenderSpecial.Post.EVENT.invoker().renderItem((ItemRenderState.LayerRenderState)(Object)this, instance, t, itemDisplayContext, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemDisplayContext;III[ILjava/util/List;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V"))
    void enchantOutline$renderItemNormal(OrderedRenderCommandQueue instance, MatrixStack matrixStack, ItemDisplayContext itemDisplayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glintType, Operation<Void> original){
        ItemRenderState.LayerRenderState castLayerRenderState = (ItemRenderState.LayerRenderState)(Object)(this);
        ActionResult result = RenderQuads.Normal.Callback.EVENT.invoker().renderItem(castLayerRenderState, instance, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);

        if(result != ActionResult.FAIL){
            original.call(instance, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);

            RenderQuads.Normal.Post.EVENT.invoker().renderItem(castLayerRenderState, instance, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);
        }
    }

    @Unique
    @Nullable
    ItemRenderState owningItemRenderState;

    @Override
    public @Nullable ItemRenderState enchantOutline$getOwningRenderState() {
        return this.owningItemRenderState;
    }

    @Override
    public void enchantOutline$setOwningItemRenderState(@Nullable ItemRenderState itemRenderState) {
        this.owningItemRenderState = itemRenderState;
    }
}
