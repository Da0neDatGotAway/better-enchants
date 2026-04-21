package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.events.ItemModelManagerUpdateModelCallback;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//this mixin class is used since this is the only call to update() and it contains item. Our only way to grab the itemStack
@Mixin(ItemModelResolver.class)
public class ItemModelManagerMixin {
    @WrapOperation(method = "appendItemLayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemModel;update(Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/item/ItemModelResolver;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/ItemOwner;I)V"))
    void enchantOutline$updateItemModel(ItemModel instance, ItemStackRenderState itemRenderState, ItemStack itemStack, ItemModelResolver itemModelManager, ItemDisplayContext itemDisplayContext, ClientLevel clientWorld, ItemOwner heldItemContext, int seed, Operation<Void> original){
        InteractionResult result = ItemModelManagerUpdateModelCallback.EVENT.invoker().onUpdate((ItemModelResolver) (Object)this, instance, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed);

        if(result != InteractionResult.FAIL){
            original.call(instance, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed);
        }
    }
}
