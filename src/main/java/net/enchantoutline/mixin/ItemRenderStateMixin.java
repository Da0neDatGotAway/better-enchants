package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.events.ItemRenderStateRenderLayerCallback;
import net.enchantoutline.mixin_accessors.ItemRenderStateAccessor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

//Again another class just to actually store the item type somewhere
@Mixin(ItemStackRenderState.class)
public class ItemRenderStateMixin implements ItemRenderStateAccessor {
    @WrapOperation(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState$LayerRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    void enchantOutline$renderOnRenderLayer(ItemStackRenderState.LayerRenderState instance, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, int light, int overlay, int i, Operation<Void> original){
        InteractionResult result = ItemRenderStateRenderLayerCallback.EVENT.invoker().onRender((ItemStackRenderState)(Object)this, instance, matrices, orderedRenderCommandQueue, light, overlay, i);

        if(result != InteractionResult.FAIL){
            original.call(instance, matrices, orderedRenderCommandQueue, light, overlay, i);
        }
    }

    @Unique
    @Nullable
    private Item renderItem = null;

    @Override
    public @Nullable Item enchantOutline$getItemRendered() {
        return this.renderItem;
    }

    @Override
    public void enchantOutline$setItemRendered(@Nullable Item item) {
        this.renderItem = item;
    }
}
