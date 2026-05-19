package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public interface LayerRenderStateRenderSpecial {
    public static interface Callback<T> {
        Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class,
                (listeners) -> (receiver, specialModelRenderer, t, itemDisplayContext, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
                    for (Callback listener : listeners) {
                        ActionResult result = listener.renderItem(receiver, specialModelRenderer, t, itemDisplayContext, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);

                        if (result != ActionResult.PASS) {
                            return result;
                        }
                    }

                    return ActionResult.PASS;
                });

        ActionResult renderItem(ItemRenderState.LayerRenderState receiver, SpecialModelRenderer<T> specialModelRenderer, @Nullable T t, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, int overlay, boolean glint, int i);
    }
    public static interface Post<T> {
        Event<Post> EVENT = EventFactory.createArrayBacked(Post.class,
                (listeners) -> (receiver, specialModelRenderer, t, itemDisplayContext, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
                    for (Post listener : listeners) {
                        ActionResult result = listener.renderItem(receiver, specialModelRenderer, t, itemDisplayContext, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i);

                        if (result != ActionResult.PASS) {
                            return result;
                        }
                    }

                    return ActionResult.PASS;
                });

        ActionResult renderItem(ItemRenderState.LayerRenderState receiver, SpecialModelRenderer<T> specialModelRenderer, @Nullable T t, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, int overlay, boolean glint, int i);
    }
}
