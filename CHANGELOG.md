# Changelog / 更新日志

## Unreleased / 未发布

### Changed / 调整

- Reworked Maidsoul Kitchen's shared cooking-device search around one TLM BFS pass. Reachable standing positions are evaluated first and each adjacent cooking device is fully checked at most once per pass.
- 重构 Maidsoul Kitchen 通用厨具搜索：每轮只运行一次 TLM BFS，先遍历可达落脚点，并保证每个相邻厨具每轮至多进行一次完整检查。

### Fixed / 修复

- Fixed steamer whitelist/blacklist updates by applying the validated payload on the server thread without rejecting it during transient task-state changes. Recipe hover tooltips now match Maidsoul Kitchen by showing the current mode, whether the recipe is cookable, and its ingredient.
- 修复蒸笼黑白名单更新：经过校验的数据包现在于服务端主线程写入，不再因任务状态短暂变化而误拒绝。配方悬浮提示也与 Maidsoul Kitchen 对齐，显示当前模式、配方是否可烹饪及所需原料。

- Separated Maidsoul Kitchen cooking-device coordinates from walking coordinates. Maids now walk to a reachable cardinal position beside a cooking block while continuing to look at and interact with the real block entity.
- 分离 Maidsoul Kitchen 烹饪任务的设备坐标与行走坐标。女仆现在会前往厨具同层侧面的可达落脚点，同时继续面向真实方块实体并与之交互。

- Preserved MSK's original in-transit invalid-target cleanup. The patch only extends its coordinate equality check to recognize a validated side-approach/device pair; unrelated or stale walking targets are still cleared by MSK.
- 保留 MSK 原有的途中无效目标清理，仅扩展其坐标相等判断以识别经过验证的“侧面落脚点/设备”配对；无关或过期的行走目标仍由 MSK 原逻辑清除。

- Fixed prepared steamer stacks being treated as usable only on the directly heated bottom layer. Maid inspection now follows Kaleidoscope Cookery's four-level heat propagation and searches the full reachable height of a four-layer stack.
- 修复已封盖蒸笼堆叠只有直接接触热源的第一层会被视为可用的问题。女仆检查现在遵循森罗厨房的四层热量传递规则，并覆盖四层蒸笼堆叠的完整可交互高度。

## 0.2.2 - 2026-09-11

### Changed / 调整

- Reduced steamer search overhead by reusing the recipe filter and storage view for one search, rejecting non-steamer blocks before block-entity inspection, and evaluating each discovered steamer only once per BFS pass.
- 降低蒸笼搜索开销：单次搜索复用配方筛选与仓储视图，先排除非蒸笼方块再读取方块实体，并确保每轮 BFS 只完整检查一次同一个蒸笼。

- Documented SimplePathfinder's design-reference credit and the authorized long-term plan to make future MSB versions independently provide required MSK functionality when no maintained upstream build exists.
- 补充 SimplePathfinder 设计参考鸣谢，并记录在上游不再提供维护版本时，由未来 MSB 在授权范围内独立承接必要 MSK 功能的长期计划。

### Fixed / 修复

- Replaced generic container-button toggles with a dedicated validated client-to-server payload that synchronizes the complete steamer filter state. The server now verifies ownership, the active task, and every recipe ID before persisting and syncing the maid's configuration.
- 将通用容器按钮切换改为专用且经过校验的客户端到服务端数据包，同步完整蒸笼筛选状态。服务端会验证女仆归属、当前任务与全部配方 ID，再持久化并同步配置。

- Aligned the steamer recipe panel to Maidsoul Kitchen's cooking-guide coordinates and separator rendering while retaining the requested icon-only grid without a search field or visible recipe names.
- 将蒸笼配方面板的坐标与分隔线绘制对齐 Maidsoul Kitchen 烹饪指南，同时保留所需的纯图标网格，不加入搜索栏或可见菜名。

## 0.2.1 - 2026-09-10

### Added / 新增

- Restored missing Maidsoul Kitchen altar recipes for the Culinary Hub and Burn Protect Bauble on 1.21.1, adapted from Maidsoul Kitchen 1.20.1 recipe data to the current Touhou Little Maid altar recipe format.
- 补回 Maidsoul Kitchen 1.21.1 缺失的烹饪中枢与燃着保护饰品祭坛配方，基于 Maidsoul Kitchen 1.20.1 配方数据迁移到当前 Touhou Little Maid 祭坛配方格式。

- Added an optional Kaleidoscope Cookery compatibility probe and isolated steamer/stockpot API adapters.
- 添加可选的 Kaleidoscope Cookery 兼容探针以及隔离的蒸笼/汤锅 API 适配层。

- Added a `Steamer Cooking` maid task for player-built, capped steamers. It loads recipe-valid food only when heat and space are available and waits for every occupied slot in the active layer to finish before collecting that layer.
- 添加面向玩家预先搭建且封顶蒸笼的“蒸笼烹饪”女仆任务：仅在有热源、有空位且食物存在合法配方时投料，并等待当前可操作层所有非空槽完成后再收取整层成品。

- Completed the Maidsoul Kitchen Culinary Hub chain for steamers. A maid carrying a hub in its designated inventory slot uses the hub's persistent Ingredient and Output sections as transport buffers, pulls only valid inputs from bound Ingredient chests, and sends finished food to bound Output chests. Without a hub, the task retains its maid-backpack fallback.
- 补齐蒸笼与 Maidsoul Kitchen 烹饪中枢的完整链路。女仆在指定槽位携带中枢时，会使用中枢持久化的 Ingredient 与 Output 分区作为运输缓冲，只从绑定的原料箱领取合法原料，并把成品送入绑定的输出箱；没有中枢时仍回退为女仆背包模式。

- Added capacity simulation and safe buffering for bound storage. Full or unavailable output chests prevent premature collection; if storage changes during an operation, finished food remains in the hub output buffer, the maid's hand, or the world instead of being deleted.
- 为绑定仓储增加容量模拟和安全缓冲。输出箱已满或不可用时不会提前收取；若操作过程中仓储状态发生变化，成品会留在中枢输出缓冲、女仆手中或世界中，不会被删除。

- Added a short-lived per-device work lock and final state revalidation to prevent two maids or external automation from causing stale actions. Overflow remains in the maid's hand or in the world rather than being deleted.
- 添加短时单设备工作锁与最终状态复查，避免两名女仆或外部自动化导致过期操作；意外溢出物会保留在女仆手中或世界中，不会被删除。

- Added a persistent per-maid recipe whitelist/blacklist and paged task configuration screen for `Steamer Cooking`. Filtering uses recipe IDs and affects ingredient placement only, so completed food can always be collected.
- 为“蒸笼烹饪”添加按女仆持久化的配方白名单/黑名单及分页任务配置界面。筛选以配方 ID 为准且只影响原料投放，因此已完成食物始终可以收取。

### Changed / 调整

- Matched the steamer recipe configuration to Maidsoul Kitchen's cooking-guide layout: task information, icon-only mode control, a 7-by-4 icon-only recipe grid, and scrollbar navigation. No search field or visible recipe-name buttons are used.
- 将蒸笼配方配置调整为 Maidsoul Kitchen 烹饪指南布局：任务信息、纯图标模式开关、7×4 纯图标配方网格及滚动条导航；不使用搜索栏，也不在配方按钮上显示菜名。

- Migrated Farmer's Delight mushroom colony registration from a TLM initialization Mixin to the official `ILittleMaid.registerSpecialCropHandler` extension API.
- 将 Farmer's Delight 蘑菇群落注册从 TLM 初始化 Mixin 迁移到官方 `ILittleMaid.registerSpecialCropHandler` 扩展接口。

### Fixed / 修复

- Fixed `Steamer Cooking` task activation on Touhou Little Maid 1.5.3 by returning a mutable brain-task list that TLM can extend with its built-in work behaviors.
- 修复“蒸笼烹饪”任务在 Touhou Little Maid 1.5.3 上无法启用的问题：现在返回 TLM 可继续追加内置工作行为的可变 AI 任务列表。

- Restored Maidsoul Kitchen's surrounding-position path check for the Berry Farm task on Touhou Little Maid 1.5.3, preventing a mature sweet berry bush itself from being treated as the only reachable destination.
- 恢复 Maidsoul Kitchen 浆果任务在 Touhou Little Maid 1.5.3 上失效的周边落脚点寻路检查，避免只把成熟甜浆果丛方块本身作为可达目标。

- Fixed steamer navigation choosing the steamer block itself as the walking target. Maids now approach a reachable position beside the selected steamer layer, preventing tall stacks from drawing them onto an upper floor above the device.
- 修复蒸笼寻路把蒸笼方块本身作为行走目标的问题。女仆现在会前往所选蒸笼层侧面的可达落脚点，避免高层蒸笼把女仆引到设备正上方的楼层。

## 0.2.0 - 2026-09-02

### Added / 新增

- Added Farmer's Delight mushroom colony support for TLM normal farm tasks.
- 为 TLM 普通农场任务添加 Farmer's Delight 蘑菇群落兼容。

- Normal farm maids can plant vanilla red/brown mushrooms on Farmer's Delight rich soil and harvest mature mushroom colonies.
- 普通农场女仆可以在 Farmer's Delight 沃土上种植原版红/棕蘑菇，并采收成熟蘑菇群落。

- Mushroom colony harvest now distinguishes Farmer's Delight knives from other main-hand tools: knives harvest 3 mushrooms and reset the colony to age 0, while non-knives harvest 5 mushrooms and revert the colony to a vanilla mushroom.
- 蘑菇群落采收现在区分 Farmer's Delight 刀与其他主手工具：刀采收 3 个蘑菇并把菌落重置为 age 0，非刀采收 5 个蘑菇并把菌落退回原版蘑菇。

## 0.1.1 - 2026-08-31

### Added / 新增

- Added optional Create: Ratatouille wheat kernels compatibility for DrinkBeer wheat brewing inputs.
- 为 DrinkBeer 小麦酿造输入添加可选的 Create: Ratatouille 小麦籽兼容。

### Fixed / 修复

- Prevented cup-only DrinkBeer barrels from blocking MSK maid beer-barrel work, and reused cups already present in the barrel cup slot instead of inserting duplicate cups.
- 避免只有空啤酒杯的 DrinkBeer 啤酒桶卡住 MSK 女仆啤酒桶任务，并让杯槽已有空杯抵扣所需杯子，避免重复塞杯。

## 0.1.0 - 2026-08-30

### Added / 新增

- Added a Maidsoul Kitchen and DrinkBeer Refill beer barrel compatibility patch for Minecraft 1.21.1 NeoForge.
- 为 Minecraft 1.21.1 NeoForge 添加 Maidsoul Kitchen 与 DrinkBeer Refill 啤酒桶兼容补丁。

- Prevents MSK maid beer-barrel tasks from extracting brewing ingredients from DrinkBeer barrel input slots.
- 避免 MSK 女仆啤酒桶任务从 DrinkBeer 啤酒桶输入槽取走酿造原料。

- Added DrinkBeer water/milk recipe compatibility data overrides.
- 添加 DrinkBeer 水桶/牛奶桶配方兼容覆盖。
