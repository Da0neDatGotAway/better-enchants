package net.enchantoutline.mixin;

import net.enchantoutline.events.RenderQuads;
import net.enchantoutline.mixin_accessors.OrderedRenderCommandQueueImplAccessor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubmitNodeStorage.class)
public class OrderedRenderCommandQueueImplMixin implements OrderedRenderCommandQueueImplAccessor {
    @Inject(method = "submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ZZILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;I)V", at = @At("HEAD"), cancellable = true)
    void enchant$submitModelPart(ModelPart part, PoseStack matrices, RenderType renderLayer, int light, int overlay, TextureAtlasSprite sprite, boolean sheeted, boolean hasGlint, int tintedColor, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, int i, CallbackInfo ci){
        if(!skipModelPartCallback){
            InteractionResult result = RenderQuads.Model.ModelPart.EVENT.invoker().callback((SubmitNodeStorage)(Object)this, part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, tintedColor, crumblingOverlay, i);

            if(result == InteractionResult.FAIL){
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
