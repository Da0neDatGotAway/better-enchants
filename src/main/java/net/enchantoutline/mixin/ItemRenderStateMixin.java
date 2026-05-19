package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.events.ItemRenderStateRenderLayerCallback;
import net.enchantoutline.mixin_accessors.ItemRenderStateAccessor;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

//Again another class just to actually store the item type somewhere
@Mixin(ItemRenderState.class)
public class ItemRenderStateMixin implements ItemRenderStateAccessor {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderState$LayerRenderState;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;III)V"))
    void enchantOutline$renderOnRenderLayer(ItemRenderState.LayerRenderState instance, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, int overlay, int i, Operation<Void> original){
        ActionResult result = ItemRenderStateRenderLayerCallback.EVENT.invoker().onRender((ItemRenderState)(Object)this, instance, matrices, orderedRenderCommandQueue, light, overlay, i);

        if(result != ActionResult.FAIL){
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
