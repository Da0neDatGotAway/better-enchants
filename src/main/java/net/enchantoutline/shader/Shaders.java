package net.enchantoutline.shader;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.enchantoutline.EnchantmentGlintOutline;
import net.enchantoutline.mixin_accessors.RenderLayerAccessor;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Supplier;

public class Shaders {
    private static final String MOD_ID = EnchantmentGlintOutline.MOD_ID;

    //still don't know what I'm doing so just gonna do this
    public static final RenderPipeline.Snippet OUTLINE_SNIPPET = RenderPipeline.builder(RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET)
            .withVertexShader(Identifier.of(MOD_ID, "core/outline"))
            .withFragmentShader(Identifier.of(MOD_ID, "core/outline"))
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS)
            .buildSnippet();

    public static final RenderPipeline CUTOUT_PIPELINE_DEPTH_CULL = RenderPipelines.register(
            RenderPipeline.builder(OUTLINE_SNIPPET)
                    .withLocation(Identifier.of(MOD_ID, "pipeline/cutout"))
                    .withDepthWrite(true)
                    .withColorWrite(false)
                    .withCull(true)
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .build()
    );

    public static final RenderPipeline CUTOUT_PIPELINE_COLOR_CULL = RenderPipelines.register(
            RenderPipeline.builder(OUTLINE_SNIPPET)
                    .withLocation(Identifier.of(MOD_ID, "pipeline/cutout"))
                    .withDepthWrite(false)
                    .withColorWrite(true)
                    .withCull(true)
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .build()
    );

    public static final RenderPipeline CUTOUT_PIPELINE_DEPTH_NOCULL = RenderPipelines.register(
            RenderPipeline.builder(OUTLINE_SNIPPET)
                    .withLocation(Identifier.of(MOD_ID, "pipeline/cutout"))
                    .withDepthWrite(true)
                    .withColorWrite(false)
                    .withCull(false)
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .build()
    );

    public static final RenderPipeline CUTOUT_PIPELINE_COLOR_NOCULL = RenderPipelines.register(
            RenderPipeline.builder(OUTLINE_SNIPPET)
                    .withLocation(Identifier.of(MOD_ID, "pipeline/cutout"))
                    .withDepthWrite(false)
                    .withColorWrite(true)
                    .withCull(false)
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .build()
    );

    public static final RenderLayer GLINT_CUTOUT_LAYER = RenderLayer.of(
            "enchout_glint_normal",
            RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_CULL)
                    .useLightmap()
                    .texture("Sampler0", SpriteAtlasTexture.ITEMS_ATLAS_TEXTURE, RenderLayers.BLOCK_SAMPLER)
                    .crumbling()
                    .build()
    );

    public static final RenderLayer COLOR_CUTOUT_LAYER = RenderLayer.of(
            "enchnout_color_normal",
            RenderSetup.builder(CUTOUT_PIPELINE_COLOR_CULL)
                    .useLightmap()
                    .texture("Sampler0", SpriteAtlasTexture.ITEMS_ATLAS_TEXTURE, RenderLayers.BLOCK_SAMPLER)
                    .crumbling()
                    .build()
    );

    public static final RenderLayer ZFIX_CUTOUT_LAYER = RenderLayer.of(
            "enchout_zfix_normal",
            RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_CULL)
                    .useLightmap()
                    .texture("Sampler0", SpriteAtlasTexture.ITEMS_ATLAS_TEXTURE, RenderLayers.BLOCK_SAMPLER)
                    .crumbling()
                    .build()
    );

    public static final RenderLayer ARMOR_ENTITY_GLINT_FIX = RenderLayer.of(
            "enchantoutline_armor_glint",
            RenderSetup.builder(RenderPipelines.GLINT)
                    .useLightmap()
                    .texture("Sampler0", ItemRenderer.ENTITY_ENCHANTMENT_GLINT)
                    .textureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
                    .crumbling()
                    .build()
    );

    public static RenderLayer createGlintRenderLayerCull(Map<String, TextureSpec> specMap){
        RenderSetup.Builder builder = RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_CULL)
                .useLightmap()
                .crumbling();
        for(var entry : specMap.entrySet()){
            builder = builder.texture(entry.getKey(), entry.getValue().location(), entry.getValue().sampler());
        }
        return RenderLayer.of("enchout_glint_model", builder.build());
    }

    public static RenderLayer createGlintRenderLayerNoCull(Map<String, TextureSpec> specMap) {

        RenderSetup.Builder builder = RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_NOCULL)
                .useLightmap()
                .crumbling();

        for(var entry : specMap.entrySet()){
            builder = builder.texture(entry.getKey(), entry.getValue().location(), entry.getValue().sampler());
        }

        return RenderLayer.of("enchout_glint_model", builder.build());
    }

    public static RenderLayer createColorRenderLayerCull(Map<String, TextureSpec> specMap) {
        return createColorRenderLayerCull(specMap, true);
    }

    //no overlay, whatever that means
    public static RenderLayer createColorRenderLayerCull(Map<String, TextureSpec> specMap, boolean before) {
        RenderSetup.Builder builder = RenderSetup.builder(CUTOUT_PIPELINE_COLOR_CULL)
                .useLightmap()
                .crumbling();

        for(var entry : specMap.entrySet()){
            builder = builder.texture(entry.getKey(), entry.getValue().location(), entry.getValue().sampler());
        }

        RenderLayer layer = RenderLayer.of("enchout_color_model", builder.build());

        RenderLayerAccessor accessor = (RenderLayerAccessor)layer;
        accessor.enchantOutline$setDrawBeforeCustom(before);
        accessor.enchantOutline$setShouldUseLayerBuffer(!before);
        return layer;
    }

    public static RenderLayer createColorRenderLayerNoCull(Map<String, TextureSpec> specMap) {
        return createColorRenderLayerNoCull(specMap ,true);
    }

    //again no overlay
    public static RenderLayer createColorRenderLayerNoCull(Map<String, TextureSpec> specMap, boolean before) {
        RenderSetup.Builder builder = RenderSetup.builder(CUTOUT_PIPELINE_COLOR_NOCULL)
                .useLightmap()
                .crumbling();

        for(var entry : specMap.entrySet()){
            builder = builder.texture(entry.getKey(), entry.getValue().location(), entry.getValue().sampler());
        }

        RenderLayer layer = RenderLayer.of("enchout_color_model", builder.build());

        RenderLayerAccessor accessor = (RenderLayerAccessor)layer;
        accessor.enchantOutline$setDrawBeforeCustom(before);
        accessor.enchantOutline$setShouldUseLayerBuffer(!before);
        return layer;
    }

    public static RenderLayer createZFixRenderLayerCull(Map<String, TextureSpec> specMap) {
        RenderSetup.Builder builder = RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_CULL);

        for(var entry : specMap.entrySet()){
            builder = builder.texture(entry.getKey(), entry.getValue().location(), entry.getValue().sampler());
        }

        return RenderLayer.of("enchout_zfix_model", builder.build());
    }

    public static RenderLayer createZFixRenderLayerNoCull(Map<String, TextureSpec> specMap) {
        RenderSetup.Builder builder = RenderSetup.builder(CUTOUT_PIPELINE_DEPTH_NOCULL);

        for(var entry : specMap.entrySet()){
            builder = builder.texture(entry.getKey(), entry.getValue().location(), entry.getValue().sampler());
        }

        return RenderLayer.of("enchout_zfix_model", builder.build());
    }

    public static record TextureSpec(Identifier location, Supplier<GpuSampler> sampler) {
    }
}
