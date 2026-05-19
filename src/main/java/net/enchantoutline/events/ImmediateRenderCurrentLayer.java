package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.ActionResult;

public interface ImmediateRenderCurrentLayer {
    public static interface Before{
        Event<Before> EVENT = EventFactory.createArrayBacked(Before.class,
                (listeners) -> (receiver, layer) -> {
                    for (Before listener : listeners) {
                        ActionResult result = listener.callback(receiver, layer);

                        if (result != ActionResult.PASS) {
                            return result;
                        }
                    }

                    return ActionResult.PASS;
                });

        ActionResult callback(VertexConsumerProvider.Immediate receiver, RenderLayer layer);
    }

    public static interface After{
        Event<After> EVENT = EventFactory.createArrayBacked(After.class,
                (listeners) -> (receiver, layer) -> {
                    for (After listener : listeners) {
                        ActionResult result = listener.post(receiver, layer);

                        if (result != ActionResult.PASS) {
                            return result;
                        }
                    }

                    return ActionResult.PASS;
                });

        ActionResult post(VertexConsumerProvider.Immediate receiver, RenderLayer layer);
    }
}
