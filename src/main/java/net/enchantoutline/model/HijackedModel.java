package net.enchantoutline.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A class to copy models. The input root must have the same traverse as the original model
 */
public class HijackedModel extends Model<Object> {

    public HijackedModel(ModelPart root, Function<Identifier, RenderLayer> layerFactory) {
        super(root, layerFactory);
    }

    @Override
    public void setAngles(Object state) {
        return;
    }
}
