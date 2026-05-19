package net.enchantoutline.mixin_accessors;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.BufferAllocator;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.SequencedMap;

public interface VertexConsumerProvider_ImmediateAccessor {
    public abstract SequencedMap<RenderLayer, BufferAllocator> enchantOutline$getLayerBuffers();
    public abstract Map<Object, Integer> enchantOutline$getDirtyMap();
    @Nullable
    public abstract Integer enchantOutline$getDirty(Object o);
    public abstract void enchantOutline$setDirty(Object o, int newDirty);
}
