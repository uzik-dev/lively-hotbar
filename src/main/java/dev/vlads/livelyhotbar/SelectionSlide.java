package dev.vlads.livelyhotbar;

/**
 * Remembers which hotbar slot was selected last frame and, after a change to a neighbouring slot, gives the
 * horizontal offset (in GUI pixels) the selection frame should still be drawn at. Offset 0 means at the slot.
 */
public final class SelectionSlide {
    /** Vanilla slot pitch in GUI pixels. */
    public static final int SLOT_PITCH = 20;
    /** How many slides have started since launch; read by tests. */
    public static int slidesStarted;

    private int lastSlot = -1;
    private int fromSlotDelta;
    private long start;
    private boolean sliding;

    /** Call once per frame with the selected slot. Returns the x offset to add to the selection frame. */
    public float update(int selectedSlot, long now, LivelyHotbarConfig config) {
        if (lastSlot < 0) {
            lastSlot = selectedSlot;
            return 0;
        }
        int delta = selectedSlot - lastSlot;
        lastSlot = selectedSlot;
        // Wrapping from slot 9 back to slot 1 would sweep across the whole bar: snap instead.
        if (delta != 0 && config.slide && Math.abs(delta) == 1) {
            fromSlotDelta = -delta;
            start = now;
            sliding = true;
            slidesStarted++;
        } else if (delta != 0) {
            sliding = false;
        }
        if (!sliding) return 0;

        float progress = config.slideDurationMs <= 0 ? 1 : (now - start) / (float) config.slideDurationMs;
        if (progress >= 1) {
            sliding = false;
            return 0;
        }
        float eased = config.bounce ? easeOutBack(progress) : easeOutCubic(progress);
        return fromSlotDelta * SLOT_PITCH * (1 - eased);
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        float u = t - 1;
        return 1 + c3 * u * u * u + c1 * u * u;
    }

    private static float easeOutCubic(float t) {
        float u = 1 - t;
        return 1 - u * u * u;
    }
}
