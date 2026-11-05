"""Builds the demo GIF from the frames LivelyHotbarGameTest took (port-tests).

The test sets slideDurationMs=600 and takes one frame per tick through the slide, so the slide is slowed 4 times.
Each frame is shown for 50 / 4 ms, so the GIF plays at the real speed of the default 150 ms slide.
The hotbar is cropped by fractions of the window, so the frames must be 854x480 or the same proportions.

Usage: python tools/make_hotbar_gif.py <screenshots dir> <output.gif>   (needs Pillow)
"""
import sys
from pathlib import Path

from PIL import Image

SLOWDOWN = 4
TICK_MS = 50
UPSCALE = 3
# Hotbar strip as fractions of the window: x from 27% to 73%, y from 87% to 96%.
CROP = (0.27, 0.87, 0.73, 0.96)


def main():
    frames_dir, out = Path(sys.argv[1]), sys.argv[2]
    paths = sorted(frames_dir.glob("*_hb_*.png"))
    if not paths:
        sys.exit(f"no hb_*.png frames in {frames_dir}")
    frames = []
    for p in paths:
        image = Image.open(p).convert("RGB")
        w, h = image.size
        box = (int(CROP[0] * w), int(CROP[1] * h), int(CROP[2] * w), int(CROP[3] * h))
        crop = image.crop(box)
        frames.append(crop.resize((crop.width * UPSCALE, crop.height * UPSCALE), Image.Resampling.NEAREST))
    delay = round(TICK_MS / SLOWDOWN)
    frames[0].save(out, save_all=True, append_images=frames[1:], duration=delay, loop=0, optimize=True, disposal=1)
    print(f"{out}: {len(frames)} frames, {delay} ms each, {frames[0].width}x{frames[0].height}")


if __name__ == "__main__":
    main()
