package net.enchantoutline.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.enchantoutline.mixin.RenderSetupAccessor;
import net.enchantoutline.mixin_accessors.RenderLayerAccessor;
import net.enchantoutline.mixin_accessors.RenderPipelineAccessor;
import net.enchantoutline.mixin_accessors.TextureAtlasSpriteAccessor;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
            RenderPipeline pipeline = ((RenderSetupAccessor)(Object)setup).enchantOutline$getPipeline();
            if(pipeline != null){
                return !((RenderPipelineAccessor)pipeline).enchantOutline$getCull();
            }
        }
        return false;
    }

    @Nullable
    public static Map<String, Identifier> getIdentifierFromSprite(TextureAtlasSprite sprite){
        if(sprite != null){
            Identifier atlas = ((TextureAtlasSpriteAccessor)((Object)sprite)).enchantOutline$getAtlasLocation();
            return Map.of(atlas.getPath(), atlas);
        }
        return null;
    }


    @Nullable
    public static Map<String, Identifier> getIdentifierFromRenderLayer(RenderType layer) {
        if (layer != null) {
            RenderSetup setup = ((RenderLayerAccessor) ((Object) layer)).enchantOutline$getRenderSetup();
            if (setup != null) {
                Map<String, RenderSetup.TextureBinding> map = ((RenderSetupAccessor) ((Object) setup)).enchantOutline$getTextures();
                String name = null;
                Identifier id = null;
                for (Map.Entry<String, RenderSetup.TextureBinding> entry : map.entrySet()) {
                    RenderSetup.TextureBinding binding = entry.getValue();
                    id = binding.location();
                    name = id.getPath();
                    break;
                }
                if (name != null) {
                    return Map.of(name, id);
                }
            }
        }
        return null;
    }
    public static RenderType renderLayerFromRenderLayerDoubleSided(RenderType renderLayer, CustomRenderLayers customRenderLayers, Function<Map<String, Identifier>, RenderType> doubleSidedFactory, Function<Map<String, Identifier>, RenderType> singleSidedFactory, RenderType fallback, boolean isDoubleSided){
        return renderLayerFromMapDoubleSided(getIdentifierFromRenderLayer(renderLayer), customRenderLayers, doubleSidedFactory, singleSidedFactory, fallback, isDoubleSided);
    }

    public static RenderType renderLayerFromSpriteDoubleSided(TextureAtlasSprite sprite, CustomRenderLayers customRenderLayers, Function<Map<String, Identifier>, RenderType> doubleSidedFactory, Function<Map<String, Identifier>, RenderType> singleSidedFactory, RenderType fallback, boolean isDoubleSided){
        return renderLayerFromMapDoubleSided(getIdentifierFromSprite(sprite), customRenderLayers, doubleSidedFactory, singleSidedFactory, fallback, isDoubleSided);
    }

    public static RenderType renderLayerFromMapDoubleSided(@Nullable Map<String, Identifier> identifier, CustomRenderLayers customRenderLayers, Function<Map<String, Identifier>, RenderType> doubleSidedFactory, Function<Map<String, Identifier>, RenderType> singleSidedFactory, RenderType fallback, boolean isDoubleSided){
        String first;
        if(identifier != null){
            first = identifier.entrySet().stream().findFirst().map(Map.Entry::getKey).orElse(null);
        }else{
            first = null;
        }

        if(isDoubleSided){
            return renderLayerFromMapWithFallback(identifier, (id) -> getOrCreateRenderLayerMap(customRenderLayers, doubleSidedFactory, identifier, first + "_db"), fallback);
        }
        return renderLayerFromMapWithFallback(identifier, (id) -> getOrCreateRenderLayerMap(customRenderLayers, singleSidedFactory, identifier, first), fallback);
    }

    public static RenderType renderLayerFromMapWithFallback(@Nullable Map<String, Identifier> identifier, Function<Map<String, Identifier>, RenderType> layerFactory, RenderType fallback){
        if(identifier != null){
            RenderType newLayer = layerFactory.apply(identifier);
            if(newLayer != null){
                return newLayer;
            }
        }
        return fallback;
    }

    @Nullable
    public static RenderType getOrCreateRenderLayerMap(CustomRenderLayers customRenderLayers, Function<Map<String, Identifier>, RenderType> layerFactory, Map<String, Identifier> identifier, String storagePath){
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
