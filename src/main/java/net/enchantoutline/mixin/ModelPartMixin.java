package net.enchantoutline.mixin;

import net.enchantoutline.mixin_accessors.ModelPartAccessor;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public class ModelPartMixin implements ModelPartAccessor {
    @Shadow
    @Final
    private List<ModelPart.Cube> cubes;

    @Shadow
    @Final
    private Map<String, ModelPart> children;

    @Override
    public List<ModelPart.Cube> enchantOutline$getCuboids() {
        return cubes;
    }

    @Override
    public Map<String, ModelPart> enchantOutline$getChildren() {
        return children;
    }
}
