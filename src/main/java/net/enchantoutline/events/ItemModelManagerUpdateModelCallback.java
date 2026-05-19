package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.HeldItemContext;

public interface ItemModelManagerUpdateModelCallback {
    Event<ItemModelManagerUpdateModelCallback> EVENT = EventFactory.createArrayBacked(ItemModelManagerUpdateModelCallback.class,
            (listeners) -> (receiver, model, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed) -> {
                for (ItemModelManagerUpdateModelCallback listener : listeners) {
                    ActionResult result = listener.onUpdate(receiver, model, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult onUpdate(ItemModelManager receiver, ItemModel model, ItemRenderState itemRenderState, ItemStack itemStack, ItemModelManager itemModelManager, ItemDisplayContext itemDisplayContext, ClientWorld clientWorld, HeldItemContext heldItemContext, int seed);
}
