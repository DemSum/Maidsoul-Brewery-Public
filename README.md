# MaidSoul Brewery

Unofficial compatibility patch for Minecraft 1.21.1 NeoForge.

The current patch prevents MSK maid beer-barrel tasks from pulling brewing ingredients back out, adds the small barrel bridge expected by MSK, includes DrinkBeer water/milk recipe compatibility data overrides, restores missing MSK altar recipes for 1.21.1, lets TLM normal farm maids handle Farmer's Delight mushroom colonies on rich soil, restores MSK Berry Farm surrounding-position path checks on TLM 1.5.3, separates MSK cooking-device positions from safe standing positions, and adds a Culinary Hub-aware maid task for prepared Kaleidoscope Cookery steamers.

当前补丁会避免 MSK 女仆从啤酒桶输入槽取走酿造原料，并补齐 MSK 需要的啤酒桶桥接逻辑，同时包含 DrinkBeer 水桶/牛奶桶配方兼容覆盖，补回 MSK 1.21.1 缺失的祭坛配方，允许 TLM 普通农场女仆处理 Farmer's Delight 沃土上的蘑菇群落，修复 MSK 浆果任务在 TLM 1.5.3 上失效的周边落脚点寻路判断，分离 MSK 通用烹饪任务的厨具坐标与安全落脚点，并为玩家预先搭好的森罗厨房蒸笼添加支持烹饪中枢完整仓储链路的女仆任务。

## Compatibility / 兼容范围

- Minecraft 1.21.1
- NeoForge
- Maidsoul Kitchen
- DrinkBeer Refill
- Touhou Little Maid altar recipes
- Farmer's Delight mushroom colonies on rich soil
- Maidsoul Kitchen Berry Farm pathing on Touhou Little Maid 1.5.3
- Maidsoul Kitchen shared cooking tasks use reachable same-level side approaches
- Kaleidoscope Cookery prepared-steamer maid task with Culinary Hub input/output storage

## Planned / 计划中

- Compatibility patches for more brewing-related mods
- A Minecraft 26.1+ migration after required upstream builds become available. If Maidsoul Kitchen is no longer maintained for those versions, MaidSoul Brewery will progressively integrate the required cooking-task, configuration, and storage functionality through the authorized [MaidsoulBrewery fork](https://github.com/DemSum/MaidsoulBrewery) instead of depending on an unavailable MSK build.
- Vanilla brewing stand compatibility

- 更多酿造相关模组兼容补丁
- 在所需上游构件可用后迁移至 Minecraft 26.1 及更高版本。如果 Maidsoul Kitchen 届时已停止维护，MaidSoul Brewery 将通过已获授权的 [MaidsoulBrewery fork](https://github.com/DemSum/MaidsoulBrewery) 逐步融合所需的烹饪任务、配置与仓储功能，而不是依赖不存在的新版本 MSK 构件。
- 原版酿造台兼容

## Notes / 说明

Maidsoul Kitchen's shared cooking movement keeps the real cooking block in `TARGET_POS` for interaction, while `WALK_TARGET` points to a reachable cardinal position beside it. A paired in-transit check prevents MSK from discarding this intentional coordinate split before the maid arrives.

Maidsoul Kitchen 通用烹饪移动会继续把真实厨具保存在 `TARGET_POS` 中用于交互，仅让 `WALK_TARGET` 指向同层侧面的可达位置；配套的途中校验会避免 MSK 在女仆抵达前错误清除这组双坐标目标。

This is not an official Maidsoul Kitchen or DrinkBeer Refill project.

本项目不是 Maidsoul Kitchen 或 DrinkBeer Refill 的官方项目。

Public development and distribution proceed under the permissions obtained by the project maintainer.

本项目的公开开发与分发以项目维护者已经取得的授权为前提。

## Credits / 鸣谢

Touhou Little Maid by TartaricAcid, Snownee, Succinum, Pajinyi, Zhi_Ban, CrystalizedSun, Foky, ZeniCrow, Paulzzh, Yuriscat, Lappland162, with credits to Verclene and ZUN.

[Maidsoul Kitchen](https://github.com/Wall-ev/MaidsoulKitchen) by wallev, with credits to Pajinyi, TartaricAcid, and lezizijiang2.

DrinkBeer Refill by Lekavar and MarbleGateKeeper.

Farmer's Delight by vectorwing, with thanks to its listed contributors.

Ratatouille / Create: Ratatouille by Forsteri, Thaumstrial, Even, and Jelly_Candy.

Kaleidoscope Cookery by ysbbbbbb, tartaric_acid, and Azumic.

[SimplePathfinder](https://github.com/KunoSayo/simplepathfinder) by KunoSayo was reviewed as a pathfinding-performance design reference. MaidSoul Brewery 0.2.2 applies only general ideas such as reducing repeated searches and reusing per-pass state; no SimplePathfinder source code is copied.

[SimplePathfinder](https://github.com/KunoSayo/simplepathfinder)（作者 KunoSayo）为寻路性能设计提供了调研参考。MaidSoul Brewery 0.2.2 仅采用减少重复查询、复用单轮搜索状态等通用思路，没有复制 SimplePathfinder 的源代码。

感谢 Touhou Little Maid、Maidsoul Kitchen、DrinkBeer Refill、Farmer's Delight、Ratatouille / Create: Ratatouille、Kaleidoscope Cookery 与 SimplePathfinder 的作者。
## License / 许可证

MIT License.

Copyright (c) 2026 DemSum.
