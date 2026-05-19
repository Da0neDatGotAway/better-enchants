package net.enchantoutline.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
public class CustomRenderLayers {
    private final Map<String, RenderLayer> customRenderLayers = new HashMap<>();
    private int dirty = 0;

    public int getDirty()
    {
        return dirty;
    }

    private void setDirty(int dirty)
    {
        this.dirty = dirty;
    }

    public RenderLayer addCustomRenderLayer(String identifier, RenderLayer layer)
    {
        RenderLayer output = customRenderLayers.put(identifier, layer);
        if(output == null)
        {
            setDirty(getDirty()+1);
        }
        return output;
    }

    public RenderLayer getCustomRenderLayer(String identifier)
    {
        return customRenderLayers.get(identifier);
    }

    public boolean containsRenderLayer(RenderLayer layer)
    {
        return customRenderLayers.containsValue(layer);
    }

    public Iterable<RenderLayer> renderLayers()
    {
        return customRenderLayers.values();
    }
}
