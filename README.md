# Angry Birds Clone (LibGDX + Box2D)

A playable 2D physics game inspired by Angry Birds, built with **Java**, **LibGDX**, **Box2D**, and **Gradle**. Designed with clean OOP architecture, modular systems, and Mac M1 (ARM64) compatibility via LWJGL3.

## Quick Start

**Requirements:** Java 11+ (Java 17 recommended)

```bash
./gradlew desktop:run
```

Build only:

```bash
./gradlew build
```

## Controls

| Input | Action |
|-------|--------|
| Mouse drag | Aim slingshot (pull bird backward) |
| Mouse release | Launch bird |
| Tap (while bird flying) | Activate bird special ability |
| Space | Activate bird special ability |
| P / Esc | Pause |

## Project Structure

```
angrybird/
├── core/                          # Platform-independent game logic
│   └── src/main/java/com/game/angrybirds/
│       ├── AngryBirdsGame.java    # Root Game class, owns shared managers
│       ├── assets/                # GameAssets — textures, fonts, sounds
│       ├── entities/              # Birds, blocks, pigs, slingshot
│       │   ├── birds/               # Bird hierarchy + factory
│       │   ├── blocks/            # Block hierarchy + factory
│       │   └── pigs/              # Pig enemy
│       ├── levels/                # LevelData, LevelLoader, enums
│       ├── managers/              # Screen, Level, Score, Save, Sound
│       ├── physics/               # Box2D world, bodies, contact listener
│       ├── screens/               # All game screens
│       ├── ui/                    # Scene2D HUD and widgets
│       └── utils/                 # Constants, math, texture generation
├── desktop/                       # LWJGL3 desktop launcher
├── assets/                        # Optional external assets (procedural fallback built-in)
└── build.gradle
```

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     AngryBirdsGame                          │
│  GameAssets │ ScreenManager │ SoundManager │ SaveManager    │
└──────────────────────────┬──────────────────────────────────┘
                           │
         ┌─────────────────┼─────────────────┐
         ▼                 ▼                 ▼
   MainMenuScreen   LevelSelectScreen   GameScreen
                                              │
                    ┌─────────────────────────┼─────────────────────────┐
                    ▼                         ▼                         ▼
           PhysicsWorldManager        LevelManager              ScoreManager
                    │                         │
                    ▼                         ▼
           GameContactListener          Entity spawning
           (damage, scoring)           (birds, blocks, pigs)
                    │
                    ▼
              Box2D World
```

### Core Systems

#### 1. Screen Management (`managers/ScreenManager`)
- Factory and pool for all screens
- Handles transitions: Main Menu → Level Select → Game → Pause → Game Over
- Each screen extends `AbstractScreen` with shared camera/viewport/stage setup

#### 2. Asset Management (`assets/GameAssets`)
- Centralized loading and disposal of textures, fonts, sounds
- **Procedural fallback**: colored shapes generated at runtime so the game runs without external art files
- Drop PNG/OGG files into `assets/` to override procedural assets (extend `loadOptionalFileAssets()`)

#### 3. Physics (`physics/`)
- `PhysicsWorldManager`: Box2D world with fixed timestep (1/60s) accumulator pattern
- `BodyFactory`: standardized body creation with collision categories/masks
- `GameContactListener`: impulse-based damage in `postSolve`, delegates to entities

#### 4. Entity System (`entities/`)
- `Entity`: base class syncing Box2D body ↔ Sprite
- **Birds**: abstract `Bird` → `RedBird`, `BombBird`, `SpeedBird` with unique abilities
- **Blocks**: abstract `Block` → `WoodBlock`, `StoneBlock`, `GlassBlock` with durability
- **Pigs**: health + death fade animation
- **Slingshot**: drag/release, launch velocity, trajectory prediction dots

#### 5. Level System (`levels/`)
- `LevelData`: declarative structure (birds, block placements, pig placements)
- `LevelLoader`: registers built-in levels; easily extended for JSON/file loading
- `LevelManager`: spawns entities from level data, tracks win/lose state

#### 6. Collision & Scoring
- Collision categories defined in `GameConstants` (bit masks)
- Damage proportional to contact impulse
- `ScoreManager`: pig/block destruction points + remaining bird bonus

#### 7. Save System (`managers/SaveManager`)
- LibGDX `Preferences` for high scores and unlocked levels

#### 8. UI (`ui/`)
- Scene2D for menus and popups
- `HUD` for in-game score and bird count overlay

## Adding a New Level

Edit `LevelLoader.java`:

```java
private LevelData createLevel4() {
    LevelData level = new LevelData();
    level.name = "My Level";
    level.index = 3;
    level.birds.add(BirdType.RED);
    level.blocks.add(new LevelData.BlockPlacement(BlockType.WOOD, 900, 120));
    level.pigs.add(new LevelData.PigPlacement(900, 200));
    return level;
}
```

Register it in `registerBuiltInLevels()`.

## Adding a New Bird Type

1. Add enum value to `BirdType`
2. Create class extending `Bird`, implement `performAbility()`
3. Register in `BirdFactory`
4. Add texture in `GameAssets.generateProceduralAssets()`

## SOLID Design Notes

| Principle | Application |
|-----------|-------------|
| **S** | Each manager owns one concern (sound, save, score, levels) |
| **O** | New birds/blocks/levels added via extension, not modification of core loops |
| **L** | All birds/blocks substitutable through base classes |
| **I** | `CollisionCallback` interface for contact listener decoupling |
| **D** | Screens depend on `AngryBirdsGame` accessors, not concrete implementations |

## Mac M1 Compatibility

- Uses **LWJGL3** backend (native ARM64 support)
- LibGDX **1.12.1** with desktop natives
- Java 11+ (tested with Amazon Corretto 17)

## License

Educational / portfolio project. Angry Birds is a trademark of Rovio Entertainment.
