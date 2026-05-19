package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionResult;

public interface ImmediateRenderCurrentLayer {
    public static interface Before{
        Event<Before> EVENT = EventFactory.createArrayBacked(Before.class,
                (listeners) -> (receiver, layer) -> {
                    for (Before listener : listeners) {
                        InteractionResult result = listener.callback(receiver, layer);

                        if (result != InteractionResult.PASS) {
                            return result;
                        }
                    }

                    return InteractionResult.PASS;
                });

        InteractionResult callback(MultiBufferSource.BufferSource receiver, RenderType layer);
    }

    public static interface After{
        Event<After> EVENT = EventFactory.createArrayBacked(After.class,
                (listeners) -> (receiver, layer) -> {
                    for (After listener : listeners) {
                        InteractionResult result = listener.post(receiver, layer);

                        if (result != InteractionResult.PASS) {
                            return result;
                        }
                    }

                    return InteractionResult.PASS;
                });

        InteractionResult post(MultiBufferSource.BufferSource receiver, RenderType layer);
    }
}
