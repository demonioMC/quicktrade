package com.demonio.quicktrade.mixin.client;

import com.demonio.quicktrade.QuickTrade;
import com.demonio.quicktrade.QuickTradeButton;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin {

    @Unique
    private QuickTradeButton quickTradeButton;

    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo ci) {
        MerchantScreen screen = ((MerchantScreen) ((Object) this));

        int screenWidth = screen.width;
        int screenHeight = screen.height;

        int backgroundWidth = ((HandledScreenAccessor) this).getBackgroundWidth();
        int backgroundHeight = ((HandledScreenAccessor) this).getBackgroundHeight();

        int originX = (screenWidth - backgroundWidth) / 2;
        int originY = (screenHeight - backgroundHeight) / 2;

        int x = originX + 140;
        int y = originY + 50;
        int width = 100;
        int height = 20;

        quickTradeButton = new QuickTradeButton(
                x,
                y,
                width,
                height,
                Text.translatable("quicktrade.button.label"),
                button -> QuickTrade.LOGGER.info("Quick Trade button clicked")
        );

        this.addDrawableChild(screen, quickTradeButton);
    }

    @Inject(at = @At("TAIL"), method = "render")
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        quickTradeButton.visible = true;
    }

    @Unique
    private <T extends Element & Drawable & Selectable> void addDrawableChild(Screen screen, T drawableElement) {
        try {
            Method method = Screen.class.getDeclaredMethod("addDrawableChild", Element.class);
            method.setAccessible(true);
            method.invoke(screen, drawableElement);
        } catch (Exception e) {
            QuickTrade.LOGGER.error("Error in addCustomDrawableChild", e);
        }
    }
}
