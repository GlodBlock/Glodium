package com.glodblock.github.glodium.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class RecipeSearchContext<C extends RecipeInput, T extends Recipe<@NotNull C>> {

    private static final Codec<ResourceKey<@NotNull Recipe<?>>> RECIPE_CODEC = ResourceKey.codec(Registries.RECIPE);
    public boolean stuck;
    public boolean dirty;
    @Nullable
    public RecipeHolder<@NotNull T> lastRecipe;
    @Nullable
    public RecipeHolder<@NotNull T> currentRecipe;
    private final Supplier<ServerLevel> levelGetter;
    private final RecipeType<@NotNull T> type;

    public RecipeSearchContext(Supplier<ServerLevel> levelGetter, RecipeType<@NotNull T> type) {
        this.levelGetter = levelGetter;
        this.type = type;
    }

    public void findRecipe() {
        if (lastRecipe != null) {
            if (testRecipe(lastRecipe)) {
                currentRecipe = lastRecipe;
                stuck = false;
                return;
            }
            lastRecipe = null;
        }
        stuck = false;
        this.onFind(this.searchRecipe());
    }

    public void onInvChange() {
        stuck = false;
        dirty = true;
    }

    public boolean shouldTick() {
        if (currentRecipe != null) {
            return true;
        }
        return !stuck;
    }

    public void onFind(@Nullable RecipeHolder<@NotNull T> recipe) {
        if (recipe == null) {
            if (dirty) {
                dirty = false;
                return;
            }
            stuck = true;
            currentRecipe = null;
            return;
        }
        dirty = false;
        lastRecipe = recipe;
        currentRecipe = recipe;
        stuck = false;
    }

    public RecipeHolder<@NotNull T> searchRecipe() {
        var level = this.levelGetter.get();
        if (level == null) {
            return null;
        }
        var recipes = level.recipeAccess().recipeMap().byType(this.type);
        for (var recipe : recipes) {
            if (testRecipe(recipe)) {
                return recipe;
            }
        }
        return null;
    }

    public abstract boolean testRecipe(RecipeHolder<T> recipe);

    public abstract void runRecipe(RecipeHolder<T> recipe);

    public void save(CompoundTag tag) {
        var nbt = new CompoundTag();
        if (this.currentRecipe != null) {
            nbt.putString("current", this.currentRecipe.id().toString());
        }
        if (this.lastRecipe != null) {
            nbt.putString("last", this.lastRecipe.id().toString());
        }
        tag.put("recipeCtx", nbt);
    }

    @SuppressWarnings("unchecked")
    public void load(CompoundTag tag) {
        var level = this.levelGetter.get();
        if (level == null) {
            return;
        }
        var nbt = tag.getCompoundOrEmpty("recipeCtx");
        if (nbt.contains("current")) {
            try {
                var id = RECIPE_CODEC.parse(NbtOps.INSTANCE, tag.getCompoundOrEmpty("current")).getOrThrow();
                this.currentRecipe = (RecipeHolder<@NotNull T>) level.recipeAccess().byKey(id).orElse(null);
            } catch (Throwable e) {
                this.currentRecipe = null;
            }
        }
        if (nbt.contains("last")) {
            try {
                var id = RECIPE_CODEC.parse(NbtOps.INSTANCE, tag.getCompoundOrEmpty("last")).getOrThrow();
                this.lastRecipe = (RecipeHolder<@NotNull T>) level.recipeAccess().byKey(id).orElse(null);
            } catch (Throwable e) {
                this.lastRecipe = null;
            }
        }
    }

}
