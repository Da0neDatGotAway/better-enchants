package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.events.ItemModelManagerUpdateModelCallback;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.HeldItemContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//this mixin class is used since this is the only call to update() and it contains item. Our only way to grab the itemStack
@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {
    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModel;update(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/item/ItemModelManager;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/HeldItemContext;I)V"))
    void enchantOutline$updateItemModel(ItemModel instance, ItemRenderState itemRenderState, ItemStack itemStack, ItemModelManager itemModelManager, ItemDisplayContext itemDisplayContext, ClientWorld clientWorld, HeldItemContext heldItemContext, int seed, Operation<Void> original){
        ActionResult result = ItemModelManagerUpdateModelCallback.EVENT.invoker().onUpdate((ItemModelManager) (Object)this, instance, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed);

        if(result != ActionResult.FAIL){
            original.call(instance, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed);
        }
    }
}
