package net.enchantoutline.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

/**
 * A class to copy models. The input root must have the same traverse as the original model
 */
public class HijackedModel extends Model<Object> {

    public HijackedModel(ModelPart root, Function<Identifier, RenderType> layerFactory) {
        super(root, layerFactory);
    }

    @Override
    public void setupAnim(Object state) {
        return;
    }
}
