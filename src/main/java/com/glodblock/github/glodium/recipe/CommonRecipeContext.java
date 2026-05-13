package com.glodblock.github.glodium.recipe;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class CommonRecipeContext<T extends Recipe<@NotNull RecipeInput>> extends RecipeSearchContext<RecipeInput, T> {

    public CommonRecipeContext(Supplier<ServerLevel> levelGetter, RecipeType<@NotNull T> type) {
        super(levelGetter, type);
    }

}
