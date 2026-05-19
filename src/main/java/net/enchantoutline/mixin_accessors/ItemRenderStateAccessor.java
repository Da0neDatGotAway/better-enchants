package net.enchantoutline.mixin_accessors;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public interface ItemRenderStateAccessor {
    @Nullable public Item enchantOutline$getItemRendered();
    public void enchantOutline$setItemRendered(@Nullable Item item);
}
