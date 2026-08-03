package net.enchantoutline.shader;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import java.util.Optional;
import net.enchantoutline.EnchantmentGlintOutline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.function.Supplier;

public class Shaders {
    private static final String MOD_ID = EnchantmentGlintOutline.MOD_ID;

    // 26.2: samplers and the vertex format moved onto bind group layouts / explicit vertex bindings.
    // MATRICES_FOG_SNIPPET already carries GLOBALS + MATRICES_PROJECTION + FOG, which is exactly what
    // outline.vsh imports (globals/dynamictransforms/projection/fog). Sampler0 + Sampler2 come from
    // SAMPLER0_SAMPLER2, matching the two `uniform sampler2D` declarations in the shader.
    public static final RenderPipeline.Snippet OUTLINE_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
            .withVertexShader(Identifier.fromNamespaceAndPath(MOD_ID, "core/outline"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(MOD_ID, "core/outline"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER2)
            .withVertexBinding(0, DefaultVertexFormat.BLOCK)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .buildSnippet();

    // depth-only: writes depth, no color. Used to lay down the outline stencil behind the item.
    private static final ColorTargetState NO_COLOR = new ColorTargetState(Optional.empty(), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_NONE);
    private static final DepthStencilState DEPTH_WRITE = new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true);
    // color-only: writes color, no depth. Used to draw the tinted outline on top of the stencil.
    private static final ColorTargetState WRITE_COLOR = new ColorTargetState(Optional.empty(), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_ALL);
    private static final DepthStencilState NO_DEPTH_WRITE = new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false);

    public static final RenderPipeline CUTOUT_PIPELINE_DEPTH_CULL = RenderPipelines.register(
            RenderPipeline.builder(OUTLINE_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/cutout"))
                    .withCull(true)
                    .withColorTargetState(NO_COLOR)
                    .withDepthStencilState(DEPTH_WRITE)
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .build()
    );

    public static final RenderPipeline CUTOUT_PIPELINE_COLOR_CULL = RenderPipelines.register(
            RenderPipeline.builder(OUTLINE_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/cutout"))
                    .withCull(true)
                    .withColorTargetState(WRITE_COLOR)
                    .withDepthStencilState(NO_DEPTH_WRITE)
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .build()
    );

    public static final RenderPipeline CUTOUT_PIPELINE_DEPTH_NOCULL = RenderPipelines.register(
            RenderPipeline.builder(OUTLINE_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/cutout"))
                    .withCull(false)
                    .withColorTargetState(NO_COLOR)
                    .withDepthStencilState(DEPTH_WRITE)
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .build()
    );

    public static final RenderPipeline CUTOUT_PIPELINE_COLOR_NOCULL = RenderPipelines.register(
            RenderPipeline.builder(OUTLINE_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/cutout"))
                    .withCull(false)
                    .withColorTargetState(WRITE_COLOR)
                    .withDepthStencilState(NO_DEPTH_WRITE)
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .build()
    );

    public static final RenderType GLINT_CUTOUT_LAYER = RenderType.create(
            "enchout_glint_normal",
            RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_CULL)
                    .useLightmap()
                    .withTexture("Sampler0", TextureAtlas.LOCATION_ITEMS)
                    .affectsCrumbling()
                    .createRenderSetup()
    );

    public static final RenderType COLOR_CUTOUT_LAYER = RenderType.create(
            "enchnout_color_normal",
            RenderSetup.builder(CUTOUT_PIPELINE_COLOR_CULL)
                    .useLightmap()
                    .withTexture("Sampler0", TextureAtlas.LOCATION_ITEMS)
                    .affectsCrumbling()
                    .createRenderSetup()
    );

    public static final RenderType ZFIX_CUTOUT_LAYER = RenderType.create(
            "enchout_zfix_normal",
            RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_CULL)
                    .useLightmap()
                    .withTexture("Sampler0", TextureAtlas.LOCATION_ITEMS)
                    .affectsCrumbling()
                    .createRenderSetup()
    );

    public static final RenderType ARMOR_ENTITY_GLINT_FIX = RenderType.create(
            "enchout_glint_armor",
            RenderSetup.builder(RenderPipelines.GLINT)
                    .useLightmap()
                    .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR)
                    .setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
                    .affectsCrumbling()
                    .createRenderSetup()
    );

    public static RenderType createGlintRenderLayerCull(Map<String, Identifier> specMap){
        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_CULL)
                .useLightmap()
                .affectsCrumbling();
        for(var entry : specMap.entrySet()){
            builder = builder.withTexture("Sampler0", entry.getValue());
        }
        return RenderType.create("enchout_glint_model", builder.createRenderSetup());
    }

    public static RenderType createGlintRenderLayerNoCull(Map<String, Identifier> specMap) {

        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_NOCULL)
                .useLightmap()
                .affectsCrumbling();

        for(var entry : specMap.entrySet()){
            builder = builder.withTexture("Sampler0", entry.getValue());
        }

        return RenderType.create("enchout_glint_model", builder.createRenderSetup());
    }

    // 26.2: draw ordering no longer lives on the RenderType (there is no shared buffer map to splice
    // into any more). It is expressed purely through SubmitNodeCollector.order(int) at submit time,
    // so the old "draw before/after custom" flags are gone.
    public static RenderType createColorRenderLayerCull(Map<String, Identifier> specMap) {
        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder(CUTOUT_PIPELINE_COLOR_CULL)
                .useLightmap()
                .affectsCrumbling();

        for(var entry : specMap.entrySet()){
            builder = builder.withTexture("Sampler0", entry.getValue());
        }

        return RenderType.create("enchout_color_model", builder.createRenderSetup());
    }

    public static RenderType createColorRenderLayerNoCull(Map<String, Identifier> specMap) {
        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder(CUTOUT_PIPELINE_COLOR_NOCULL)
                .useLightmap()
                .affectsCrumbling();

        for(var entry : specMap.entrySet()){
            builder = builder.withTexture("Sampler0", entry.getValue());
        }

        return RenderType.create("enchout_color_model", builder.createRenderSetup());
    }

    public static RenderType createZFixRenderLayerCull(Map<String, Identifier> specMap) {//TextureSpec
        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_CULL);

        for(var entry : specMap.entrySet()){
            builder = builder.withTexture("Sampler0", entry.getValue()/*, entry.getValue().sampler()*/);
        }

        return RenderType.create("enchout_zfix_model", builder.createRenderSetup());
    }

    public static RenderType createZFixRenderLayerNoCull(Map<String, Identifier> specMap) {//TextureSpec
        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_NOCULL);

        for(var entry : specMap.entrySet()){
            builder = builder.withTexture("Sampler0", entry.getValue()/*, entry.getValue().sampler()*/);
        }

        return RenderType.create("enchout_zfix_model", builder.createRenderSetup());
    }

    /*public static record TextureSpec(Identifier location, Supplier<GpuSampler> sampler) {
    }*/
}
