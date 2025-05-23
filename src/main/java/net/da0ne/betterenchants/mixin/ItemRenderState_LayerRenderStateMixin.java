package net.da0ne.betterenchants.mixin;

import net.da0ne.betterenchants.BetterEnchants;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderState.LayerRenderState.class)
public class ItemRenderState_LayerRenderStateMixin {

    @Shadow
    private ItemRenderState.Glint glint;

    @Shadow
    private SpecialModelRenderer<?> specialModelType;

    @Inject(method = "render", at = @At("HEAD"))
    private void Da0ne$RenderEntry(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci)
    {
        if(glint != ItemRenderState.Glint.NONE){
            if(specialModelType != null)
            {
                if(!BetterEnchants.getConfig().getSpecialRenderSolid()) {
                    BetterEnchants.isEnchanted.set(ItemRenderer.getItemGlintConsumer(vertexConsumers, BetterEnchants.ENCHANT_SOLID_LAYER, true, true));
                }
                else{
                    BetterEnchants.isEnchanted.set(vertexConsumers.getBuffer(BetterEnchants.SOLID_SOLID_LAYER));
                }
            }
            else
            {
                if(!BetterEnchants.getConfig().getItemRenderSolid()) {
                    BetterEnchants.isEnchanted.set(ItemRenderer.getItemGlintConsumer(vertexConsumers, BetterEnchants.ENCHANT_CUTOUT_LAYER, true, true));
                }
                else{
                    BetterEnchants.isEnchanted.set(vertexConsumers.getBuffer(BetterEnchants.SOLID_CUTOUT_LAYER));
                }
            }
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void Da0ne$RenderReturn(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci)
    {
        BetterEnchants.isEnchanted.remove();
    }
}
