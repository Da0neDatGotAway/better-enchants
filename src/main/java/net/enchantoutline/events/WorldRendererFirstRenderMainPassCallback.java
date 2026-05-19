package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;

public interface WorldRendererFirstRenderMainPassCallback {
    Event<WorldRendererFirstRenderMainPassCallback> EVENT = EventFactory.createArrayBacked(WorldRendererFirstRenderMainPassCallback.class,
            (listeners) -> () -> {
                for (WorldRendererFirstRenderMainPassCallback listener : listeners) {
                    InteractionResult result = listener.tryRenderPass();

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult tryRenderPass();
}
