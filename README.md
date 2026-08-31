# MaidSoul Brewery

Unofficial compatibility patch for Minecraft 1.21.1 NeoForge.

MaidSoul Brewery currently targets DrinkBeer Refill compatibility only. It patches the gap where the current Maidsoul Kitchen release cannot directly handle DrinkBeer Refill beer barrels, without modifying, bundling, or redistributing upstream jars.

当前仅适配 DrinkBeer Refill。现版本 Maidsoul Kitchen 还无法直接适配 DrinkBeer Refill 的啤酒桶，本项目通过最小 Mixin patch 与必要 data 覆盖补齐兼容，不修改、不内置、不再分发上游 jar。

The current patch prevents MSK maid beer-barrel tasks from pulling brewing ingredients back out, adds the small barrel bridge expected by MSK, and includes DrinkBeer water/milk recipe compatibility data overrides.

当前补丁会避免 MSK 女仆从啤酒桶输入槽取走酿造原料，并补齐 MSK 需要的啤酒桶桥接逻辑，同时包含 DrinkBeer 水桶/牛奶桶配方兼容覆盖。

## Compatibility / 兼容范围

- Minecraft 1.21.1
- NeoForge
- Maidsoul Kitchen
- DrinkBeer Refill

## Planned / 计划中

- Compatibility patches for more brewing-related mods
- Vanilla brewing stand compatibility

## Notes / 说明

This is not an official Maidsoul Kitchen or DrinkBeer Refill project.

本项目不是 Maidsoul Kitchen 或 DrinkBeer Refill 的官方项目。

Currently intended for private use unless upstream recipe-data redistribution permission is confirmed.

当前仅建议私用；公开发布前需要确认上游配方数据再分发授权。

## License / 许可证

MIT License.

Copyright (c) 2026 DemSum.
