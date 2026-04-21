package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.InteractionResult;

import java.util.List;

public interface RenderQuads {
    public static interface Normal{
        public static interface Callback {
            Event<RenderQuads.Normal.Callback> EVENT = EventFactory.createArrayBacked(RenderQuads.Normal.Callback.class,
                    (listeners) -> (receiver, orderedRenderCommandQueue, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType) -> {
                        for (RenderQuads.Normal.Callback listener : listeners) {
                            InteractionResult result = listener.renderItem(receiver, orderedRenderCommandQueue, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);

                            if (result != InteractionResult.PASS) {
                                return result;
                            }
                        }

                        return InteractionResult.PASS;
                    });

            InteractionResult renderItem(ItemStackRenderState.LayerRenderState receiver, OrderedSubmitNodeCollector orderedRenderCommandQueue, PoseStack matrixStack, ItemDisplayContext itemDisplayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderType renderLayer, ItemStackRenderState.FoilType glintType);
        }

        public static interface Post {
            Event<RenderQuads.Normal.Post> EVENT = EventFactory.createArrayBacked(RenderQuads.Normal.Post.class,
                    (listeners) -> (receiver, orderedRenderCommandQueue, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType) -> {
                        for (RenderQuads.Normal.Post listener : listeners) {
                            InteractionResult result = listener.renderItem(receiver, orderedRenderCommandQueue, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);

                            if (result != InteractionResult.PASS) {
                                return result;
                            }
                        }

                        return InteractionResult.PASS;
                    });
            InteractionResult renderItem(ItemStackRenderState.LayerRenderState receiver, OrderedSubmitNodeCollector orderedRenderCommandQueue, PoseStack matrixStack, ItemDisplayContext itemDisplayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderType renderLayer, ItemStackRenderState.FoilType glintType);
        }
    }
    public static interface Model {
        public static interface ModelPart {
            Event<ModelPart> EVENT = EventFactory.createArrayBacked(ModelPart.class,
                    (listeners) -> (receiver, part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, tintedColor, crumblingOverlay, i) -> {
                        for (ModelPart listener : listeners) {
                            InteractionResult result = listener.callback(receiver, part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, tintedColor, crumblingOverlay, i);

                            if (result != InteractionResult.PASS) {
                                return result;
                            }
                        }

                        return InteractionResult.PASS;
                    });
            InteractionResult callback(OrderedSubmitNodeCollector receiver, net.minecraft.client.model.geom.ModelPart part, PoseStack matrices, RenderType renderLayer, int light, int overlay, TextureAtlasSprite sprite, boolean sheeted, boolean hasGlint, int tintedColor, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, int i);
        }
    }
}
