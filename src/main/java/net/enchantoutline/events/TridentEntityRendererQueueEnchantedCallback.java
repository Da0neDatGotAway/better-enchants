package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

//again, purposefully not going to assign a type since I need generics
public interface TridentEntityRendererQueueEnchantedCallback<S> {
    Event<TridentEntityRendererQueueEnchantedCallback> EVENT = EventFactory.createArrayBacked(TridentEntityRendererQueueEnchantedCallback.class,
            (listeners) -> ( queueHolder, queue, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand) -> {
                for (TridentEntityRendererQueueEnchantedCallback listener : listeners) {
                    InteractionResult result = listener.onQueue(queueHolder, queue, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult onQueue(SubmitNodeCollector queueHolder, OrderedSubmitNodeCollector queue, Model<? super S> model, S s, PoseStack matrixStack, RenderType renderLayer, int light, int overlay, int tintColor, @Nullable TextureAtlasSprite sprite, int outlineColor, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlayCommand);
}
