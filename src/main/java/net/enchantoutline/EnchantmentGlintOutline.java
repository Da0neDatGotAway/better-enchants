package net.enchantoutline;

import it.unimi.dsi.fastutil.Function;
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

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.Identifier;
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
	public static final ThreadLocal<ItemStackRenderState.LayerRenderState> LAYER_RENDER_STATE_RENDER_MODEL_STORAGE = new ThreadLocal<>();

	//26.2: the model hook fires on SubmitNodeCollection, which has no order(int) - only the owning
	//SubmitNodeCollector does. Stash the collector for the duration of a SpecialModelRenderer#submit
	//call so the hook can still place its geometry in the right ordering bucket. This also scopes the
	//hook to special item models, which is exactly what the old submitModelPart+hasFoil path covered.
	public static final ThreadLocal<SubmitNodeCollector> SPECIAL_MODEL_COLLECTOR = new ThreadLocal<>();

	public static EnchantmentOutlineConfig getConfig()
	{
		return config;
	}

	public static int getColorBatchingQueue(){
		return -9124657;
	}

	public static int getGlintBatchingQueue(){
		return 9124657;
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
		RenderQuads.Normal.Callback.EVENT.register((receiver, collector, matrixStack, itemDisplayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType) -> {
			if(config.isEnabled()){
				if (glintType != ItemStackRenderState.FoilType.NONE) {
					@Nullable ItemOverride override = getOverrideFromLayerRenderState(config::getItemOverride, receiver);
					if(override == null || override.shouldRender()){
						float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, false));
						List<BakedQuad> thickenedQuads = QuadHelper.thickenQuad(quads, scale);
						if(config.getRenderSolidOverrideOrDefault(override, false)) {
							//render solid (by having no Z write, while Z test but rendering before the item is Rendered)

							int[] tint = {config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override))};

							RenderType colorLayer = RenderLayerHelper.renderLayerFromSpriteDoubleSided(quads.getFirst().materialInfo().sprite(), COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, false);
							RenderType zFixLayer = RenderLayerHelper.renderLayerFromSpriteDoubleSided(quads.getFirst().materialInfo().sprite(), ZFIX_LAYERS, Shaders::createZFixRenderLayerNoCull, Shaders::createZFixRenderLayerCull, Shaders.ZFIX_CUTOUT_LAYER, false);

							//26.2: this used to rely on splicing our render types into the BufferSource's
							//fixed buffer map so that endBatch flushed them at the right moment. That map is
							//gone; ordering is now purely the order(int) key, which the dispatcher walks in
							//ascending order within each phase. Colour goes before the item, the depth fix after.
							final int colorTint = tint[0];
							collector.order(getColorBatchingQueue()).submitCustomGeometry(matrixStack, colorLayer, (pose, vc) -> {
								QuadHelper.renderCustomGeometryFromQuads(pose, vc, thickenedQuads, colorTint);
							});
							final int zFixTint = tintLayers != null && tintLayers.length > 0 ? tintLayers[0] : 0xFFFFFFFF;
							collector.submitCustomGeometry(matrixStack, zFixLayer, (pose, vc) -> {
								QuadHelper.renderCustomGeometryFromQuads(pose, vc, thickenedQuads, zFixTint);
							});

						}
						else{
							//render glint (by having Z write and Z test, but no color write after rendering the item, in other words write to the depth buffer)
							//should do it properly with the item rendered but without a little bit of work that makes the item disappear so this patch until someone complains
							RenderType glintLayer = RenderLayerHelper.renderLayerFromSpriteDoubleSided(quads.getFirst().materialInfo().sprite(), GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, false);

							final int glintTint = tintLayers != null && tintLayers.length > 0 ? tintLayers[0] : 0xFFFFFFFF;
							collector.order(getGlintBatchingQueue()).submitCustomGeometry(matrixStack, glintLayer, (pose, vc) -> {
								QuadHelper.renderCustomGeometryFromQuads(pose, vc, thickenedQuads, glintTint);
							});
							RenderType enchantLayer = RenderTypes.glint();
							collector.order(getGlintBatchingQueue()+1).submitCustomGeometry(matrixStack, enchantLayer, (pose, vc) -> {
								QuadHelper.renderCustomGeometryFromQuads(pose, vc, thickenedQuads, glintTint);
							});
						}
					}
				}
			}
			return InteractionResult.PASS;
		});

		//The other half of render solid, for special item models (shield, in-hand trident).
		//26.2: submitModelPart's foil flag is gone. Special renderers now submit the foil as a plain
		//extra submitModel using a vanilla glint render type, so we detect it by render type instead.
		//The SPECIAL_MODEL_COLLECTOR guard scopes this to SpecialModelRenderer#submit, which keeps it
		//from colliding with the armour and thrown-trident handlers below.
		RenderModelGlintCallback.EVENT.register((receiver, model, state, poseStack, renderType, light, overlay, tintedColor, sprite, outlineColor, crumblingOverlay) -> {
			if(!config.isEnabled() || model instanceof HijackedModel || !isGlintRenderType(renderType)){
				return InteractionResult.PASS;
			}

			SubmitNodeCollector collector = SPECIAL_MODEL_COLLECTOR.get();
			if(collector == null){
				return InteractionResult.PASS;
			}

			ItemStackRenderState.LayerRenderState storedLayerRenderState = LAYER_RENDER_STATE_RENDER_MODEL_STORAGE.get();
			@Nullable ItemOverride override = null;
			@Nullable Item renderedItem = null;
			if(storedLayerRenderState != null){
				override = getOverrideFromLayerRenderState(config::getItemOverride, storedLayerRenderState);
				renderedItem = getItemFromLayerRenderState(storedLayerRenderState);
			}
			if(override != null && !override.shouldRender()){
				return InteractionResult.PASS;
			}

			//The glint submit carries the *base* sprite, so the outline samples the item's own texture.
			//TridentSpecialRenderer goes through the short submitModel overload and so has no sprite;
			//fall back to its known texture there, as the thrown-trident handler already does.
			RenderType textureSource = null;
			if(sprite == null && renderedItem == Items.TRIDENT){
				textureSource = model.renderType(ThrownTridentRenderer.TRIDENT_LOCATION);
			}

			boolean isDoubleSided = textureSource != null && RenderLayerHelper.isRenderLayerDoubleSided(textureSource);

			float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, false));
			HijackedModel thickModel = ModelHelper.getThickenedModel(model, layer -> Shaders.GLINT_CUTOUT_LAYER, scale);

			if (config.getRenderSolidOverrideOrDefault(override, false)) {
				int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

				RenderType colorLayer = outlineLayer(textureSource, sprite, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, isDoubleSided);
				RenderType zFixLayer = outlineLayer(textureSource, sprite, ZFIX_LAYERS, Shaders::createZFixRenderLayerNoCull, Shaders::createZFixRenderLayerCull, Shaders.ZFIX_CUTOUT_LAYER, isDoubleSided);

				collector.order(getColorBatchingQueue()).submitModel(thickModel, state, poseStack, colorLayer, Integer.MAX_VALUE, 0, tint, sprite, outlineColor, crumblingOverlay);
				collector.submitModel(thickModel, state, poseStack, zFixLayer, Integer.MAX_VALUE, 0, tint, sprite, outlineColor, crumblingOverlay);
			} else {
				RenderType glintLayer = outlineLayer(textureSource, sprite, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, isDoubleSided);

				collector.order(getGlintBatchingQueue()).submitModel(thickModel, state, poseStack, glintLayer, Integer.MAX_VALUE, 0, tintedColor, sprite, outlineColor, crumblingOverlay);
				collector.order(getGlintBatchingQueue()+1).submitModel(thickModel, state, poseStack, RenderTypes.entityGlint(), Integer.MAX_VALUE, 0, tintedColor, sprite, outlineColor, crumblingOverlay);
			}

			return InteractionResult.PASS;
		});

		EquipmentRendererQueueEnchantedCallback.EVENT.register((( queueHolder, renderedStack, queue, texture, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand) -> {
			//I can build this using the current renderLayer the model class is surprisingly simple. It just is made of a model part which I already am able to render an outline for. just build a new model every frame and we should be set
			if(config.isEnabled()){
				@Nullable ItemOverride override = null;
				if(renderedStack != null){
					override = getOverrideFromNullableItem(config::getArmorOverride, renderedStack.getItem());
				}
				if(override == null && config.shouldRenderArmor() || override != null && override.shouldRender()){
					model.setupAnim(s);

					float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, true));
					//should create it straight from the identifier but it's just not working, this does. hence the garbage
					RenderType garbageHackPatchLayer = RenderTypes.armorCutoutNoCull(texture);
					if(config.getRenderSolidOverrideOrDefault(override, true)){
						int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

						//armor is literally always double-sided, the equipment renderer forces it to use double-sided.
						RenderType colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, true); //RenderLayerHelper.renderLayerFromIdentifierDoubleSided(texture, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, true);

						HijackedModel thickColorModel = ModelHelper.getThickenedModel(model, layer -> Shaders.COLOR_CUTOUT_LAYER, scale);

						queueHolder.order(getColorBatchingQueue()).submitModel(thickColorModel, s, matrixStack, colorLayer, Integer.MAX_VALUE, 0, tint, sprite, outlineColor, crumblingOverlayCommand);
					}
					else{
						RenderType glintZLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, true);

						HijackedModel thickGlintZModel = ModelHelper.getThickenedModel(model, layer -> Shaders.GLINT_CUTOUT_LAYER, scale);

						queueHolder.order(getGlintBatchingQueue()).submitModel(thickGlintZModel, s, matrixStack, glintZLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
						queueHolder.order(getGlintBatchingQueue()+1).submitModel(thickGlintZModel, s, matrixStack, Shaders.ARMOR_ENTITY_GLINT_FIX, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
					}
				}
			}
			return InteractionResult.PASS;
		}));

		TridentEntityRendererQueueEnchantedCallback.EVENT.register(((queueHolder, queue, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand) -> {
			if(config.isEnabled()){
				if(renderLayer.equals(RenderTypes.entityGlint())){
					@Nullable ItemOverride override = getOverrideFromNullableItem(config::getItemOverride, Items.TRIDENT);
					if(override == null || override.shouldRender()){
						float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, true));
						RenderType garbageHackPatchLayer = model.renderType(ThrownTridentRenderer.TRIDENT_LOCATION);
						if(config.getRenderSolidOverrideOrDefault(override, false)){
							int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

							RenderType colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, false);

							HijackedModel thickColorModel = ModelHelper.getThickenedModel(model, layer -> Shaders.COLOR_CUTOUT_LAYER, scale);

							queueHolder.order(getColorBatchingQueue()).submitModel(thickColorModel, s, matrixStack, colorLayer, Integer.MAX_VALUE, 0, tint, sprite, outlineColor, crumblingOverlayCommand);
						}
						else{
							int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

							RenderType glintZLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, false);

							HijackedModel thickGlintZModel = ModelHelper.getThickenedModel(model, layer -> Shaders.GLINT_CUTOUT_LAYER, scale);

							queueHolder.order(getGlintBatchingQueue()).submitModel(thickGlintZModel, s, matrixStack, glintZLayer, Integer.MAX_VALUE, 0, tint, sprite, outlineColor, crumblingOverlayCommand);
							queueHolder.order(getGlintBatchingQueue()+1).submitModel(thickGlintZModel, s, matrixStack, RenderTypes.entityGlint(), Integer.MAX_VALUE, 0, 0, sprite, outlineColor, crumblingOverlayCommand);
						}
					}
				}
			}
			return InteractionResult.PASS;
		}));

		//---------- End Render Calls ----------

		//---------- Render Layer Order ----------
		//26.2 removed MultiBufferSource/BufferSource entirely. There is no shared, ordered map of
		//RenderType -> ByteBufferBuilder to splice our layers into and no endBatch to force a flush.
		//FeatureRenderDispatcher now walks SubmitNodeStorage's order(int) keys in ascending order
		//within each phase (solid, then translucent, ...), and SubmitNodeCollection picks the phase
		//from the render type. Our cutout layers have no blend function, so they land in the solid
		//phase alongside the item, and order() alone gives the before/after placement we need.
		//That makes the whole buffer-splicing block below obsolete - it is gone, not ported.
		/* removed in 26.2:
		 *   ImmediateRenderCurrentLayer.Before/After  (endBatch interception)
		 *   BufferBuilderModifyReturnValue            (fixedBuffers rewriting)
		 *   WorldRendererFirstRenderMainPassCallback  (the 26.1 "jank render pass skip")
		 */

		//--------- End Render Layer Order ----------

		//---------- Item Type Storage ----------
		ItemModelManagerUpdateModelCallback.EVENT.register(((receiver, model, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed) -> {
			((ItemRenderStateAccessor)itemRenderState).enchantOutline$setItemRendered(itemStack.getItem());

			return InteractionResult.PASS;
		}));

		ItemRenderStateRenderLayerCallback.EVENT.register(((receiver, layerRenderState, matrices, orderedRenderCommandQueue, light, overlay, i) -> {
			((ItemRenderState_LayerRenderStateAccessor)layerRenderState).enchantOutline$setOwningItemRenderState(receiver);

			return InteractionResult.PASS;
		}));

		//set the item right before we lose the type
		LayerRenderStateRenderSpecial.Callback.EVENT.register(((receiver, specialModelRenderer, o, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
			LAYER_RENDER_STATE_RENDER_MODEL_STORAGE.set(receiver);
			SPECIAL_MODEL_COLLECTOR.set(orderedRenderCommandQueue);

			return InteractionResult.PASS;
		}));

		//clear the item right after calling
		LayerRenderStateRenderSpecial.Post.EVENT.register(((receiver, specialModelRenderer, o, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
			LAYER_RENDER_STATE_RENDER_MODEL_STORAGE.remove();
			SPECIAL_MODEL_COLLECTOR.remove();

			return InteractionResult.PASS;
		}));
		//---------- End Item Type Storage ----------

		//---------- Mod Patches ----------

		//--------- End Mod Patches ----------
	}

	/**
	 * 26.2: foil is no longer a boolean on the submit, it is simply "this model is being submitted with
	 * one of the vanilla glint render types". Covers every glint variant vanilla can hand us.
	 */
	private static boolean isGlintRenderType(RenderType renderType){
		return renderType == RenderTypes.entityGlint()
				|| renderType == RenderTypes.glint()
				|| renderType == RenderTypes.glintTranslucent()
				|| renderType == RenderTypes.armorEntityGlint();
	}

	@Nullable
	private static Item getItemFromLayerRenderState(ItemStackRenderState.LayerRenderState layerRenderState){
		@Nullable ItemStackRenderState owningState = ((ItemRenderState_LayerRenderStateAccessor)layerRenderState).enchantOutline$getOwningRenderState();
		if(owningState != null){
			return ((ItemRenderStateAccessor)owningState).enchantOutline$getItemRendered();
		}
		return null;
	}

	/**
	 * Builds the outline render type from whichever texture source we managed to recover - a render
	 * type when we know one, otherwise the sprite the glint submit carried, otherwise the fallback.
	 */
	private static RenderType outlineLayer(@Nullable RenderType textureSource, @Nullable TextureAtlasSprite sprite,
										   CustomRenderLayers layers,
										   java.util.function.Function<Map<String, Identifier>, RenderType> noCullFactory,
										   java.util.function.Function<Map<String, Identifier>, RenderType> cullFactory,
										   RenderType fallback, boolean isDoubleSided){
		if(textureSource != null){
			return RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(textureSource, layers, noCullFactory, cullFactory, fallback, isDoubleSided);
		}
		if(sprite != null){
			return RenderLayerHelper.renderLayerFromSpriteDoubleSided(sprite, layers, noCullFactory, cullFactory, fallback, isDoubleSided);
		}
		return fallback;
	}

	private static void initLayers(){
		GLINT_LAYERS.addCustomRenderLayer(Identifier.fromNamespaceAndPath(MOD_ID,"cutoutlayer").toString(), Shaders.GLINT_CUTOUT_LAYER);
		COLOR_LAYERS.addCustomRenderLayer(Identifier.fromNamespaceAndPath(MOD_ID,"cutoutlayer").toString(), Shaders.COLOR_CUTOUT_LAYER);
		ZFIX_LAYERS.addCustomRenderLayer(Identifier.fromNamespaceAndPath(MOD_ID, "cutoutlayer").toString(), Shaders.ZFIX_CUTOUT_LAYER);
	}

	@Nullable ItemOverride getOverrideFromLayerRenderState(Function<String, @Nullable ItemOverride> overrideGetter, ItemStackRenderState.LayerRenderState layerRenderState){
		@Nullable ItemStackRenderState owningState = ((ItemRenderState_LayerRenderStateAccessor)layerRenderState).enchantOutline$getOwningRenderState();
		if(owningState != null){
			@Nullable Item renderedItem = ((ItemRenderStateAccessor)owningState).enchantOutline$getItemRendered();
			return getOverrideFromNullableItem(overrideGetter, renderedItem);
		}
		return null;
	}

	@Nullable ItemOverride getOverrideFromNullableItem(Function<String, @Nullable ItemOverride> overrideGetter, @Nullable Item renderedItem){
		if(renderedItem != null){
			Identifier itemId = BuiltInRegistries.ITEM.getKey(renderedItem);
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