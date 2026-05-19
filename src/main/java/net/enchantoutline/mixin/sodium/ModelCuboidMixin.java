package net.enchantoutline.mixin.sodium;

import net.caffeinemc.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.enchantoutline.util.ModelHelper;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ModelCuboid.class)

public class ModelCuboidMixin {
    @Shadow
    @Final
    @Mutable
    public long[] textures;

    @Shadow
    @Final
    @Mutable
    public int[] positions;

    @Inject(method = "<init>", at = @At("RETURN"))
    void enchantOutline$flipOnInit(int u, int v, float x1, float y1, float z1, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> renderDirections, CallbackInfo ci){
        if(ModelHelper.FLIP_CUBOIDS.get()){
            enchantOutline$reverseVertices(positions, textures);
        }
    }

    @Invoker("reverseVertices")
    private static void enchantOutline$reverseVertices(int[] vertices, long[] texCoords) {

    }
}
