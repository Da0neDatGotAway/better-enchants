package net.enchantoutline.mixin_accessors;

import net.minecraft.client.renderer.rendertype.RenderType;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.SequencedMap;

public interface MultiBufferSource_BufferSourceAccessor {
    public abstract SequencedMap<RenderType, ByteBufferBuilder> enchantOutline$getLayerBuffers();
    public abstract Map<Object, Integer> enchantOutline$getDirtyMap();
    @Nullable
    public abstract Integer enchantOutline$getDirty(Object o);
    public abstract void enchantOutline$setDirty(Object o, int newDirty);
}
