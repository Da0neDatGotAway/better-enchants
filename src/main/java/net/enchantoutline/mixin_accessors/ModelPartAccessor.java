package net.enchantoutline.mixin_accessors;

import net.minecraft.client.model.geom.ModelPart;

import java.util.List;
import java.util.Map;

public interface ModelPartAccessor {
    public List<ModelPart.Cube> enchantOutline$getCuboids();
    public Map<String, ModelPart> enchantOutline$getChildren();
}
