package com.glodblock.github.glodium.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public abstract class CommonRecipeContext<T extends Recipe<RecipeInput>> extends RecipeSearchContext<RecipeInput, T> {

    public CommonRecipeContext(Supplier<Level> levelGetter, RecipeType<T> type) {
        super(levelGetter, type);
    }

}
