package net.enchantoutline.mixin_accessors;

import net.minecraft.client.render.RenderSetup;

//TODO: make it so that instead of a shouldDrawBeforeCustom each renderLayer can store the layer it should draw before, and use that. This should fix ALL strange rendering artifacts
public interface RenderLayerAccessor {
    public RenderSetup enchantOutline$getRenderSetup();
    /**
     * If we shouldn't render this layer in the layer buffer
     * @return
     */
    public boolean enchantOutline$shouldUseLayerBuffer();
    public void enchantOutline$setShouldUseLayerBuffer(boolean newUseLayerBuffer);
    public boolean enchantOutline$shouldDrawBeforeCustom();
    public boolean enchantOutline$shouldDrawAfterCustom();
    public void enchantOutline$setDrawBeforeCustom(boolean drawBeforeCustom);
    public void enchantOutline$setDrawAfterCustom(boolean drawAfterCustom);
}
