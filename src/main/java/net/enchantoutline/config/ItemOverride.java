package net.enchantoutline.config;

import net.minecraft.util.math.ColorHelper;

public class ItemOverride {
    public boolean render = true;
    public boolean override_outline_size = false;
    public float outline_size = 20;
    public boolean override_render_solid = false;
    public boolean render_solid = false;
    public boolean override_render_solid_outline_color = false;
    public int[] render_solid_outline_color = {210,150,248};

    public ItemOverride(ItemOverride from){
        render = from.render;
        override_outline_size = from.override_outline_size;
        outline_size = from.outline_size;
        override_render_solid = from.override_render_solid;
        render_solid = from.render_solid;
        override_render_solid_outline_color = from.override_render_solid_outline_color;
        render_solid_outline_color = from.render_solid_outline_color;
    }
    public ItemOverride(){}

    public void setRender(boolean render){
        this.render = render;
    }
    public boolean shouldRender(){
        return render;
    }

    public void setOverrideOutlineSize(boolean overrideOutlineSize){
        this.override_outline_size = overrideOutlineSize;
    }

    public boolean shouldOverrideOutlineSize() {
        return override_outline_size;
    }

    public void setOutlineSize(float outlineSize){
        this.outline_size = outlineSize;
    }

    public float getOutlineSize(){
        return outline_size;
    }

    public void setOverrideRenderSolid(boolean overrideRenderSolid){
        this.override_render_solid = overrideRenderSolid;
    }

    public boolean shouldOverrideRenderSolid(){
        return override_render_solid;
    }

    public void setRenderSolid(boolean renderSolid){
        this.render_solid = renderSolid;
    }

    public boolean shouldRenderSolid(){
        return render_solid;
    }

    public void setOverrideRenderSolidOutlineColor(boolean overrideRenderSolidOutlineColor){
        this.override_render_solid_outline_color = overrideRenderSolidOutlineColor;
    }

    public boolean shouldOverrideRenderSolidOutlineColor(){
        return override_render_solid_outline_color;
    }

    public void setRenderSolidOutlineColor(int[] color)
    {
        this.render_solid_outline_color = color;
    }

    public int[] getRenderSolidOutlineColor(){
        return render_solid_outline_color;
    }
}
