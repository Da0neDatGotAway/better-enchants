package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.ActionResult;

import java.util.List;

public interface RenderQuads {
    public static interface Normal{
        public static interface Callback {
            Event<RenderQuads.Normal.Callback> EVENT = EventFactory.createArrayBacked(RenderQuads.Normal.Callback.class,
                    (listeners) -> (receiver, orderedRenderCommandQueue, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType) -> {
                        for (RenderQuads.Normal.Callback listener : listeners) {
                            ActionResult result = listener.renderItem(receiver, orderedRenderCommandQueue, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);

                            if (result != ActionResult.PASS) {
                                return result;
                            }
                        }

                        return ActionResult.PASS;
                    });

            ActionResult renderItem(ItemRenderState.LayerRenderState receiver, OrderedRenderCommandQueue orderedRenderCommandQueue, MatrixStack matrixStack, ItemDisplayContext itemDisplayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glintType);
        }

        public static interface Post {
            Event<RenderQuads.Normal.Post> EVENT = EventFactory.createArrayBacked(RenderQuads.Normal.Post.class,
                    (listeners) -> (receiver, orderedRenderCommandQueue, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType) -> {
                        for (RenderQuads.Normal.Post listener : listeners) {
                            ActionResult result = listener.renderItem(receiver, orderedRenderCommandQueue, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);

                            if (result != ActionResult.PASS) {
                                return result;
                            }
                        }

                        return ActionResult.PASS;
                    });
            ActionResult renderItem(ItemRenderState.LayerRenderState receiver, OrderedRenderCommandQueue orderedRenderCommandQueue, MatrixStack matrixStack, ItemDisplayContext itemDisplayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glintType);
        }
    }
    public static interface Model {
        public static interface ModelPart {
            Event<ModelPart> EVENT = EventFactory.createArrayBacked(ModelPart.class,
                    (listeners) -> (receiver, part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, tintedColor, crumblingOverlay, i) -> {
                        for (ModelPart listener : listeners) {
                            ActionResult result = listener.callback(receiver, part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, tintedColor, crumblingOverlay, i);

                            if (result != ActionResult.PASS) {
                                return result;
                            }
                        }

                        return ActionResult.PASS;
                    });
            ActionResult callback(OrderedRenderCommandQueueImpl receiver, net.minecraft.client.model.ModelPart part, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor, ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int i);
        }
    }
}
