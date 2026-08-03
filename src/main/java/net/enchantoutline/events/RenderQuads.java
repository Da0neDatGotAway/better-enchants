package net.enchantoutline.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.List;

public interface RenderQuads {
    // 26.2: the Model.ModelPart event lived on SubmitNodeStorage#submitModelPart, which no longer
    // exists. Its replacement is RenderModelGlintCallback.
    interface Normal {
        interface Callback {
            Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class,
                    (listeners) -> (receiver, collector, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType) -> {
                        for (Callback listener : listeners) {
                            InteractionResult result = listener.renderItem(receiver, collector, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);

                            if (result != InteractionResult.PASS) {
                                return result;
                            }
                        }

                        return InteractionResult.PASS;
                    });

            InteractionResult renderItem(ItemStackRenderState.LayerRenderState receiver, SubmitNodeCollector collector, PoseStack matrixStack, ItemDisplayContext itemDisplayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderType renderLayer, ItemStackRenderState.FoilType glintType);
        }

        interface Post {
            Event<Post> EVENT = EventFactory.createArrayBacked(Post.class,
                    (listeners) -> (receiver, collector, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType) -> {
                        for (Post listener : listeners) {
                            InteractionResult result = listener.renderItem(receiver, collector, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);

                            if (result != InteractionResult.PASS) {
                                return result;
                            }
                        }

                        return InteractionResult.PASS;
                    });

            InteractionResult renderItem(ItemStackRenderState.LayerRenderState receiver, SubmitNodeCollector collector, PoseStack matrixStack, ItemDisplayContext itemDisplayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderType renderLayer, ItemStackRenderState.FoilType glintType);
        }
    }
}
