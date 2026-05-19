package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

public interface LayerRenderStateRenderSpecial {
    public static interface Callback<T> {
        Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class,
                (listeners) -> (receiver, specialModelRenderer, t, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
                    for (Callback listener : listeners) {
                        InteractionResult result = listener.renderItem(receiver, specialModelRenderer, t, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);

                        if (result != InteractionResult.PASS) {
                            return result;
                        }
                    }

                    return InteractionResult.PASS;
                });

        InteractionResult renderItem(ItemStackRenderState.LayerRenderState receiver, SpecialModelRenderer<T> specialModelRenderer, @Nullable T t, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, int light, int overlay, boolean glint, int i);
    }
    public static interface Post<T> {
        Event<Post> EVENT = EventFactory.createArrayBacked(Post.class,
                (listeners) -> (receiver, specialModelRenderer, t, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
                    for (Post listener : listeners) {
                        InteractionResult result = listener.renderItem(receiver, specialModelRenderer, t, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);

                        if (result != InteractionResult.PASS) {
                            return result;
                        }
                    }

                    return InteractionResult.PASS;
                });

        InteractionResult renderItem(ItemStackRenderState.LayerRenderState receiver, SpecialModelRenderer<T> specialModelRenderer, @Nullable T t, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, int light, int overlay, boolean glint, int i);
    }
}
