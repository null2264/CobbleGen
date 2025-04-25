# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed
- Fix networking
- Fix server still try to sync recipe with client when `enableRecipeViewer` is set to false

## [v5.4.1] - 2025-04-25

### Fixed
- Fix crashes when being loaded alongside unsupported Create mod
- Fix `cobblegen-meta.create.loadIntegration = false` didn't stop CobbleGen from trying to mixin Create mod
- [NeoForge] Fix Create support

## [v5.4.0] - 2025-04-07

### Added
- Support for MC 1.21.5
- Add GameTest
  - Add CI to test the mod using HeadlessMC

### Other
- Completely rework the project to use Manifold's preprocessor instead of ReplayMod's preprocessor
  - Also start splitting the project into several modules

## [v5.3.16] - 2025-03-10

### Fixed
- Fix CobbleGen generating empty config instead of default config

## [v5.3.15] - 2025-02-02

### Fixed
- Fix crashes due to MC breaking changes in MC 1.21.2 and newer

## [v5.3.14] - 2024-12-26

### Fixed
- Fix Create integration for Patch I and J

### Other
- Update gradle to v8.11.1
- Update archloom to v1.9
- Update me.modmuss50.mod-publish-plugin to v0.8.1
- Update com.gradleup.shadow to v8.3.5

## [v5.3.13] - 2024-11-05

### Added
- Re added support for MC 1.19.3-1.19.4

## [v5.3.12] - 2024-10-30

### Fixed
- Fixed REI integration due to API breaking changes in MC 1.21.2 or newer
- Adjust JEI integration due to deprecations in JEI 19 or newer
- Fixed issues with sending packet on NeoForge caused by Network Refactor

## [v5.3.11] - 2024-10-24

- MC 1.21.2 and 1.21.3 support

## [v5.3.10] - 2024-09-10

- Prevent some potential NullPointerException crashes
- Force config to generate when the game is loaded instead of waiting for player to load a world
- Added experimental feature: Biome filter (`biomes` and `excludedBiomes`)  
  Similar to `dimensions` and `excludedDimensions`, it lets to you to control on which biome a block can or cannot generate. Change `enableExperimentalFeatures` in `cobblegen-meta.json5` to test this feature.

## [v5.3.9] - 2024-06-21

- Fixed basalt generator is not being loaded

## [v5.3.8] - 2024-06-18

- Fixed tags only generate air

## [v5.3.7] - 2024-06-14

- Add support for MC 1.21

## [v5.3.6] - 2024-04-24

- Fixed NeoForge version of the mod can't be loaded

## [v5.3.5] - 2024-04-24

- Add support for MC1.20.5 full release

## [v5.3.4] - 2024-04-04

- Fixed players can't join a server when advanced generator is configured

## [v5.3.3] - 2024-03-17

- [FABRIC] Fix incompatibility with Create Fabric patch F caused by Porting Lib breaking changes
- [1.18.2] Fix crash related to Log4J

## [v5.3.2] - 2024-03-03

- [1.20.5] Fix recipe sync (networking)

## [v5.3.1] - 2024-02-28

- Fixed networking issue

## [v5.3.0] - 2024-02-25

- [1.20.2] Now uses Configuration Phase to sync config
- Added initial support for 1.20.5 (might be a bit buggy)
- Fixed crash due to PortingLib's Fake Player removal

## [v5.2.4] - 2024-01-09

- Add (partial) support for 1.16.5
- Adjust reload meta command permission
- Add option to disable debug log to reduce log spam

## [v5.2.3] - 2023-12-02

- [Fabric 1.20.2+] Fixed incompatibility with Fabric API 0.91+

## [v5.2.2] - 2023-11-17

- [1.20.2] NeoForge support!

## [v5.2.1] - 2023-11-15

- [EMI] Add option to invert input

## [v5.2.0] - 2023-11-12

- Added meta config
  - Added a toggle to merge CobbleGen to EMI's "World Interaction" category
  - Added an option to disable CobbleGen tooltip when merged with EMI
  - Added a toggle to remove overlapping recipes when merged with EMI
  - Added an option to disable Recipe Viewer support entirely
  - Added an option to enable experimental features
- [Experimental] Ability to customize obsidian generation

## [v5.1.1] - 2023-10-07

- Forge support

## [v5.1.0] - 2023-09-29

- 1.20.2 support

## [v5.0.3] - 2023-09-17

- Fixed Create 0.5.1 support

## [v5.0.2] - 2023-09-03

- Fixed filter not working properly

## [v5.0.1] - 2023-06-21

- [EMI Integration] Merged CobbleGen recipe categories into World Interaction category

## [v5.0.0] - 2023-06-20

- Added Forge support
- [1.20.x] Fixed JEI integration
- [1.20.x] Fixed REI integration
- Fixed config not actually reloading when admin use the `/reload` command
- Fixed `customGen` always returns default value unless explicitly emptied in the config (`"customGen" = {}`)
- Added Create 0.5.1 support

## [v4.0.4] - 2023-05-28

- Fix Create Pipe generating Obsidian

## [v4.0.3] - 2023-05-20

- Fixed Obsidian can't be generated

## [v4.0.2] - 2023-05-18

- Fixed config not being loaded properly

## [v4.0.1] - 2023-05-16

- Added 1.20+ support
- API breaking change to prevent future breaking changes

## [v4.0.0] - 2023-05-15

- Overhauled how fluid interaction is being handled
- Improved Create mod compatibility
- Added Advanced Custom Generators (Custom Fluid Interaction)
- Added API for mod developers
- Recipe Viewers (REI/EMI/JEI) now shows server-side config when available

## [v3.2.1] - 2023-04-29

- Fix server crash

## [v3.2.0] - 2023-04-24

- Fix compatibility with Porting Lib
- Fix Create Pipe support

## [v3.1.0] - 2023-04-23

- REI and EMI integration
- Create Pipe support

[unreleased]: https://github.com/null2264/CobbleGen/compare/v5.4.1...main
[v5.4.1]: https://github.com/null2264/CobbleGen/compare/v5.4.0...v5.4.1
[v5.4.0]: https://github.com/null2264/CobbleGen/compare/v5.3.16...v5.4.0
[v5.3.16]: https://github.com/null2264/CobbleGen/compare/v5.3.15...v5.3.16
[v5.3.15]: https://github.com/null2264/CobbleGen/compare/v5.3.14...v5.3.15
[v5.3.14]: https://github.com/null2264/CobbleGen/compare/v5.3.13...v5.3.14
[v5.3.13]: https://github.com/null2264/CobbleGen/compare/v5.3.12...v5.3.13
[v5.3.12]: https://github.com/null2264/CobbleGen/compare/v5.3.11...v5.3.12
[v5.3.11]: https://github.com/null2264/CobbleGen/compare/v5.3.10...v5.3.11
[v5.3.10]: https://github.com/null2264/CobbleGen/compare/v5.3.9...v5.3.10
[v5.3.9]: https://github.com/null2264/CobbleGen/compare/v5.3.8...v5.3.9
[v5.3.8]: https://github.com/null2264/CobbleGen/compare/v5.3.7...v5.3.8
[v5.3.7]: https://github.com/null2264/CobbleGen/compare/v5.3.6...v5.3.7
[v5.3.6]: https://github.com/null2264/CobbleGen/compare/v5.3.5...v5.3.6
[v5.3.5]: https://github.com/null2264/CobbleGen/compare/v5.3.4...v5.3.5
[v5.3.4]: https://github.com/null2264/CobbleGen/compare/v5.3.3...v5.3.4
[v5.3.3]: https://github.com/null2264/CobbleGen/compare/v5.3.2...v5.3.3
[v5.3.2]: https://github.com/null2264/CobbleGen/compare/v5.3.1...v5.3.2
[v5.3.1]: https://github.com/null2264/CobbleGen/compare/v5.3.0...v5.3.1
[v5.3.0]: https://github.com/null2264/CobbleGen/compare/v5.2.4...v5.3.0
[v5.2.4]: https://github.com/null2264/CobbleGen/compare/v5.2.3...v5.2.4
[v5.2.3]: https://github.com/null2264/CobbleGen/compare/v5.2.2...v5.2.3
[v5.2.2]: https://github.com/null2264/CobbleGen/compare/v5.2.1...v5.2.2
[v5.2.1]: https://github.com/null2264/CobbleGen/compare/v5.2.0...v5.2.1
[v5.2.0]: https://github.com/null2264/CobbleGen/compare/v5.1.1...v5.2.0
[v5.1.1]: https://github.com/null2264/CobbleGen/compare/v5.1.0...v5.1.1
[v5.1.0]: https://github.com/null2264/CobbleGen/compare/v5.0.3...v5.1.0
[v5.0.3]: https://github.com/null2264/CobbleGen/compare/v5.0.2...v5.0.3
[v5.0.2]: https://github.com/null2264/CobbleGen/compare/v5.0.1...v5.0.2
[v5.0.1]: https://github.com/null2264/CobbleGen/compare/v5.0.0...v5.0.1
[v5.0.0]: https://github.com/null2264/CobbleGen/compare/v4.0.4...v5.0.0
[v4.0.4]: https://github.com/null2264/CobbleGen/compare/v4.0.3...v4.0.4
[v4.0.3]: https://github.com/null2264/CobbleGen/compare/v4.0.2...v4.0.3
[v4.0.2]: https://github.com/null2264/CobbleGen/compare/v4.0.1...v4.0.2
[v4.0.1]: https://github.com/null2264/CobbleGen/compare/v4.0.1...v4.0.1
[v4.0.0]: https://github.com/null2264/CobbleGen/compare/v3.2.1...v4.0
[v3.2.1]: https://github.com/null2264/CobbleGen/compare/v3.2...v3.2.1
[v3.2.0]: https://github.com/null2264/CobbleGen/compare/v3.1...v3.2
[v3.1.0]: https://github.com/null2264/CobbleGen/compare/11d70c8be8e1474630f9643b801f69ac3db0719c...v3.1
