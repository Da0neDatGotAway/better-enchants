package net.enchantoutline.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

//purposefully not going to assign a type since I need generics
public interface EquipmentRendererQueueEnchantedCallback<S> {
    Event<EquipmentRendererQueueEnchantedCallback> EVENT = EventFactory.createArrayBacked(EquipmentRendererQueueEnchantedCallback.class,
            (listeners) -> ( queueHolder, renderedStack, queue, texture, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand) -> {
                for (EquipmentRendererQueueEnchantedCallback listener : listeners) {
                    ActionResult result = listener.onQueue(queueHolder, renderedStack, queue, texture, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult onQueue(OrderedRenderCommandQueue queueHolder, ItemStack renderedStack, RenderCommandQueue queue, Identifier texture, Model<? super S> model, S s, MatrixStack matrixStack, RenderLayer renderLayer, int light, int overlay, int tintColor, @Nullable Sprite sprite, int outlineColor, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand);
}
