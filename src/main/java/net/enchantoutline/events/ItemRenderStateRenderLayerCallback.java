package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionResult;

public interface ItemRenderStateRenderLayerCallback {
    Event<ItemRenderStateRenderLayerCallback> EVENT = EventFactory.createArrayBacked(ItemRenderStateRenderLayerCallback.class,
            (listeners) -> (receiver, layerRenderState, matrices, orderedRenderCommandQueue, light, overlay, i) -> {
                for (ItemRenderStateRenderLayerCallback listener : listeners) {
                    InteractionResult result = listener.onRender(receiver, layerRenderState, matrices, orderedRenderCommandQueue, light, overlay, i);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult onRender(ItemStackRenderState receiver, ItemStackRenderState.LayerRenderState layerRenderState, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, int light, int overlay, int i);
}
