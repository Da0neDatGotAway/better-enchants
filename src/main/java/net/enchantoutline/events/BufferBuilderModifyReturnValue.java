package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;

public interface BufferBuilderModifyReturnValue {
    Event<BufferBuilderModifyReturnValue> EVENT = EventFactory.createArrayBacked(BufferBuilderModifyReturnValue.class,
            (listeners) -> (original) -> {
                for (BufferBuilderModifyReturnValue listener : listeners) {
                    @Nullable MultiBufferSource.BufferSource result = listener.getVertexProvider(original);

                    if (result != null) {
                        return result;
                    }
                }

                return null;
            });

    @Nullable MultiBufferSource.BufferSource getVertexProvider(MultiBufferSource.BufferSource original);
}
