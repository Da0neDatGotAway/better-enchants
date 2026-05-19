package net.enchantoutline.mixin_accessors;

import net.minecraft.client.render.item.ItemRenderState;
import org.jetbrains.annotations.Nullable;

public interface ItemRenderState_LayerRenderStateAccessor {
    @Nullable public ItemRenderState enchantOutline$getOwningRenderState();
    public void enchantOutline$setOwningItemRenderState(@Nullable ItemRenderState itemRenderState);
}
