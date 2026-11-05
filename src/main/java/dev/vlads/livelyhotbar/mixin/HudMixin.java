package dev.vlads.livelyhotbar.mixin;

import dev.vlads.livelyhotbar.LivelyHotbar;
import dev.vlads.livelyhotbar.SelectionSlide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Slides the hotbar selection frame. Vanilla draws it at x = guiWidth/2 - 92 + selectedSlot * 20; this mixin adds
 * the animation's remaining offset to that x in the frame's blit (the second blitSprite of extractItemHotbar).
 */
@Mixin(Hud.class)
public abstract class HudMixin {
    @Unique
    private final SelectionSlide livelyHotbar$slide = new SelectionSlide();
    @Unique
    private float livelyHotbar$offset;

    @Inject(method = "extractItemHotbar", at = @At("HEAD"))
    private void livelyHotbar$beforeHotbar(CallbackInfo ci) {
        livelyHotbar$offset = 0;
        if (!LivelyHotbar.config.enabled) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        int selected = minecraft.player.getInventory().getSelectedSlot();
        livelyHotbar$offset = livelyHotbar$slide.update(selected, Util.getMillis(), LivelyHotbar.config);
    }

    @ModifyArg(
            method = "extractItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1),
            index = 2)
    private int livelyHotbar$shiftSelection(int x) {
        return x + Math.round(livelyHotbar$offset);
    }
}
