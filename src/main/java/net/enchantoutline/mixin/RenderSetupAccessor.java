package net.enchantoutline.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.render.RenderSetup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RenderSetup.class)
public interface RenderSetupAccessor {
    @Accessor("textures")
    Map<String, RenderSetup.TextureSpec> getTextures();
    @Accessor("pipeline")
    RenderPipeline getPipeline();
}
