package net.enchantoutline.config;

import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ItemOverrideContainer {
    private String item;
    private final ItemOverride override;

    public ItemOverrideContainer(){
        this(BuiltInRegistries.ITEM.getKey(null).toString(), new ItemOverride());
    }
    public ItemOverrideContainer(ItemOverrideContainer from) {
        item = from.item;
        override = new ItemOverride(from.override);
    }

    public ItemOverrideContainer(String item, ItemOverride override){
        this.item = item;
        this.override = override;
    }

    public void setItemString(String item){
        this.item = item;
    }

    public String getItemString(){return item;}

    public void setItem(Item item){
        this.item = BuiltInRegistries.ITEM.getKey(item).toString();
    }

    public Item getItem(){
        return BuiltInRegistries.ITEM.getValue(Identifier.parse(item));
    }

    public ItemOverride getItemOverride(){
        return override;
    }
}
