package net.enchantoutline.mixin_accessors;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;

import java.util.Set;

public interface ModelPart_CuboidAccessor {
    public int enchantOutline$getU();
    public int enchantOutline$getV();
    public float enchantOutline$getExtraX();
    public float enchantOutline$getExtraY();
    public float enchantOutline$getExtraZ();
    public boolean enchantOutline$getMirror();
    public float enchantOutline$getTextureWidth();
    public float enchantOutline$getTextureHeight();
    public Set<Direction> enchantOutline$getDirections();
}
