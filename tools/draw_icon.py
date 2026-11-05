"""Draws the mod icon: three hotbar slots, the selection frame sliding from the left one to the middle one.
32x32 pixel art scaled up. Pure Python, no Pillow needed.

Usage: python tools/draw_icon.py <output.png> [scale, default 4 = 128x128; use 16 for 512x512]
"""
import struct
import sys
import zlib

SIZE = 32
SCALE = int(sys.argv[2]) if len(sys.argv) > 2 else 4
CLEAR = (0, 0, 0, 0)
px = [[CLEAR] * SIZE for _ in range(SIZE)]


def rgb(h):
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), 255)


def rect(x0, y0, x1, y1, color):
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            px[y][x] = color


def frame(x0, y0, x1, y1, color, dashed=False):
    """A one-pixel outline; dashed skips every other pixel."""
    for x in range(x0, x1 + 1):
        for y in (y0, y1):
            if not dashed or x % 2 == 0:
                px[y][x] = color
    for y in range(y0, y1 + 1):
        for x in (x0, x1):
            if not dashed or y % 2 == 0:
                px[y][x] = color


# Three vanilla-looking slots: grey fill, dark top-left edge, white bottom-right edge.
SLOT_Y0, SLOT_Y1 = 13, 22
for x0 in (2, 12, 22):
    x1 = x0 + 8
    rect(x0, SLOT_Y0, x1, SLOT_Y1, rgb("8b8b8b"))
    rect(x0, SLOT_Y0, x1, SLOT_Y0, rgb("373737"))
    rect(x0, SLOT_Y0, x0, SLOT_Y1, rgb("373737"))
    rect(x0, SLOT_Y1, x1, SLOT_Y1, rgb("ffffff"))
    rect(x1, SLOT_Y0, x1, SLOT_Y1, rgb("ffffff"))

# Ghost of the frame on the left slot (where it came from), dashed; it hugs the slot's own border.
frame(1, 12, 11, 23, rgb("c8c8c8"), dashed=True)
# The frame on the middle slot (where it is going): bright and solid, one pixel outside the slot.
frame(11, 12, 21, 23, rgb("ffffff"))

# Gold motion arrow above the row, from the ghost towards the frame.
gold = rgb("ffd84a")
for x in range(4, 17):
    px[6][x] = gold
for x, y in ((17, 6), (16, 5), (16, 7), (15, 4), (15, 8)):
    px[y][x] = gold


def png(path):
    rows = b""
    for y in range(SIZE * SCALE):
        rows += b"\x00" + b"".join(bytes(px[y // SCALE][x // SCALE]) for x in range(SIZE * SCALE))

    def chunk(tag, data):
        return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)

    w = h = SIZE * SCALE
    data = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    data += chunk(b"IDAT", zlib.compress(rows, 9)) + chunk(b"IEND", b"")
    open(path, "wb").write(data)


png(sys.argv[1])
