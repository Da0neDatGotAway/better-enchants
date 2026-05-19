package net.enchantoutline.config.yacl;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.StateManager;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.ItemControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl3.impl.controller.ColorControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.FloatFieldControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import net.enchantoutline.config.EnchantmentOutlineConfig;
import net.enchantoutline.config.ItemOverrideContainer;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.awt.*;

public class ItemOverrideContainerController implements Controller<ItemOverrideContainer> {
    private final Option<ItemOverrideContainer> option;
    private final Option<Item> itemOption;
    private final Option<Boolean> renderOption;
    private final Option<Boolean> overrideSizeOption;
    private final Option<Float> sizeOption;
    private final Option<Boolean> overrideRenderSolidOption;
    private final Option<Boolean> renderSolidOption;
    private final Option<Boolean> overrideColorOption;
    private final Option<Color> colorOption;

    public ItemOverrideContainerController(Option<ItemOverrideContainer> option, ItemOverrideContainer defaultItemOverrideContainer){
        this.option = option;
        itemOption = Option.<Item>createBuilder()
                .name(Text.empty())
                //as much as I want the names YACL is broken ATM so I can't : (
                //.name(Text.translatable("controller.enchantoutline.itemoverride.item"))
                //.description(OptionDescription.of(Text.translatable("tooltip.controller.enchantoutline.itemoverride.item")))
                .stateManager(StateManager.createInstant(defaultItemOverrideContainer.getItem(),() -> {return option.pendingValue().getItem();},
                                (item) -> {
                                    ItemOverrideContainer pending = new ItemOverrideContainer(option.pendingValue());
                                    pending.setItem(item);
                                    option.requestSet(pending);}))
                .controller(ItemControllerBuilder::create)
                .build();
        renderOption = Option.<Boolean>createBuilder()
                .name(Text.empty())
                .stateManager(StateManager.createInstant(defaultItemOverrideContainer.getItemOverride().shouldRender(), () -> {return option.pendingValue().getItemOverride().shouldRender();},
                        (render) -> {
                            ItemOverrideContainer pending = new ItemOverrideContainer(option.pendingValue());
                            pending.getItemOverride().setRender(render);
                            option.requestSet(pending);
                        }))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
        overrideSizeOption = Option.<Boolean>createBuilder()
                .name(Text.empty())
                .stateManager(StateManager.createInstant(defaultItemOverrideContainer.getItemOverride().shouldOverrideOutlineSize(), () -> {return option.pendingValue().getItemOverride().shouldOverrideOutlineSize();},
                        (override) -> {
                            ItemOverrideContainer pending = new ItemOverrideContainer(option.pendingValue());
                            pending.getItemOverride().setOverrideOutlineSize(override);
                            option.requestSet(pending);
                        }))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
        //TODO: when YACL gets a fix update this to be a slider
        //it had to be a slider anyway despite being tiny because you can't edit float fields for some reason YACL why
        sizeOption = Option.<Float>createBuilder()
                .name(Text.empty())
                .stateManager(StateManager.createInstant(defaultItemOverrideContainer.getItemOverride().getOutlineSize(), () -> {return option.pendingValue().getItemOverride().getOutlineSize();},
                        (size) -> {
                            ItemOverrideContainer pending = new ItemOverrideContainer(option.pendingValue());
                            pending.getItemOverride().setOutlineSize(size);
                            option.requestSet(pending);
                        }))
                .controller((opt) -> FloatSliderControllerBuilder.create(opt)
                        .range(0f, EnchantmentOutlineConfig.MAX_OUTLINE_SIZE)
                        .step(1f)
                        .formatValue(new FloatValueFormatter(0)))
                .build();
        overrideRenderSolidOption = Option.<Boolean>createBuilder()
                .name(Text.empty())
                .stateManager(StateManager.createInstant(defaultItemOverrideContainer.getItemOverride().shouldOverrideRenderSolid(), () -> {return option.pendingValue().getItemOverride().shouldOverrideRenderSolid();},
                        (override) -> {
                            ItemOverrideContainer pending = new ItemOverrideContainer(option.pendingValue());
                            pending.getItemOverride().setOverrideRenderSolid(override);
                            option.requestSet(pending);
                        }))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
        renderSolidOption = Option.<Boolean>createBuilder()
                .name(Text.empty())
                .stateManager(StateManager.createInstant(defaultItemOverrideContainer.getItemOverride().shouldRenderSolid(), () -> {return option.pendingValue().getItemOverride().shouldRenderSolid();},
                        (solid) -> {
                            ItemOverrideContainer pending = new ItemOverrideContainer(option.pendingValue());
                            pending.getItemOverride().setRenderSolid(solid);
                            option.requestSet(pending);
                        }))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
        overrideColorOption = Option.<Boolean>createBuilder()
                .name(Text.empty())
                .stateManager(StateManager.createInstant(defaultItemOverrideContainer.getItemOverride().shouldOverrideRenderSolidOutlineColor(), () -> {return option.pendingValue().getItemOverride().shouldOverrideRenderSolidOutlineColor();},
                        (override) -> {
                            ItemOverrideContainer pending = new ItemOverrideContainer(option.pendingValue());
                            pending.getItemOverride().setOverrideRenderSolidOutlineColor(override);
                            option.requestSet(pending);
                        }))
                .controller(TickBoxControllerBuilderImpl::new)
                .build();
        colorOption = Option.<Color>createBuilder()
                .name(Text.empty())
                .stateManager(StateManager.createInstant(EnchantmentOutlineConfig.getColorFromInt(defaultItemOverrideContainer.getItemOverride().getRenderSolidOutlineColor()), () -> {return EnchantmentOutlineConfig.getColorFromInt(option.pendingValue().getItemOverride().getRenderSolidOutlineColor());},
                        (color) -> {
                            ItemOverrideContainer pending = new ItemOverrideContainer(option.pendingValue());
                            pending.getItemOverride().setRenderSolidOutlineColor(EnchantmentOutlineConfig.getIntFromColor(color));
                            option.requestSet(pending);
                        }))
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                .build();
    }

    @Override
    public Option<ItemOverrideContainer> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return Text.literal(option.toString());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new ItemOverrideContainerElement(this, screen, widgetDimension);
    }

    public Option<Item>getItemOption(){
        return itemOption;
    }

    public Option<Boolean>getRenderOption(){
        return renderOption;
    }

    public Option<Boolean>getOverrideSizeOption(){
        return overrideSizeOption;
    }

    public Option<Float>getSizeOption(){
        return sizeOption;
    }

    public Option<Boolean>getOverrideRenderSolidOption(){
        return overrideRenderSolidOption;
    }

    public Option<Boolean>getRenderSolidOption(){
        return renderSolidOption;
    }

    public Option<Boolean>getOverrideColorOption(){
        return overrideColorOption;
    }

    public Option<Color>getColorOption(){
        return colorOption;
    }
}
