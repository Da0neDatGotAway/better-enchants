package net.enchantoutline.config.yacl;

import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.minecraft.text.Text;

public record FloatValueFormatter(int decimalPlaces) implements ValueFormatter<Float> {
    @Override
    public Text format(Float value) {
        return Text.literal(String.format("%." + decimalPlaces + "f", value));
    }
}
