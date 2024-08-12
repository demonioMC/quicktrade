package com.demonio.quicktrade.mixin.client;

import com.demonio.quicktrade.QuickTrade;
import com.demonio.quicktrade.QuickTradeButton;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {

    @Unique
    private QuickTradeButton quickTradeButton;

    @Unique
    private MerchantScreen screen;

    @Unique
    private MerchantScreenHandler handler;

    @Unique
    private final PlayerEntity player;

    public MerchantScreenMixin(MerchantScreenHandler screenHandler, PlayerInventory playerInventory, Text title) {
        super(screenHandler, playerInventory, title);

        player = playerInventory.player;
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void afterConstructor(MerchantScreenHandler screenHandler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        screen = ((MerchantScreen) ((Object) this));
        handler = screen.getScreenHandler();
    }

    @Unique
    private void doTradeOnce() {
        assert client != null;
        assert client.interactionManager != null;

        client.interactionManager.clickSlot(this.handler.syncId, 2, 0, SlotActionType.QUICK_MOVE, client.player);
    }

    @Unique
    private void doQuickTrade() {
        int selectedTradeOfferIndex = ((MerchantScreenAccessor) screen).getSelectedIndex();

        while (this.isValidTrade()) {
            doTradeOnce();
            handler.switchTo(selectedTradeOfferIndex);
        }
    }

    @Unique
    private boolean isValidTrade() {
        Slot outputSlot = handler.getSlot(2);

        boolean hasOutputSlotStack = handler.getSlot(2).hasStack();
        boolean canTakeItems = outputSlot.canTakeItems(this.player);
        boolean isVillagerLeveled = handler.isLeveled();

        return hasOutputSlotStack && canTakeItems && isVillagerLeveled;
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void drawButton(CallbackInfo ci) {
        int screenWidth = screen.width;
        int screenHeight = screen.height;

        int backgroundWidth = ((HandledScreenAccessor) screen).getBackgroundWidth();
        int backgroundHeight = ((HandledScreenAccessor) screen).getBackgroundHeight();

        int originX = (screenWidth - backgroundWidth) / 2;
        int originY = (screenHeight - backgroundHeight) / 2;

        int x = originX + 125;
        int y = originY + 55;
        int width = 64;
        int height = 14;

        quickTradeButton = new QuickTradeButton(
                x,
                y,
                width,
                height,
                Text.translatable("quicktrade.button.label"),
                button -> doQuickTrade()
        );

        quickTradeButton.visible = false;

        addDrawableChild(screen, quickTradeButton);
    }

    @Inject(at = @At("TAIL"), method = "render")
    private void toggleButtonVisibility(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        quickTradeButton.visible = isValidTrade();
    }

    @Unique
    private <T extends Element & Drawable & Selectable> void addDrawableChild(Screen screen, T drawableElement) {
        try {
            Method method = Screen.class.getDeclaredMethod("addDrawableChild", Element.class);
            method.setAccessible(true);
            method.invoke(screen, drawableElement);
        } catch (Exception e) {
            QuickTrade.LOGGER.error("Error in custom addDrawableChild", e);
        }
    }
}
