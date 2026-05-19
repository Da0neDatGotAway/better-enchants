package net.enchantoutline.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.enchantoutline.mixin.RenderSetupAccessor;
import net.enchantoutline.mixin_accessors.RenderLayerAccessor;
import net.enchantoutline.mixin_accessors.RenderPipelineAccessor;
import net.enchantoutline.shader.Shaders;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RenderLayerHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger(RenderLayerHelper.class);

    public static boolean isRenderLayerDoubleSided(RenderLayer renderLayer){
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
    public static Map<String, Shaders.TextureSpec> getIdentifierFromRenderLayer(RenderLayer renderLayer){
        RenderSetup setup = ((RenderLayerAccessor)renderLayer).enchantOutline$getRenderSetup();
        if(setup != null) {
            Map<String, RenderSetup.TextureSpec> textureBase = ((RenderSetupAccessor) (Object) setup).getTextures();
            if (textureBase != null) {
                Map<String, Shaders.TextureSpec> newMap = new HashMap<>(textureBase.size());
                for(var entry : textureBase.entrySet()){
                    newMap.put(entry.getKey(), new Shaders.TextureSpec(entry.getValue().location(), entry.getValue().sampler()));
                }
                return newMap;
            }
        }
        return null;
    }

    public static RenderLayer renderLayerFromRenderLayerDoubleSided(RenderLayer renderLayer, CustomRenderLayers customRenderLayers, Function<Map<String, Shaders.TextureSpec>, RenderLayer> doubleSidedFactory, Function<Map<String, Shaders.TextureSpec>, RenderLayer> singleSidedFactory, RenderLayer fallback, boolean isDoubleSided){
        return renderLayerFromMapDoubleSided(getIdentifierFromRenderLayer(renderLayer), customRenderLayers, doubleSidedFactory, singleSidedFactory, fallback, isDoubleSided);
    }

    public static RenderLayer renderLayerFromMapDoubleSided(@Nullable Map<String, Shaders.TextureSpec> identifier, CustomRenderLayers customRenderLayers, Function<Map<String, Shaders.TextureSpec>, RenderLayer> doubleSidedFactory, Function<Map<String, Shaders.TextureSpec>, RenderLayer> singleSidedFactory, RenderLayer fallback, boolean isDoubleSided){
        if(isDoubleSided){
            return renderLayerFromMapWithFallback(identifier, (id) -> getOrCreateRenderLayerMap(customRenderLayers, doubleSidedFactory, identifier, identifier.toString() + "_db"), fallback);
        }
        return renderLayerFromMapWithFallback(identifier, (id) -> getOrCreateRenderLayerMap(customRenderLayers, singleSidedFactory, identifier, identifier.toString()), fallback);
    }

    public static RenderLayer renderLayerFromMapWithFallback(@Nullable Map<String, Shaders.TextureSpec> identifier, Function<Map<String, Shaders.TextureSpec>, RenderLayer> layerFactory, RenderLayer fallback){
        if(identifier != null){
            RenderLayer newLayer = layerFactory.apply(identifier);
            if(newLayer != null){
                return newLayer;
            }
        }
        return fallback;
    }

    @Nullable
    public static RenderLayer getOrCreateRenderLayerMap(CustomRenderLayers customRenderLayers, Function<Map<String, Shaders.TextureSpec>, RenderLayer> layerFactory, Map<String, Shaders.TextureSpec> identifier, String storagePath){
        RenderLayer output = customRenderLayers.getCustomRenderLayer(storagePath);
        if(output != null)
        {
            return output;
        }
        return customRenderLayers.addCustomRenderLayer(storagePath, layerFactory.apply(identifier));
    }

    @Nullable
    public static RenderLayer getOrCreateRenderLayer(CustomRenderLayers customRenderLayers, Function<Identifier, RenderLayer> layerFactory, Identifier identifier, String storagePath){
        RenderLayer output = customRenderLayers.getCustomRenderLayer(storagePath);
        if(output != null)
        {
            return output;
        }
        return customRenderLayers.addCustomRenderLayer(storagePath, layerFactory.apply(identifier));
    }

    @Nullable
    public static RenderLayer getOrCreateRenderLayer(CustomRenderLayers customRenderLayers, Function<Identifier, RenderLayer> layerCreationFunction, Identifier identifier){
        return getOrCreateRenderLayer(customRenderLayers, layerCreationFunction, identifier, identifier.toString());
    }
}
