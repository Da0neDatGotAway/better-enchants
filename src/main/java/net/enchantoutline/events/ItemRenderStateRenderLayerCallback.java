package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;

public interface ItemRenderStateRenderLayerCallback {
    Event<ItemRenderStateRenderLayerCallback> EVENT = EventFactory.createArrayBacked(ItemRenderStateRenderLayerCallback.class,
            (listeners) -> (receiver, layerRenderState, matrices, orderedRenderCommandQueue, light, overlay, i) -> {
                for (ItemRenderStateRenderLayerCallback listener : listeners) {
                    ActionResult result = listener.onRender(receiver, layerRenderState, matrices, orderedRenderCommandQueue, light, overlay, i);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult onRender(ItemRenderState receiver, ItemRenderState.LayerRenderState layerRenderState, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, int overlay, int i);
}
