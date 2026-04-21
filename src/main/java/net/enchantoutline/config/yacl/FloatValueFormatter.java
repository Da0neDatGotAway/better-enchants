package net.enchantoutline.config.yacl;

import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.minecraft.network.chat.Component;

public record FloatValueFormatter(int decimalPlaces) implements ValueFormatter<Float> {
    @Override
    public Component format(Float value) {
        return Component.literal(String.format("%." + decimalPlaces + "f", value));
    }
}
