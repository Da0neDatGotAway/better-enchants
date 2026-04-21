package net.enchantoutline.util;

import net.minecraft.client.renderer.rendertype.RenderType;

import java.util.HashMap;
import java.util.Map;
public class CustomRenderLayers {
    private final Map<String, RenderType> customRenderLayers = new HashMap<>();
    private int dirty = 0;

    public int getDirty()
    {
        return dirty;
    }

    private void setDirty(int dirty)
    {
        this.dirty = dirty;
    }

    public RenderType addCustomRenderLayer(String identifier, RenderType layer)
    {
        RenderType output = customRenderLayers.put(identifier, layer);
        if(output == null)
        {
            setDirty(getDirty()+1);
        }
        return output;
    }

    public RenderType getCustomRenderLayer(String identifier)
    {
        return customRenderLayers.get(identifier);
    }

    public boolean containsRenderLayer(RenderType layer)
    {
        return customRenderLayers.containsValue(layer);
    }

    public Iterable<RenderType> renderLayers()
    {
        return customRenderLayers.values();
    }
}
