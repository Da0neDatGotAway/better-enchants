package net.enchantoutline.mixin_accessors;

import net.minecraft.client.model.ModelPart;

import java.util.List;
import java.util.Map;

public interface ModelPartAccessor {
    public List<ModelPart.Cuboid> enchantOutline$getCuboids();
    public Map<String, ModelPart> enchantOutline$getChildren();
}
