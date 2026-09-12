package io.github.demsum.maidsoulbrewery.recipe;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record RecipeOption(ResourceLocation id, ItemStack result, List<Ingredient> ingredients) {
    public RecipeOption {
        result = result.copy();
        ingredients = List.copyOf(ingredients);
    }

    @Override
    public ItemStack result() {
        return result.copy();
    }
}
