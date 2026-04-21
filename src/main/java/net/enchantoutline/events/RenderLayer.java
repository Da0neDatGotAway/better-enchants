package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

public interface RenderLayer {
    public static interface AreVerticesNotSharedCallback{
        Event<AreVerticesNotSharedCallback> EVENT = EventFactory.createArrayBacked(AreVerticesNotSharedCallback.class,
                (listeners) -> (receiver, original) -> {
                    for (AreVerticesNotSharedCallback listener : listeners) {
                        @Nullable Boolean result = listener.getVerticesNotShared(receiver, original);

                        if (result != null) {
                            return result;
                        }
                    }

                    return null;
                });

        @Nullable Boolean getVerticesNotShared(net.minecraft.client.renderer.rendertype.RenderType receiver, boolean original);
    }
}
