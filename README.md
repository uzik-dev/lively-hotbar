# Lively Hotbar

Small client-side animation for the hotbar in Minecraft (Fabric).

- **Slide** — the selection frame glides to the neighbouring slot when you scroll or press 1–9 next to the current
  one. Vanilla jumps; this moves it over 150 ms with a small spring at the end.

Jumps further than one slot (for example from slot 1 to slot 9) and wrap-around from 9 to 1 snap as in vanilla,
so the frame never sweeps across the whole bar.

Client-side only: install it on your game, servers do not need it. No Fabric API required.

## Configuration

`config/lively_hotbar.properties` is created on first launch. Durations are in milliseconds.

| Key | Default | What it does |
|---|---|---|
| `enabled` | `true` | Turns the animation on or off |
| `slide` | `true` | Slide to a neighbouring slot instead of jumping |
| `slideDurationMs` | `150` | Length of the slide |
| `bounce` | `true` | Small overshoot at the end; `false` for a plain ease-out |

## Building

Java 25+: `./gradlew build` (builds against Minecraft 26.3; the mod declares 26.3 only), the jar is in `build/libs`.

## License

MIT
