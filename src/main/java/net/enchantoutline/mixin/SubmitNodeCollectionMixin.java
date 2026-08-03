package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.enchantoutline.events.RenderModelGlintCallback;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.2 replacement for the old {@code SubmitNodeStorage#submitModelPart} hook.
 * <p>
 * In 26.1 a model part carried an explicit {@code hasFoil} flag, which is how the mod knew a special
 * item model (shield, in-hand trident, ...) wanted a glint. 26.2 removed that flag entirely and
 * {@link net.minecraft.client.renderer.special.SpecialModelRenderer} implementations now submit the
 * foil as a plain extra {@code submitModel} call using a vanilla glint {@link RenderType}. So the
 * detection moves from "flag on the submit" to "which render type is being submitted", and the hook
 * moves from the (now deleted) model-part overload down to the real {@code submitModel}
 * implementation on {@link SubmitNodeCollection}.
 */
@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin {

    @Inject(
            method = "submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    <S> void enchantOutline$submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType,
                                        int lightCoords, int overlayCoords, int tintedColor,
                                        @Nullable TextureAtlasSprite sprite, int outlineColor,
                                        ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay,
                                        CallbackInfo ci) {
        InteractionResult result = RenderModelGlintCallback.EVENT.invoker().onSubmit(
                (SubmitNodeCollection) (Object) this, model, state, poseStack, renderType,
                lightCoords, overlayCoords, tintedColor, sprite, outlineColor, crumblingOverlay);

        if (result == InteractionResult.FAIL) {
            ci.cancel();
        }
    }
}
