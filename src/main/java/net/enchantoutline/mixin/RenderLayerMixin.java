package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.enchantoutline.mixin_accessors.RenderLayerAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderLayer.class)
public class RenderLayerMixin implements RenderLayerAccessor {

    @Shadow
    @Final
    private RenderSetup renderSetup;

    @ModifyReturnValue(method = "areVerticesNotShared", at = @At("RETURN"))
    private boolean enchantOutline$areVerticesNotShared(boolean original){
        @Nullable Boolean result = net.enchantoutline.events.RenderLayer.AreVerticesNotSharedCallback.EVENT.invoker().getVerticesNotShared((RenderLayer) (Object)this, original);

        if(result != null)
        {
            return false;
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
        return renderSetup;
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
