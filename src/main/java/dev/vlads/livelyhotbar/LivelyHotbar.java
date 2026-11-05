package dev.vlads.livelyhotbar;

import net.fabricmc.api.ClientModInitializer;

public class LivelyHotbar implements ClientModInitializer {
    public static final String MOD_ID = "lively_hotbar";
    public static LivelyHotbarConfig config = new LivelyHotbarConfig();

    @Override
    public void onInitializeClient() {
        config = LivelyHotbarConfig.load();
    }
}
