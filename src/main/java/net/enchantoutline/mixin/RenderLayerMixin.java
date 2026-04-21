package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.enchantoutline.mixin_accessors.RenderLayerAccessor;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderType.class)
public class RenderLayerMixin implements RenderLayerAccessor {

    @Shadow
    @Final
    private RenderSetup state;

    @ModifyReturnValue(method = "canConsolidateConsecutiveGeometry", at = @At("RETURN"))
    private boolean enchantOutline$canConsolidateConsecutiveGeometry(boolean original){
        // Yarn's areVerticesNotShared inverts to Mojang's canConsolidateConsecutiveGeometry.
        // If any listener says "vertices not shared", consolidation is disallowed.
        @Nullable Boolean result = net.enchantoutline.events.RenderLayer.AreVerticesNotSharedCallback.EVENT.invoker().getVerticesNotShared((RenderType) (Object)this, !original);

        if(result != null)
        {
            return true;
        }
        return original;
    }

    @Unique
    boolean shouldUseLayerBuffer = true;

    @Unique
    //It's my mod I can do what I want.
    byte drawBeforeAfterCustom = 0;

    @Override
    public RenderSetup enchantOutline$getRenderSetup() {
        return state;
    }

    @Override
    public boolean enchantOutline$shouldUseLayerBuffer() {
        return shouldUseLayerBuffer;
    }

    @Override
    public void enchantOutline$setShouldUseLayerBuffer(boolean newUseLayerBuffer) {
        shouldUseLayerBuffer = newUseLayerBuffer;
    }

    @Override
    public boolean enchantOutline$shouldDrawBeforeCustom() {
        return drawBeforeAfterCustom == -1;
    }

    @Override
    public boolean enchantOutline$shouldDrawAfterCustom() {
        return drawBeforeAfterCustom == 1;
    }

    @Override
    public void enchantOutline$setDrawBeforeCustom(boolean drawBeforeCustom) {
        if(drawBeforeCustom){
            this.drawBeforeAfterCustom = -1;
            return;
        }
        this.drawBeforeAfterCustom = 0;
    }

    @Override
    public void enchantOutline$setDrawAfterCustom(boolean drawAfterCustom) {
        if(drawAfterCustom){
            this.drawBeforeAfterCustom = 1;
            return;
        }
        this.drawBeforeAfterCustom = 0;
    }
}
