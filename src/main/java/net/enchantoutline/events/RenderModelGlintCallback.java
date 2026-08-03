package net.enchantoutline.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

/**
 * Fired for every model submitted to a {@link SubmitNodeCollection} in 26.2. Replaces the 26.1
 * {@code RenderQuads.Model.ModelPart} event, whose target overload no longer exists.
 */
public interface RenderModelGlintCallback {
    Event<RenderModelGlintCallback> EVENT = EventFactory.createArrayBacked(RenderModelGlintCallback.class,
            (listeners) -> (receiver, model, state, poseStack, renderType, light, overlay, tintedColor, sprite, outlineColor, crumblingOverlay) -> {
                for (RenderModelGlintCallback listener : listeners) {
                    InteractionResult result = listener.onSubmit(receiver, model, state, poseStack, renderType,
                            light, overlay, tintedColor, sprite, outlineColor, crumblingOverlay);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    // Deliberately non-generic: a generic abstract method would stop this from being a functional
    // interface, and callers only ever pass the state straight back through to submitModel.
    InteractionResult onSubmit(SubmitNodeCollection receiver, Model<?> model, Object state, PoseStack poseStack,
                               RenderType renderType, int light, int overlay, int tintedColor,
                               @Nullable TextureAtlasSprite sprite, int outlineColor,
                               ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay);
}
