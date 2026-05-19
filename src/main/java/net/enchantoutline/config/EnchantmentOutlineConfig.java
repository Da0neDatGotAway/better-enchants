package net.enchantoutline.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.enchantoutline.EnchantmentGlintOutline;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Taken from webspeak
 */
public class EnchantmentOutlineConfig {
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("enchantment-glint-outline.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final float MAX_OUTLINE_SIZE = 30;

    public boolean enabled = true;
    public float outline_size = 20;
    public boolean render_solid = false;
    public int[] render_solid_outline_color_rgb = {210,150,248};
    public boolean render_equipment = true;
    public float equipment_outline_size = 20;
    public boolean render_equipment_solid = false;
    public Map<String, ItemOverride> item_overrides = new HashMap<>();
    public Map<String, ItemOverride> equipment_overrides = new HashMap<>();

    public Map<String, ItemOverride> overrides;

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setOutlineSize(float outlineSize){
        this.outline_size = outlineSize;
    }

    public float getOutlineSize(){
        return outline_size;
    }

    public void setRenderSolid(boolean renderSolid){
        this.render_solid = renderSolid;
    }

    public boolean shouldRenderSolid(){
        return render_solid;
    }

    public int[] getOutlineColor()
    {
        return render_solid_outline_color_rgb;
    }

    public void setBaseSolidOutlineColor(int[] color){
        render_solid_outline_color_rgb = color;
    }

    public void setBaseSolidOutlineColorAsInt(int color)
    {
        color = ColorHelper.withAlpha(255, color);
        int[] newOutlineColor = new int[3];
        newOutlineColor[0] = ColorHelper.getRed(color);
        newOutlineColor[1] = ColorHelper.getGreen(color);
        newOutlineColor[2] = ColorHelper.getBlue(color);
        render_solid_outline_color_rgb = newOutlineColor;
    }

    public int getOutlineColorAsInt(int[] outlineColorInt)
    {
        if(outlineColorInt.length < 3){
            return -1;
        }
        return ColorHelper.withAlpha(255,ColorHelper.getArgb((outlineColorInt[0]), (outlineColorInt[1]), (outlineColorInt[2])));
    }

    public void setRenderArmor(boolean renderArmor){
        this.render_equipment = renderArmor;
    }

    public boolean shouldRenderArmor(){
        return render_equipment;
    }

    public void setArmorOutlineSize(float armorOutlineSize){
        this.equipment_outline_size = armorOutlineSize;
    }

    public float getArmorOutlineSize(){
        return equipment_outline_size;
    }

    public void setRenderArmorSolid(boolean renderArmorSolid){
        this.render_equipment_solid = renderArmorSolid;
    }

    public boolean shouldRenderArmorSolid(){
        return render_equipment_solid;
    }

    public void setItemOverrides(Map<String, ItemOverride> item_overrides){
        this.item_overrides = item_overrides;
    }

    public Map<String, ItemOverride> getItemOverrides(){
        return item_overrides;
    }

    public void setItemOverridesFromContainerList(List<ItemOverrideContainer> overrideList){
        Map<String, ItemOverride> newOverrides = new HashMap<>(overrideList.size());
        for(ItemOverrideContainer itemOverrideContainer : overrideList){
            newOverrides.put(itemOverrideContainer.getItemString(), itemOverrideContainer.getItemOverride());
        }
        setItemOverrides(newOverrides);
    }

    public List<ItemOverrideContainer> getItemOverridesAsContainerList(){
        List<ItemOverrideContainer> overrideList = new ArrayList<>(getItemOverrides().size());
        for(var set : getItemOverrides().entrySet()){
            overrideList.add(new ItemOverrideContainer(set.getKey(), set.getValue()));
        }
        return overrideList;
    }

    public void setArmorOverrides(Map<String, ItemOverride> armor_overrides){
        this.equipment_overrides = armor_overrides;
    }

    public Map<String, ItemOverride> getArmorOverrides(){
        return equipment_overrides;
    }

    public void setArmorOverridesFromContainerList(List<ItemOverrideContainer> overrideList){
        Map<String, ItemOverride> newOverrides = new HashMap<>(overrideList.size());
        for(ItemOverrideContainer itemOverrideContainer : overrideList){
            newOverrides.put(itemOverrideContainer.getItemString(), itemOverrideContainer.getItemOverride());
        }
        setArmorOverrides(newOverrides);
    }

    public List<ItemOverrideContainer> getArmorOverridesAsContainerList(){
        List<ItemOverrideContainer> overrideList = new ArrayList<>(getArmorOverrides().size());
        for(var set : getArmorOverrides().entrySet()){
            overrideList.add(new ItemOverrideContainer(set.getKey(), set.getValue()));
        }
        return overrideList;
    }

    @Nullable
    public ItemOverride getItemOverride(Object item){
        return item_overrides.get(item);
    }

    @Nullable
    public ItemOverride getArmorOverride(Object item){
        return equipment_overrides.get(item);
    }

    public static int[] getIntFromColor(Color color){
        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
    }

    public static Color getColorFromInt(int[] intColor){
        return new Color(intColor[0], intColor[1], intColor[2], 255);
    }

    public float getScaleFactorFromOutlineSize(float outlineSize){
        return outlineSize/1000f;
    }

    public float getOutlineSizeOverrideOrDefault(ItemOverride override, boolean armor){
        if(override != null){
            if(override.shouldOverrideOutlineSize()){
                return override.getOutlineSize();
            }
        }
        return armor ? getArmorOutlineSize() : getOutlineSize();
    }

    public boolean getRenderSolidOverrideOrDefault(ItemOverride override, boolean armor){
        if(override != null){
            if(override.shouldOverrideRenderSolid()){
                return override.shouldRenderSolid();
            }
        }
        return armor ? shouldRenderArmorSolid() : shouldRenderSolid();
    }

    public int[] getOutlineColorOverrideOrDefault(ItemOverride override){
        if(override != null){
            if(override.shouldOverrideRenderSolidOutlineColor()){
                return override.getRenderSolidOutlineColor();
            }
        }
        return getOutlineColor();
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static EnchantmentOutlineConfig fromJson(String json) {
        return GSON.fromJson(json, EnchantmentOutlineConfig.class);
    }

    public static EnchantmentOutlineConfig fromJson(Reader reader) {
        return GSON.fromJson(reader, EnchantmentOutlineConfig.class);
    }

    /**
     * Asynchronously save the Enchantment Glint Outline config to file.
     * @return A future that completes when the config is saved.
     */
    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(() -> {
            try(BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE)) {
                writer.write(toJson());
            } catch (Exception e) {
                EnchantmentGlintOutline.LOGGER.error("Error saving Enchant Glint Outline config.", e);
                throw new CompletionException(e);
            }
        });
    }
}
