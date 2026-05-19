package net.enchantoutline.config.yacl;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.ItemControllerBuilder;
import dev.isxander.yacl3.impl.controller.BooleanControllerBuilderImpl;
import net.enchantoutline.EnchantmentGlintOutline;
import net.enchantoutline.config.EnchantmentOutlineConfig;
import net.enchantoutline.config.ItemOverrideContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;

public class YACLScreenFactory {
    private static final EnchantmentOutlineConfig defaultConfig = new EnchantmentOutlineConfig();
    public static Screen makeConfig(Screen parent)
    {
        YetAnotherConfigLib configScreen = YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("title.enchantoutline.config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("category.enchantoutline.main"))
                        .tooltip(Text.translatable("tooltip.category.enchantoutline.main"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("option.enchantoutline.rendermain"))
                                .description(OptionDescription.of(Text.translatable("tooltip.option.enchantoutline.rendermain")))
                                .binding(defaultConfig.isEnabled(), EnchantmentGlintOutline.getConfig()::isEnabled, EnchantmentGlintOutline.getConfig()::setEnabled)
                                .controller(BooleanControllerBuilderImpl::new)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.translatable("option.enchantoutline.solidcolor"))
                                .description(OptionDescription.of(Text.translatable("tooltip.option.enchantoutline.solidcolor").append(Text.translatable("tooltip.error.yamlcolorissue").formatted(Formatting.YELLOW))))
                                .binding(EnchantmentOutlineConfig.getColorFromInt(defaultConfig.getOutlineColor()), () -> EnchantmentOutlineConfig.getColorFromInt(EnchantmentGlintOutline.getConfig().getOutlineColor()), (color) -> {EnchantmentGlintOutline.getConfig().setBaseSolidOutlineColor(EnchantmentOutlineConfig.getIntFromColor(color));})
                                .controller(opt -> ColorControllerBuilder.create(opt)
                                        .allowAlpha(true))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("group.enchantoutline.item"))
                                .description(OptionDescription.of(Text.translatable("tooltip.category.enchantoutline.item")))
                                .option(Option.<Float>createBuilder()
                                        .name(Text.translatable("option.item.enchantoutline.size"))
                                        .description(OptionDescription.of(Text.translatable("tooltip.option.item.enchantoutline.size")))
                                        .binding(defaultConfig.getOutlineSize(), EnchantmentGlintOutline.getConfig()::getOutlineSize, EnchantmentGlintOutline.getConfig()::setOutlineSize)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(0f, EnchantmentOutlineConfig.MAX_OUTLINE_SIZE)
                                                .step(1f)
                                                .formatValue(new FloatValueFormatter(0)))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("option.item.enchantoutline.rendersolid"))
                                        .description(OptionDescription.of(Text.translatable("tooltip.option.item.enchantoutline.rendersolid")))
                                        .binding(defaultConfig.shouldRenderSolid(), EnchantmentGlintOutline.getConfig()::shouldRenderSolid, EnchantmentGlintOutline.getConfig()::setRenderSolid)
                                        .controller(BooleanControllerBuilderImpl::new)
                                        .build())
                                .build())
                        .group(ListOption.<ItemOverrideContainer>createBuilder()
                                .name(Text.translatable("list.enchantoutline.override.item"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.translatable("tooltip.list.enchantoutline.override.item"))
                                        .webpImage(Identifier.of(EnchantmentGlintOutline.MOD_ID, "textures/listexplained.webp"))
                                        .build())
                                .binding(defaultConfig.getItemOverridesAsContainerList(), EnchantmentGlintOutline.getConfig()::getItemOverridesAsContainerList, EnchantmentGlintOutline.getConfig()::setItemOverridesFromContainerList)
                                .controller(ItemOverrideContainerControllerBuilderImpl::new)
                                .initial(ItemOverrideContainer::new)
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("group.enchantoutline.equipment"))
                                .description(OptionDescription.of(Text.translatable("tooltip.category.enchantoutline.equipment")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("option.equipment.enchantoutline.render"))
                                        .description(OptionDescription.of(Text.translatable("tooltip.option.equipment.enchantoutline.render")))
                                        .binding(defaultConfig.shouldRenderArmor(), EnchantmentGlintOutline.getConfig()::shouldRenderArmor, EnchantmentGlintOutline.getConfig()::setRenderArmor)
                                        .controller(BooleanControllerBuilderImpl::new)
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.translatable("option.equipment.enchantoutline.size"))
                                        .description(OptionDescription.of(Text.translatable("tooltip.option.equipment.enchantoutline.size")))
                                        .binding(defaultConfig.getArmorOutlineSize(), EnchantmentGlintOutline.getConfig()::getArmorOutlineSize, EnchantmentGlintOutline.getConfig()::setArmorOutlineSize)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(0f, EnchantmentOutlineConfig.MAX_OUTLINE_SIZE)
                                                .step(1f)
                                                .formatValue(new FloatValueFormatter(0)))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("option.equipment.enchantoutline.rendersolid"))
                                        .description(OptionDescription.of(Text.translatable("tooltip.option.equipment.enchantoutline.rendersolid")))
                                        .binding(defaultConfig.shouldRenderArmorSolid(), EnchantmentGlintOutline.getConfig()::shouldRenderArmorSolid, EnchantmentGlintOutline.getConfig()::setRenderArmorSolid)
                                        .controller(BooleanControllerBuilderImpl::new)
                                        .build())
                                .build())
                        .group(ListOption.<ItemOverrideContainer>createBuilder()
                                .name(Text.translatable("list.enchantoutline.override.equipment"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.translatable("tooltip.list.enchantoutline.override.equipment"))
                                        .webpImage(Identifier.of(EnchantmentGlintOutline.MOD_ID, "textures/listexplained.webp"))
                                        .build())
                                .binding(defaultConfig.getArmorOverridesAsContainerList(), EnchantmentGlintOutline.getConfig()::getArmorOverridesAsContainerList, EnchantmentGlintOutline.getConfig()::setArmorOverridesFromContainerList)
                                .controller(ItemOverrideContainerControllerBuilderImpl::new)
                                .initial(ItemOverrideContainer::new)
                                .build())
                        .build())
                .save(EnchantmentGlintOutline.getConfig()::saveAsync)
                .build();

        return configScreen.generateScreen(parent);
    }
}
