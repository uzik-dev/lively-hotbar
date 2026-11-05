package dev.vlads.livelyhotbar;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Settings in config/lively_hotbar.properties; missing keys are written back with their defaults. */
public final class LivelyHotbarConfig {
    private static final Logger LOG = LoggerFactory.getLogger(LivelyHotbar.MOD_ID);

    public boolean enabled = true;
    /** The selection frame slides to the neighbouring slot instead of jumping. */
    public boolean slide = true;
    public int slideDurationMs = 150;
    /** Ease with a small overshoot past the target slot, so the frame settles with a little spring. */
    public boolean bounce = true;

    public static LivelyHotbarConfig load() {
        LivelyHotbarConfig config = new LivelyHotbarConfig();
        Path file = FabricLoader.getInstance().getConfigDir().resolve(LivelyHotbar.MOD_ID + ".properties");
        Properties props = new Properties();
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                props.load(reader);
            } catch (IOException e) {
                LOG.warn("Could not read {}, using defaults", file, e);
            }
        }
        config.enabled = bool(props, "enabled", config.enabled);
        config.slide = bool(props, "slide", config.slide);
        config.slideDurationMs = integer(props, "slideDurationMs", config.slideDurationMs);
        config.bounce = bool(props, "bounce", config.bounce);
        try (Writer writer = Files.newBufferedWriter(file)) {
            props.store(writer, "Lively Hotbar. Durations are in milliseconds.");
        } catch (IOException e) {
            LOG.warn("Could not write {}", file, e);
        }
        return config;
    }

    private static boolean bool(Properties props, String key, boolean fallback) {
        String value = props.getProperty(key);
        if (value == null) {
            props.setProperty(key, Boolean.toString(fallback));
            return fallback;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static int integer(Properties props, String key, int fallback) {
        String value = props.getProperty(key);
        try {
            if (value != null) return Math.max(0, Integer.parseInt(value.trim()));
        } catch (NumberFormatException ignored) {
        }
        props.setProperty(key, Integer.toString(fallback));
        return fallback;
    }
}
