package net.enchantoutline;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.enchantoutline.config.EnchantmentOutlineConfig;
import net.enchantoutline.config.ItemOverride;
import net.enchantoutline.events.*;
import net.enchantoutline.mixin_accessors.*;
import net.enchantoutline.model.HijackedModel;
import net.enchantoutline.shader.Shaders;
import net.enchantoutline.util.CustomRenderLayers;
import net.enchantoutline.util.ModelHelper;
import net.enchantoutline.util.QuadHelper;
import net.enchantoutline.util.RenderLayerHelper;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class EnchantmentGlintOutline implements ModInitializer {
	public static final String MOD_ID = "enchantment-glint-outline";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final CustomRenderLayers GLINT_LAYERS = new CustomRenderLayers();
	public static final CustomRenderLayers COLOR_LAYERS = new CustomRenderLayers();
	public static final CustomRenderLayers ZFIX_LAYERS = new CustomRenderLayers();

	private static EnchantmentOutlineConfig config;

	//I don't want to go out using a ThreadLocal like this but since we call to an interface class as a middleman I have no way to transfer the data all the way through
	public static final ThreadLocal<ItemRenderState.LayerRenderState> LAYER_RENDER_STATE_RENDER_MODEL_STORAGE = new ThreadLocal<>();

	public static EnchantmentOutlineConfig getConfig()
	{
		return config;
	}

	public static int getColorBatchingQueue(){
		return -9124657;
	}

	public static int getZFixBatchingQueue(){
		return 9124657;
	}

	private static RenderLayer getTargetEnchantGlintLayer(){
		return RenderLayers.armorEntityGlint();
	}

	private static RenderLayer getTargetEnchantColorLayer(){
		return TexturedRenderLayers.getEntitySolid();
	}

	private static RenderLayer getTargetEnchantZFixLayer(){
		return RenderLayers.waterMask();
	}

    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		loadConfig();
		initLayers();

		//---------- Renderer Calls ----------

		//called before non-special item is rendered.
		RenderQuads.Normal.Callback.EVENT.register((receiver, orderedRenderCommandQueue, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType) -> {
			if(config.isEnabled()){
				if (glintType != ItemRenderState.Glint.NONE) {
					@Nullable ItemOverride override = getOverrideFromLayerRenderState(config::getItemOverride, receiver);
					if(override == null || override.shouldRender()){
						float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, false));
						List<BakedQuad> thickenedQuads = QuadHelper.thickenQuad(quads, scale);
						if(config.getRenderSolidOverrideOrDefault(override, false)) {
							//render solid (by having no Z write, while Z test but rendering before the item is Rendered)

							int[] tint = {config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override))};

							RenderLayer colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(renderLayer, COLOR_LAYERS, specMap -> Shaders.createColorRenderLayerNoCull(specMap, false), specMap -> Shaders.createColorRenderLayerCull(specMap, false), Shaders.COLOR_CUTOUT_LAYER, false);
							RenderLayer zFixLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(renderLayer, ZFIX_LAYERS, Shaders::createZFixRenderLayerNoCull, Shaders::createZFixRenderLayerCull, Shaders.ZFIX_CUTOUT_LAYER, false);

							orderedRenderCommandQueue.submitItem(matrixStack, itemDisplayContext, 0, 0, 0, tint, thickenedQuads, colorLayer, ItemRenderState.Glint.NONE);
							orderedRenderCommandQueue.submitItem(matrixStack, itemDisplayContext, 0, 0, 0, tintLayers, thickenedQuads, zFixLayer, ItemRenderState.Glint.NONE);

						}
						else{
							//render glint (by having Z write and Z test, but no color write after rendering the item, in other words write to the depth buffer)

							RenderLayer glintLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(renderLayer, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, false);

							orderedRenderCommandQueue.submitItem(matrixStack, itemDisplayContext, 0, 0, 0, tintLayers, thickenedQuads, glintLayer, glintType);
						}
					}
				}
			}
			return ActionResult.PASS;
		});

		//the other half of render solid for special items. They should either both use the renderCommandQueue or both avoid it though, this mix is not making me happy.
		RenderQuads.Model.ModelPart.EVENT.register((receiver, part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, tintedColor, crumblingOverlay, i) -> {
			if(config.isEnabled()){
				if(hasGlint){
					ItemRenderState.LayerRenderState storedLayerRenderState = LAYER_RENDER_STATE_RENDER_MODEL_STORAGE.get();
					@Nullable ItemOverride override = null;
					if(storedLayerRenderState != null){
						override = getOverrideFromLayerRenderState(config::getItemOverride, storedLayerRenderState);
					}

					boolean isDoubleSided = RenderLayerHelper.isRenderLayerDoubleSided(renderLayer);
					//boolean isDoubleSided = true;

					float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, false));
					ModelPart thickModelPart = ModelHelper.thickenedModelPart(part, scale);
					if(override == null || override.shouldRender()) {
						if (config.getRenderSolidOverrideOrDefault(override, false)) {
							int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

							//get render layer
							RenderLayer colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(renderLayer, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, isDoubleSided);
							RenderLayer zFixLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(renderLayer, ZFIX_LAYERS, Shaders::createZFixRenderLayerNoCull, Shaders::createZFixRenderLayerCull, Shaders.ZFIX_CUTOUT_LAYER, isDoubleSided);

							//render call
							OrderedRenderCommandQueueImplAccessor commandQueueAccessor = (OrderedRenderCommandQueueImplAccessor) receiver;
							commandQueueAccessor.enchantOutline$setSkipModelPartCallback(true);
							receiver.getBatchingQueue(getColorBatchingQueue()).submitModelPart(thickModelPart, matrices, colorLayer, Integer.MAX_VALUE, 0, sprite, sheeted, false, tint, crumblingOverlay, i);
							receiver.getBatchingQueue(getZFixBatchingQueue()).submitModelPart(thickModelPart, matrices, zFixLayer, Integer.MAX_VALUE, 0, sprite, sheeted, false, tint, crumblingOverlay, i);
							commandQueueAccessor.enchantOutline$setSkipModelPartCallback(false);
						} else {
							//instead of using render double-sided for this section it would probably be better to have a creation method for double sided layers. This would be a good improvement

							//get render layer
							RenderLayer glintLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(renderLayer, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, isDoubleSided);

							//render call
							OrderedRenderCommandQueueImplAccessor commandQueueAccessor = (OrderedRenderCommandQueueImplAccessor) receiver;
							commandQueueAccessor.enchantOutline$setSkipModelPartCallback(true);
							receiver.getBatchingQueue(getZFixBatchingQueue()).submitModelPart(thickModelPart, matrices, glintLayer, Integer.MAX_VALUE, 0, sprite, sheeted, true, tintedColor, crumblingOverlay, i);
							commandQueueAccessor.enchantOutline$setSkipModelPartCallback(false);
						}
					}
				}
			}
			return ActionResult.PASS;
		});

		EquipmentRendererQueueEnchantedCallback.EVENT.register((( queueHolder, renderedStack, queue, texture, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand) -> {
			//I can build this using the current renderLayer the model class is surprisingly simple. It just is made of a model part which I already am able to render an outline for. just build a new model every frame and we should be set
			if(config.isEnabled()){
				@Nullable ItemOverride override = null;
				if(renderedStack != null){
					override = getOverrideFromNullableItem(config::getArmorOverride, renderedStack.getItem());
				}
				if(override == null && config.shouldRenderArmor() || override != null && override.shouldRender()){
					model.setAngles(s);

					float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, true));
					//should create it straight from the identifier but it's just not working, this does. hence the garbage
					RenderLayer garbageHackPatchLayer = RenderLayers.armorCutoutNoCull(texture);
					if(config.getRenderSolidOverrideOrDefault(override, true)){
						int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

						//armor is literally always double-sided, the equipment renderer forces it to use double-sided.
						RenderLayer colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, true); //RenderLayerHelper.renderLayerFromIdentifierDoubleSided(texture, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, true);

						HijackedModel thickColorModel = ModelHelper.getThickenedModel(model, layer -> Shaders.COLOR_CUTOUT_LAYER, scale);

						queueHolder.getBatchingQueue(getColorBatchingQueue()).submitModel(thickColorModel, s, matrixStack, colorLayer, Integer.MAX_VALUE, 0, tint, sprite, outlineColor, crumblingOverlayCommand);
					}
					else{
						RenderLayer glintZLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, true);

						HijackedModel thickGlintZModel = ModelHelper.getThickenedModel(model, layer -> Shaders.GLINT_CUTOUT_LAYER, scale);

						queueHolder.getBatchingQueue(getZFixBatchingQueue()).submitModel(thickGlintZModel, s, matrixStack, glintZLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
						queueHolder.getBatchingQueue(getZFixBatchingQueue()+1).submitModel(thickGlintZModel, s, matrixStack, Shaders.ARMOR_ENTITY_GLINT_FIX, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
					}
				}
			}
			return ActionResult.PASS;
		}));

		TridentEntityRendererQueueEnchantedCallback.EVENT.register(((queueHolder, queue, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand) -> {
			if(config.isEnabled()){
				if(renderLayer.equals(RenderLayers.entityGlint())){
					@Nullable ItemOverride override = getOverrideFromNullableItem(config::getItemOverride, Items.TRIDENT);
					if(override == null || override.shouldRender()){
						float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, true));
						RenderLayer garbageHackPatchLayer = model.getLayer(TridentEntityRenderer.TEXTURE);
						if(config.getRenderSolidOverrideOrDefault(override, false)){
							int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

							RenderLayer colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, false);

							HijackedModel thickColorModel = ModelHelper.getThickenedModel(model, layer -> Shaders.COLOR_CUTOUT_LAYER, scale);

							queueHolder.getBatchingQueue(getColorBatchingQueue()).submitModel(thickColorModel, s, matrixStack, colorLayer, Integer.MAX_VALUE, 0, tint, sprite, outlineColor, crumblingOverlayCommand);
						}
						else{
							RenderLayer glintZLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, false);

							HijackedModel thickGlintZModel = ModelHelper.getThickenedModel(model, layer -> Shaders.GLINT_CUTOUT_LAYER, scale);

							queueHolder.getBatchingQueue(getZFixBatchingQueue()).submitModel(thickGlintZModel, s, matrixStack, glintZLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
							queueHolder.getBatchingQueue(getZFixBatchingQueue()+1).submitModel(thickGlintZModel, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
						}
					}
				}
			}
			return ActionResult.PASS;
		}));

		//---------- End Render Calls ----------

		//---------- Render Layer Order ----------

		//solid ordering in WorldRenderer
		WorldRenderer.RenderLayer.Callback.EVENT.register((receiver, renderLayer) -> {

			if(renderLayer.equals(getTargetEnchantColorLayer())){
				for(var customLayer : COLOR_LAYERS.renderLayers())
				{
					if(((RenderLayerAccessor)customLayer).enchantOutline$shouldUseLayerBuffer()) {
						receiver.draw(customLayer);
					}
				}
			}
			return ActionResult.PASS;
		});

		//glint ordering in world renderer
		WorldRenderer.RenderLayer.Callback.EVENT.register((receiver, renderLayer) -> {

			if(renderLayer.equals(getTargetEnchantGlintLayer())){
				for(var customLayer : GLINT_LAYERS.renderLayers())
				{
					if(((RenderLayerAccessor)customLayer).enchantOutline$shouldUseLayerBuffer()) {
						receiver.draw(customLayer);
					}
				}
			}
			return ActionResult.PASS;
		});

		ImmediateRenderCurrentLayer.Before.EVENT.register((receiver, layer) -> {
			for (RenderLayer renderLayer : ((VertexConsumerProvider_ImmediateAccessor)receiver).enchantOutline$getLayerBuffers().keySet()) {
				if(((RenderLayerAccessor)renderLayer).enchantOutline$shouldDrawBeforeCustom()){
					receiver.draw(renderLayer);
				}
			}

			return ActionResult.PASS;
		});

		ImmediateRenderCurrentLayer.After.EVENT.register((receiver, layer) -> {
			for (RenderLayer renderLayer : ((VertexConsumerProvider_ImmediateAccessor)receiver).enchantOutline$getLayerBuffers().keySet()) {
				if(((RenderLayerAccessor)renderLayer).enchantOutline$shouldDrawAfterCustom()){
					receiver.draw(renderLayer);
				}
			}

			return ActionResult.PASS;
		});

		//VertexConsumerProvider contains a setDirty method used to track if we need to update it's return value or not.
		BufferBuilderModifyReturnValue.EVENT.register((original) -> {
			VertexConsumerProvider_ImmediateAccessor accessor = (VertexConsumerProvider_ImmediateAccessor)original;

			var enchantGlintLayer = getTargetEnchantGlintLayer();
			var enchantColorLayer = getTargetEnchantColorLayer();
			var enchantZFixLayer = getTargetEnchantZFixLayer();

			var buffers = accessor.enchantOutline$getLayerBuffers();
			if(!Objects.equals(accessor.enchantOutline$getDirty(GLINT_LAYERS), GLINT_LAYERS.getDirty()) && buffers.containsKey(enchantGlintLayer) || !Objects.equals(accessor.enchantOutline$getDirty(COLOR_LAYERS), COLOR_LAYERS.getDirty()) && buffers.containsKey(enchantColorLayer) || !Objects.equals(accessor.enchantOutline$getDirty(ZFIX_LAYERS), ZFIX_LAYERS.getDirty()) && buffers.containsKey(enchantZFixLayer)){
				accessor.enchantOutline$setDirty(GLINT_LAYERS, GLINT_LAYERS.getDirty());
				accessor.enchantOutline$setDirty(COLOR_LAYERS, COLOR_LAYERS.getDirty());
				accessor.enchantOutline$setDirty(ZFIX_LAYERS, ZFIX_LAYERS.getDirty());

				SequencedMap<RenderLayer, BufferAllocator> clonedBuffer = new Object2ObjectLinkedOpenHashMap<>(buffers);
				buffers.clear();
				for(var set : clonedBuffer.entrySet()) {
					if(!GLINT_LAYERS.containsRenderLayer(set.getKey()) && !COLOR_LAYERS.containsRenderLayer(set.getKey()) && !ZFIX_LAYERS.containsRenderLayer(set.getKey())) {
						if(set.getKey() == enchantColorLayer) {
							for(RenderLayer layer : COLOR_LAYERS.renderLayers()) {
								if(((RenderLayerAccessor)layer).enchantOutline$shouldUseLayerBuffer()){
									buffers.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
								}
							}
						}

						//this block is stupid, but we need to make sure our armor layer goes where we want it to. This is how
						if(set.getKey() == Shaders.ARMOR_ENTITY_GLINT_FIX){
							enchantGlintLayer = Shaders.ARMOR_ENTITY_GLINT_FIX;
						}
						if(set.getKey() == enchantGlintLayer) {
							for(RenderLayer layer : GLINT_LAYERS.renderLayers())
							{
								if(((RenderLayerAccessor)layer).enchantOutline$shouldUseLayerBuffer()) {
									buffers.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
								}
							}
						}
						if(set.getKey() == getTargetEnchantGlintLayer()){
							buffers.put(Shaders.ARMOR_ENTITY_GLINT_FIX, new BufferAllocator(Shaders.ARMOR_ENTITY_GLINT_FIX.getExpectedBufferSize()));
						}
						//end dumb block

						if(set.getKey() == enchantZFixLayer){
							for(RenderLayer layer : ZFIX_LAYERS.renderLayers())
							{
								if(((RenderLayerAccessor)layer).enchantOutline$shouldUseLayerBuffer()) {
									buffers.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
								}
							}
						}
						buffers.put(set.getKey(), set.getValue());
					}
				}
			}

			return null;
		});
		//--------- End Render Layer Order ----------

		//---------- Item Type Storage ----------
		ItemModelManagerUpdateModelCallback.EVENT.register(((receiver, model, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed) -> {
			((ItemRenderStateAccessor)itemRenderState).enchantOutline$setItemRendered(itemStack.getItem());

			return ActionResult.PASS;
		}));

		ItemRenderStateRenderLayerCallback.EVENT.register(((receiver, layerRenderState, matrices, orderedRenderCommandQueue, light, overlay, i) -> {
			((ItemRenderState_LayerRenderStateAccessor)layerRenderState).enchantOutline$setOwningItemRenderState(receiver);

			return ActionResult.PASS;
		}));

		//set the item right before we lose the type
		LayerRenderStateRenderSpecial.Callback.EVENT.register(((receiver, specialModelRenderer, o, itemDisplayContext, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
			LAYER_RENDER_STATE_RENDER_MODEL_STORAGE.set(receiver);

			return ActionResult.PASS;
		}));

		//clear the item right after calling
		LayerRenderStateRenderSpecial.Post.EVENT.register(((receiver, specialModelRenderer, o, itemDisplayContext, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
			LAYER_RENDER_STATE_RENDER_MODEL_STORAGE.remove();

			return ActionResult.PASS;
		}));
		//---------- End Item Type Storage ----------

		//---------- Mod Patches ----------

		//--------- End Mod Patches ----------
	}

	private static void initLayers(){
		GLINT_LAYERS.addCustomRenderLayer(Identifier.of(MOD_ID,"cutoutlayer").toString(), Shaders.GLINT_CUTOUT_LAYER);
		COLOR_LAYERS.addCustomRenderLayer(Identifier.of(MOD_ID,"cutoutlayer").toString(), Shaders.COLOR_CUTOUT_LAYER);
		ZFIX_LAYERS.addCustomRenderLayer(Identifier.of(MOD_ID, "cutoutlayer").toString(), Shaders.ZFIX_CUTOUT_LAYER);
	}

	@Nullable ItemOverride getOverrideFromLayerRenderState(Function<String, @Nullable ItemOverride> overrideGetter, ItemRenderState.LayerRenderState layerRenderState){
		@Nullable ItemRenderState owningState = ((ItemRenderState_LayerRenderStateAccessor)layerRenderState).enchantOutline$getOwningRenderState();
		if(owningState != null){
			@Nullable Item renderedItem = ((ItemRenderStateAccessor)owningState).enchantOutline$getItemRendered();
			return getOverrideFromNullableItem(overrideGetter, renderedItem);
		}
		return null;
	}

	@Nullable ItemOverride getOverrideFromNullableItem(Function<String, @Nullable ItemOverride> overrideGetter, @Nullable Item renderedItem){
		if(renderedItem != null){
			Identifier itemId = Registries.ITEM.getId(renderedItem);
			if(itemId != null){
				return overrideGetter.apply(itemId.toString());
			}
		}
		return null;
	}

	private static void loadConfig() {
		Path configFile = EnchantmentOutlineConfig.CONFIG_FILE;
		if (Files.exists(configFile)) {
			try(BufferedReader reader = Files.newBufferedReader(configFile)) {
				config = EnchantmentOutlineConfig.fromJson(reader);
			} catch (Exception e) {
				LOGGER.error("Error loading Enchantment Glint Outline config file. Default values will be used for this session.", e);
				config = new EnchantmentOutlineConfig();
			}
		} else {
			config = new EnchantmentOutlineConfig();
		}

		// Immediately save config to file to update any fields that may have changed.
		config.saveAsync();
	}
}