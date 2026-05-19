package net.enchantoutline.mixin;

import net.enchantoutline.events.RenderQuads;
import net.enchantoutline.mixin_accessors.OrderedRenderCommandQueueImplAccessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OrderedRenderCommandQueueImpl.class)
public class OrderedRenderCommandQueueImplMixin implements OrderedRenderCommandQueueImplAccessor {
    //jank? what jank? It's too late for me to be programming rn
    @Inject(method = "submitModelPart", at = @At("HEAD"), cancellable = true)
    void enchant$submitModelPart(ModelPart part, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor, ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int i, CallbackInfo ci){
        if(!skipModelPartCallback){
            ActionResult result = RenderQuads.Model.ModelPart.EVENT.invoker().callback((OrderedRenderCommandQueueImpl)(Object)this, part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, tintedColor, crumblingOverlay, i);

            if(result == ActionResult.FAIL){
                ci.cancel();
            }
        }
    }

    @Unique
    private boolean skipModelPartCallback = false;

    @Override
    public boolean enchantOutline$skipModelPartCallback() {
        return skipModelPartCallback;
    }

    @Override
    public void enchantOutline$setSkipModelPartCallback(boolean newSkipModelPartCallback) {
        skipModelPartCallback = newSkipModelPartCallback;
    }
}
