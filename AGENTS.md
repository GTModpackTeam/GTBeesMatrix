# AGENTS.md

This file provides guidance to AI coding agents when working with code in this repository.

## Project Overview

GTBeesMatrix (GTBM) is a Minecraft 1.12.2 Forge mod that adds tweaks, fixes, and integrations for Forestry bees in GregTech-based modpacks. It extends GregTech CE Unofficial (CEu) with bee breeding fixes, GT machine recipes for bee products, and multiblock apiaries.

Note: This project was renamed from GTExpert (GTE). Many internal class names still use the GTE prefix (e.g., `GTEModule`, `GTEModuleManager`, `BaseGTEModule`), but the mod ID and package path use `gtbm`.

## Build Commands

```bash
# Setup development workspace (required first time)
./gradlew setupDecompWorkspace

# Build the mod
./gradlew build

# Run Minecraft client
./gradlew runClient

# Run Minecraft server
./gradlew runServer

# Apply code formatting (Spotless)
./gradlew spotlessApply

# Check code formatting
./gradlew spotlessCheck

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.github.gtexpert.core.GTETest"

# Update buildscript to latest version
./gradlew updateBuildScript
```

## Architecture

### Module System

The mod uses a custom module system for organizing features and integrations:

- **Module Manager** (`modules/GTEModuleManager.java`): Discovers and loads modules via ASM, manages lifecycle events, and handles module dependencies
- **Base Module** (`modules/BaseGTEModule.java`): Abstract base class all modules extend
- **Module Container** (`modules/GTEModules.java`): Registers the main module container

Modules are annotated with `@GTEModule` and automatically discovered at runtime. Module configuration is in `config/gtexpert/modules.cfg`.

### Integration Modules

Located in `integration/`, each subdirectory contains integration code for a specific mod:
- `forestry` - Forestry (farms, bees, recipes)
- `gendustry` - Gendustry (industrial apiary, bee status widget)
- `binnies` - Binnie's Mods (Extra Bees, Extra Trees)
- `gtfo` - GregTech Food Option
- `top` - The One Probe (tooltip providers for apiaries)

Integration modules extend `GTBMIntegrationSubmodule` and declare mod dependencies via the `@GTEModule(modDependencies = {...})` annotation.

### Core Components

- **CoreMod** (`core/GTECoreMod.java`): FML loading plugin that performs ASM transformations and handles dependency loading
- **Mixins** (`mixins/`): Mixin classes organized by target mod (chisel, draconicevolution, gcym, gregtech)
- **Common** (`common/`): Blocks, items, metatileentities, config, and event handlers
- **API** (`api/`): Public API including recipes, materials, capabilities, and utilities
- **Loaders** (`loaders/`): Recipe and material loaders

### Key Dependencies

- GregTech CE Unofficial (CEu) - Required
- Forestry - Required
- MixinBooter - Required for mixin support
- GroovyScript - Required for scripting support

## Code Style

- **Formatting**: Enforced via Spotless using Eclipse formatter (run `./gradlew spotlessApply`)
- **Import order**: Defined in `spotless.importorder` (gregtech, net, codechickenlib, other, javax/java, static)
- **Line length**: 120 characters max
- **Indentation**: 4 spaces

## Configuration

- `buildscript.properties`: Main build configuration (mod version, features, dependencies)
- `dependencies.gradle`: Add new mod dependencies here
- `repositories.gradle`: Add new maven repositories here
- Debug flags in `buildscript.properties` enable runtime dependencies for specific mod integrations during development

## Testing Integration Mods

Enable debug flags in `buildscript.properties` to load specific mods at runtime:
```properties
debug_all = false       # Enable all optional mods
debug_gendustry = true  # Enable just Gendustry
debug_binnies = true    # Enable just Binnie's Mods
debug_gtfo = true       # Enable just GregTech Food Option
```
