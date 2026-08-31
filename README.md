# MaidSoul Brewery

Unofficial compatibility patch for Minecraft 1.21.1 NeoForge.

MaidSoul Brewery currently targets DrinkBeer Refill compatibility only. It patches the gap where the current Maidsoul Kitchen release cannot directly handle DrinkBeer Refill beer barrels, without modifying, bundling, or redistributing upstream jars.

当前仅适配 DrinkBeer Refill。现版本 Maidsoul Kitchen 还无法直接适配 DrinkBeer Refill 的啤酒桶，本项目通过最小 Mixin patch 补齐兼容，不修改、不内置、不再分发上游 jar。

The public version prevents MSK maid beer-barrel tasks from pulling brewing ingredients back out and adds the small barrel bridge expected by MSK.

公开版会避免 MSK 女仆从啤酒桶输入槽取走酿造原料，并补齐 MSK 需要的啤酒桶桥接逻辑。

No DrinkBeer Refill recipe overrides or upstream recipe data are included in this public repository.

公开仓库不包含 DrinkBeer Refill 配方覆盖，也不包含上游配方数据。

## Temporarily Removed Recipes / 暂时移除的配方

The public version temporarily removes compatibility overrides for these DrinkBeer Refill recipes:

- `drinkbeer:beer_mug`
- `drinkbeer:beer_mug_apple_lambic`
- `drinkbeer:beer_mug_blaze_milk_stout`
- `drinkbeer:beer_mug_blaze_stout`
- `drinkbeer:beer_mug_frothy_pink_eggnog`
- `drinkbeer:beer_mug_night_howl_kvass`
- `drinkbeer:beer_mug_pumpkin_kvass`
- `drinkbeer:beer_mug_sweet_berry_kriek`

These overrides will be updated in the public version after recipe-data redistribution permission is confirmed.

公开版暂时移除了以上 DrinkBeer Refill 配方的兼容覆盖。取得配方数据再分发授权后，公开版会更新这些内容。

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

Private builds may contain local-only recipe compatibility data. Public builds exclude that content unless redistribution permission is confirmed.

私人版可能包含仅限本地使用的配方兼容数据。公开版默认排除这类内容，除非后续确认再分发授权。

## License / 许可证

MIT License.

Copyright (c) 2026 DemSum.
