package net.enchantoutline.mixin;

import net.enchantoutline.mixin_accessors.RenderLayerAccessor;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderType.class)
public class RenderLayerMixin implements RenderLayerAccessor {

    @Shadow
    @Final
    private RenderSetup state;

    @Override
    public RenderSetup enchantOutline$getRenderSetup() {
        return state;
    }
}
