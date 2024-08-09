package com.demonio.quicktrade;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import com.mojang.blaze3d.systems.RenderSystem;

public class QuickTradeButton extends ButtonWidget {

    public QuickTradeButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(
                x, y, width, height,
                message,
                onPress,
                (buttonWidget) -> Text.translatable("quicktrade.button.narration")
        );
    }
}
