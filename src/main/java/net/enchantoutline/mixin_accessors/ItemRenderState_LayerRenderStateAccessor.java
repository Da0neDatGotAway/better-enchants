package net.enchantoutline.mixin_accessors;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jetbrains.annotations.Nullable;

public interface ItemRenderState_LayerRenderStateAccessor {
    @Nullable public ItemStackRenderState enchantOutline$getOwningRenderState();
    public void enchantOutline$setOwningItemRenderState(@Nullable ItemStackRenderState itemRenderState);
}
