package net.enchantoutline.mixin;

import net.enchantoutline.mixin_accessors.TextureAtlasSpriteAccessor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextureAtlasSprite.class)
public class TextureAtlasSpriteMixin implements TextureAtlasSpriteAccessor {

    @Shadow
    private Identifier atlasLocation;

    @Override
    public Identifier enchantOutline$getAtlasLocation() {
        return atlasLocation;
    }
}
