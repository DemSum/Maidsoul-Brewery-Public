# MaidSoul Brewery

Unofficial compatibility patch for Minecraft 1.21.1 NeoForge.

MaidSoul Brewery currently targets small Maidsoul Kitchen compatibility patches for DrinkBeer Refill and selected farming behavior, without modifying, bundling, or redistributing upstream jars.

当前用于补齐 Maidsoul Kitchen 与 DrinkBeer Refill 以及部分作物行为的小型兼容问题，不修改、不内置、不再分发上游 jar。

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

When a maid has a Culinary Hub in the slot designated by Maidsoul Kitchen, the `Steamer Cooking` task uses the hub's persistent Ingredient and Output sections as transport buffers. It pulls recipe-valid food only from chests bound as Ingredient storage and sends completed food only to chests bound as Output storage. If no hub is installed, it falls back to the maid's available backpack. It does not build, dismantle, stack, or open and close steamers.

女仆在 Maidsoul Kitchen 指定槽位携带烹饪中枢时，“蒸笼烹饪”任务会使用中枢持久化的 Ingredient 与 Output 分区作为运输缓冲，只从绑定为 Ingredient 的箱子领取合法原料，并只把成品送入绑定为 Output 的箱子。没有安装中枢时才回退为女仆可用背包。女仆不会搭建、拆除、堆叠或开关蒸笼。

Each maid has a persistent recipe filter in the `Steamer Cooking` task configuration. It defaults to an empty blacklist (all recipes allowed), uses the same icon-grid whitelist/blacklist layout as Maidsoul Kitchen cooking tasks, and filters only new ingredient placement. Already-finished food is always collected.

每名女仆的“蒸笼烹饪”任务配置中都有独立持久化的配方筛选。默认是空黑名单（允许全部配方），使用与 Maidsoul Kitchen 烹饪任务一致的图标网格白名单/黑名单界面；筛选只限制新原料投放，已经完成的食物始终可以收取。

Filter changes use a dedicated server-bound packet and are validated against the maid owner, active task, and currently loaded steamer recipes before being saved. Steamer searches reuse per-pass state and avoid repeating full device checks without changing the verified side-approach behavior.

筛选修改通过专用服务端数据包同步，保存前会验证女仆归属、当前任务以及当前加载的蒸笼配方。蒸笼搜索会复用每轮状态并避免重复完整检查设备，同时不改变已经验证的侧面落脚行为。

Maidsoul Kitchen's shared cooking movement keeps the real cooking block in `TARGET_POS` for interaction, while `WALK_TARGET` points to a reachable cardinal position beside it. A paired in-transit check prevents MSK from discarding this intentional coordinate split before the maid arrives.

Maidsoul Kitchen 通用烹饪移动会继续把真实厨具保存在 `TARGET_POS` 中用于交互，仅让 `WALK_TARGET` 指向同层侧面的可达位置；配套的途中校验会避免 MSK 在女仆抵达前错误清除这组双坐标目标。

This is not an official Maidsoul Kitchen or DrinkBeer Refill project.

本项目不是 Maidsoul Kitchen 或 DrinkBeer Refill 的官方项目。

Public development and distribution proceed under the permissions obtained by the project maintainer. Upstream jars are not bundled. Any future reuse or adaptation of upstream implementation code will remain scoped to the granted permission and will retain explicit attribution.

本项目的公开开发与分发以项目维护者已经取得的授权为前提，不内置上游 jar。未来若复用或改编上游实现代码，将严格限制在授权范围内并明确标注来源。

## Credits / 鸣谢

Touhou Little Maid by TartaricAcid, Snownee, Succinum, Pajinyi, Zhi_Ban, CrystalizedSun, Foky, ZeniCrow, Paulzzh, Yuriscat, Lappland162, with credits to Verclene and ZUN.

[Maidsoul Kitchen](https://github.com/Wall-ev/MaidsoulKitchen) by wallev, with credits to Pajinyi, TartaricAcid, and lezizijiang2.

DrinkBeer Refill by Lekavar and MarbleGateKeeper.

Farmer's Delight by vectorwing, with thanks to its listed contributors.

Ratatouille / Create: Ratatouille by Forsteri, Thaumstrial, Even, and Jelly_Candy.

Kaleidoscope Cookery by ysbbbbbb, tartaric_acid, and Azumic.

[SimplePathfinder](https://github.com/KunoSayo/simplepathfinder) by KunoSayo was reviewed as a pathfinding-performance design reference. MaidSoul Brewery 0.2.2 applies only general ideas such as reducing repeated searches and reusing per-pass state; no SimplePathfinder source code is copied.

[SimplePathfinder](https://github.com/KunoSayo/simplepathfinder)（作者 KunoSayo）为寻路性能设计提供了调研参考。MaidSoul Brewery 0.2.2 仅采用减少重复查询、复用单轮搜索状态等通用思路，没有复制 SimplePathfinder 的源代码。

感谢 Touhou Little Maid、Maidsoul Kitchen、DrinkBeer Refill、Farmer's Delight、Ratatouille / Create: Ratatouille、Kaleidoscope Cookery 与 SimplePathfinder 的作者。MaidSoul Brewery 不包含或再分发这些 mod 的 jar。

## License / 许可证

MIT License.

Copyright (c) 2026 DemSum.
