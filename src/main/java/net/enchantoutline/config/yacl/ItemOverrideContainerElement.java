package net.enchantoutline.config.yacl;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemOverrideContainerElement extends ControllerWidget<ItemOverrideContainerController> implements ParentElement {
    private final ItemOverrideContainerController itemOverrideContainerController;

    private Element focused;
    private boolean dragging = false;

    private final AbstractWidget itemWidget;
    private final AbstractWidget renderWidget;
    private final AbstractWidget overrideSizeWidget;
    private final AbstractWidget sizeWidget;
    private final AbstractWidget overrideRenderSolidWidget;
    private final AbstractWidget renderSolidWidget;
    private final AbstractWidget overrideColorWidget;
    private final AbstractWidget colorWidget;

    public ItemOverrideContainerElement(ItemOverrideContainerController control, YACLScreen screen, Dimension<Integer> dim) {
        super(control, screen, dim);
        //I would change the size but again YACL is broken
        //Dimension<Integer> dim2 = dim.expanded(0, 40);
        //setDimension(dim2);
        this.itemOverrideContainerController = control;
        itemWidget = itemOverrideContainerController.getItemOption().controller().provideWidget(screen, dim);
        renderWidget = itemOverrideContainerController.getRenderOption().controller().provideWidget(screen, dim);
        overrideSizeWidget = itemOverrideContainerController.getOverrideSizeOption().controller().provideWidget(screen, dim);
        sizeWidget = itemOverrideContainerController.getSizeOption().controller().provideWidget(screen, dim);
        overrideRenderSolidWidget = itemOverrideContainerController.getOverrideRenderSolidOption().controller().provideWidget(screen, dim);
        renderSolidWidget = itemOverrideContainerController.getRenderSolidOption().controller().provideWidget(screen, dim);
        overrideColorWidget = itemOverrideContainerController.getOverrideColorOption().controller().provideWidget(screen, dim);
        colorWidget = itemOverrideContainerController.getColorOption().controller().provideWidget(screen, dim);
    }

    @Override
    protected int getHoveredControlWidth() {
        return getUnhoveredControlWidth();
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click mouseButtonEvent, boolean doubleClick) {
        return ParentElement.super.mouseClicked(mouseButtonEvent, doubleClick);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput keyEvent) {
        return ParentElement.super.keyPressed(keyEvent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        return ParentElement.super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click mouseButtonEvent, double dx, double dy) {
        return ParentElement.super.mouseDragged(mouseButtonEvent, dx, dy);
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        //TODO: change these equations whenever YACL gets fixed
        int xPos = getDimension().x();
        int width = getDimension().width()/8;
        int yPos = getDimension().y();
        int height = getDimension().height();
        itemWidget.setDimension(itemWidget.getDimension().withY(yPos).withX(xPos).withWidth((int)(width*1.8)).withHeight(height));
        xPos+= (int) (width*1.8);
        renderWidget.setDimension(renderWidget.getDimension().withY(yPos).withX(xPos).withWidth((int)(width/1.7)).withHeight(height));
        //xPos+= (int) (width/1.5);
        xPos+= (int) (width/1.7);
        overrideSizeWidget.setDimension(overrideSizeWidget.getDimension().withY(yPos).withX(xPos).withWidth((int)(width)).withHeight(height/2));
        sizeWidget.setDimension(sizeWidget.getDimension().withY(yPos+height/2).withX(xPos).withWidth(width).withHeight(height/2));
        xPos+=width;
        overrideRenderSolidWidget.setDimension(overrideRenderSolidWidget.getDimension().withY(yPos).withX(xPos).withWidth((int)(width/1.7)).withHeight(height/2));
        renderSolidWidget.setDimension(renderSolidWidget.getDimension().withY(yPos+height/2).withX(xPos).withWidth((int)(width/1.7)).withHeight(height/2));
        xPos+= (int) (width/1.7);
        overrideColorWidget.setDimension(overrideColorWidget.getDimension().withY(yPos).withX(xPos).withWidth((int)(width/1.7)).withHeight(height));
        colorWidget.setDimension(overrideColorWidget.getDimension().withY(yPos).withX(getDimension().x()).withWidth((int)(width*8.1)).withHeight(height));

        colorWidget.render(graphics, mouseX, mouseY, delta);
        itemWidget.render(graphics, mouseX, mouseY, delta);
        renderWidget.render(graphics, mouseX, mouseY, delta);
        overrideSizeWidget.render(graphics, mouseX, mouseY, delta);
        sizeWidget.render(graphics, mouseX, mouseY, delta);
        overrideRenderSolidWidget.render(graphics, mouseX, mouseY, delta);
        renderSolidWidget.render(graphics, mouseX, mouseY, delta);
        overrideColorWidget.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(itemWidget, renderWidget, overrideSizeWidget, overrideRenderSolidWidget, renderSolidWidget, overrideColorWidget, sizeWidget, colorWidget);
    }

    @Override
    public void unfocus() {
        itemWidget.unfocus();
        renderWidget.unfocus();
        overrideSizeWidget.unfocus();
        sizeWidget.unfocus();
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public @Nullable Element getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        this.focused = focused;
    }
}
