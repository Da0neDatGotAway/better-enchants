package net.enchantoutline.config;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemOverrideContainer {
    private String item;
    private final ItemOverride override;

    public ItemOverrideContainer(){
        this(Registries.ITEM.getId(null).toString(), new ItemOverride());
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
        this.item = Registries.ITEM.getId(item).toString();
    }

    public Item getItem(){
        return Registries.ITEM.get(Identifier.of(item));
    }

    public ItemOverride getItemOverride(){
        return override;
    }
}
