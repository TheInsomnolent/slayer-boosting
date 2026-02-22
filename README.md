# Slayer Boosting

A [RuneLite](https://runelite.net/) plugin that helps you maximise slayer points by reminding you which slayer master to use at each milestone task.

## Features

- **Configurable rules** – define up to 5 rules of the form *"every X tasks, use master Y"*; the highest-interval match wins.
- **NPC highlighting** – correct master is highlighted **green**, wrong masters are highlighted **red** (uses the built-in NPC overlay service).
- **Permanent overlay** – always-visible panel showing your next task number, which master to visit, current streak, points, and projected points for the next task.
- **Achievement diary support** – toggle Elite Western Provinces (+25 % for Nieve/Steve) and Elite Kourend & Kebos (boosted Konar points) for accurate projections.
- **Live config reload** – changes take effect immediately, no restart needed.

## Default Configuration

| Rule | Interval | Master  |
|------|----------|---------|
| Default | every task | Turael / Aya |
| Rule 1 | every 10th | Duradel |
| Rule 2 | every 50th | Konar |

Rules 3–5 are available for custom setups (e.g. Krystilia skipping is not yet tracked).

## Installation

Install from the **RuneLite Plugin Hub**:

1. Open RuneLite → click the wrench icon → **Plugin Hub**.
2. Search for **Slayer Boosting** and click **Install**.

## Building from Source

```bash
./gradlew build
```

Requires Java 11+.

## License

[BSD 2-Clause](LICENSE)