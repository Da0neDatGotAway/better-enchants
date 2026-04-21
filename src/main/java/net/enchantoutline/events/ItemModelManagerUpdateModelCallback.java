package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
public interface ItemModelManagerUpdateModelCallback {
    Event<ItemModelManagerUpdateModelCallback> EVENT = EventFactory.createArrayBacked(ItemModelManagerUpdateModelCallback.class,
            (listeners) -> (receiver, model, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed) -> {
                for (ItemModelManagerUpdateModelCallback listener : listeners) {
                    InteractionResult result = listener.onUpdate(receiver, model, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult onUpdate(ItemModelResolver receiver, ItemModel model, ItemStackRenderState itemRenderState, ItemStack itemStack, ItemModelResolver itemModelManager, ItemDisplayContext itemDisplayContext, ClientLevel clientWorld, ItemOwner heldItemContext, int seed);
}
