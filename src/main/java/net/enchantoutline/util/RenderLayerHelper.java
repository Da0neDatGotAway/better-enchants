package net.enchantoutline.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.enchantoutline.mixin.RenderSetupAccessor;
import net.enchantoutline.mixin_accessors.RenderLayerAccessor;
import net.enchantoutline.mixin_accessors.RenderPipelineAccessor;
import net.enchantoutline.shader.Shaders;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

public class RenderLayerHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger(RenderLayerHelper.class);

    public static boolean isRenderLayerDoubleSided(RenderType renderLayer){
        RenderSetup setup = ((RenderLayerAccessor)renderLayer).enchantOutline$getRenderSetup();
        if(setup != null){
            RenderPipeline pipeline = ((RenderSetupAccessor)(Object)setup).getPipeline();
            if(pipeline != null){
                return !((RenderPipelineAccessor)pipeline).enchantOutline$getCull();
            }
        }
        return false;
    }

    @Nullable
    public static Map<String, Shaders.TextureSpec> getIdentifierFromRenderLayer(RenderType renderLayer){
        // In MC 26.1 RenderSetup.getTextures() returns Map<String, TextureAndSampler> holding resolved
        // GpuTextureView handles, not the Identifier/Supplier<GpuSampler> pairs the mod needs to build
        // a matching custom layer. Without a way to recover the original binding, callers fall through
        // to the fallback layer.
        return null;
    }

    public static RenderType renderLayerFromRenderLayerDoubleSided(RenderType renderLayer, CustomRenderLayers customRenderLayers, Function<Map<String, Shaders.TextureSpec>, RenderType> doubleSidedFactory, Function<Map<String, Shaders.TextureSpec>, RenderType> singleSidedFactory, RenderType fallback, boolean isDoubleSided){
        return renderLayerFromMapDoubleSided(getIdentifierFromRenderLayer(renderLayer), customRenderLayers, doubleSidedFactory, singleSidedFactory, fallback, isDoubleSided);
    }

    public static RenderType renderLayerFromMapDoubleSided(@Nullable Map<String, Shaders.TextureSpec> identifier, CustomRenderLayers customRenderLayers, Function<Map<String, Shaders.TextureSpec>, RenderType> doubleSidedFactory, Function<Map<String, Shaders.TextureSpec>, RenderType> singleSidedFactory, RenderType fallback, boolean isDoubleSided){
        if(isDoubleSided){
            return renderLayerFromMapWithFallback(identifier, (id) -> getOrCreateRenderLayerMap(customRenderLayers, doubleSidedFactory, identifier, identifier.toString() + "_db"), fallback);
        }
        return renderLayerFromMapWithFallback(identifier, (id) -> getOrCreateRenderLayerMap(customRenderLayers, singleSidedFactory, identifier, identifier.toString()), fallback);
    }

    public static RenderType renderLayerFromMapWithFallback(@Nullable Map<String, Shaders.TextureSpec> identifier, Function<Map<String, Shaders.TextureSpec>, RenderType> layerFactory, RenderType fallback){
        if(identifier != null){
            RenderType newLayer = layerFactory.apply(identifier);
            if(newLayer != null){
                return newLayer;
            }
        }
        return fallback;
    }

    @Nullable
    public static RenderType getOrCreateRenderLayerMap(CustomRenderLayers customRenderLayers, Function<Map<String, Shaders.TextureSpec>, RenderType> layerFactory, Map<String, Shaders.TextureSpec> identifier, String storagePath){
        RenderType output = customRenderLayers.getCustomRenderLayer(storagePath);
        if(output != null)
        {
            return output;
        }
        return customRenderLayers.addCustomRenderLayer(storagePath, layerFactory.apply(identifier));
    }

    @Nullable
    public static RenderType getOrCreateRenderLayer(CustomRenderLayers customRenderLayers, Function<Identifier, RenderType> layerFactory, Identifier identifier, String storagePath){
        RenderType output = customRenderLayers.getCustomRenderLayer(storagePath);
        if(output != null)
        {
            return output;
        }
        return customRenderLayers.addCustomRenderLayer(storagePath, layerFactory.apply(identifier));
    }

    @Nullable
    public static RenderType getOrCreateRenderLayer(CustomRenderLayers customRenderLayers, Function<Identifier, RenderType> layerCreationFunction, Identifier identifier){
        return getOrCreateRenderLayer(customRenderLayers, layerCreationFunction, identifier, identifier.toString());
    }
}
