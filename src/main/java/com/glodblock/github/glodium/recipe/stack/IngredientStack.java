package com.glodblock.github.glodium.recipe.stack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.function.Predicate;

public abstract class IngredientStack<T, S extends Predicate<T>> {

    protected final S ingredient;
    protected int amount;

    public static final Codec<Item> ITEM_CODEC = RecordCodecBuilder.create(
            builder -> builder
                    .group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(i -> i.ingredient),
                            ExtraCodecs.POSITIVE_INT.optionalFieldOf("amount", 1).forGetter(i -> i.amount)
                    ).apply(builder, Item::new)
    );
    public static final Codec<Fluid> FLUID_CODEC = RecordCodecBuilder.create(
            builder -> builder
                    .group(
                            FluidIngredient.CODEC.fieldOf("ingredient").forGetter(i -> i.ingredient),
                            ExtraCodecs.POSITIVE_INT.optionalFieldOf("amount", 1).forGetter(i -> i.amount)
                    ).apply(builder, Fluid::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Item> ITEM_STREAM_CODEC = StreamCodec.of(
            (buf, s) -> s.to(buf),
            IngredientStack::ofItem
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientStack.Fluid> FLUID_STREAM_CODEC = StreamCodec.of(
            (buf, s) -> s.to(buf),
            IngredientStack::ofFluid
    );

    public IngredientStack(S ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public S getIngredient() {
        return this.ingredient;
    }

    public int getAmount() {
        return this.amount;
    }

    public static IngredientStack.Item of(ItemStack ingredient) {
        return new Item(Ingredient.of(ingredient), ingredient.getCount());
    }

    public static IngredientStack.Item of(Ingredient ingredient, int amount) {
        return new Item(ingredient, amount);
    }

    public static IngredientStack.Fluid of(FluidStack ingredient) {
        return new Fluid(FluidIngredient.of(ingredient), ingredient.getAmount());
    }

    public static IngredientStack.Fluid of(FluidIngredient ingredient, int amount) {
        return new Fluid(ingredient, amount);
    }

    public static IngredientStack.Item ofItem(RegistryFriendlyByteBuf buff) {
        return new Item(Ingredient.CONTENTS_STREAM_CODEC.decode(buff), buff.readInt());
    }

    public static IngredientStack.Fluid ofFluid(RegistryFriendlyByteBuf buff) {
        return new Fluid(FluidIngredient.STREAM_CODEC.decode(buff), buff.readInt());
    }

    public abstract void to(RegistryFriendlyByteBuf buff);

    @SuppressWarnings("unchecked")
    public void consume(Object stack) {
        if (this.amount <= 0) {
            return;
        }
        if (this.ingredient.test((T) stack)) {
            int from = getStackAmount((T) stack);
            if (from > this.amount) {
                this.setStackAmount((T) stack, from - this.amount);
                this.amount = 0;

            } else {
                this.setStackAmount((T) stack, 0);
                this.amount -= from;
            }
        }
    }

    public boolean isEmpty() {
        return this.amount <= 0;
    }

    public abstract boolean checkType(Object obj);

    public abstract IngredientStack<T, S> sample();

    public abstract int getStackAmount(T stack);

    public abstract void setStackAmount(T stack, int amount);

    @Override
    public String toString() {
        return this.amount + "x" +this.ingredient;
    }

    public static final class Item extends IngredientStack<ItemStack, Ingredient> {

        public static final Item EMPTY = new Item(Ingredient.EMPTY, 0);

        private Item(Ingredient ingredient, int amount) {
            super(ingredient, amount);
        }

        @Override
        public void to(RegistryFriendlyByteBuf buff) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buff, this.ingredient);
            buff.writeInt(this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof ItemStack;
        }

        @Override
        public Item sample() {
            return new Item(this.ingredient, this.amount);
        }

        @Override
        public int getStackAmount(ItemStack stack) {
            return stack.getCount();
        }

        @Override
        public void setStackAmount(ItemStack stack, int amount) {
            stack.setCount(amount);
        }

    }

    public static final class Fluid extends IngredientStack<FluidStack, FluidIngredient> {

        public static final Fluid EMPTY = new Fluid(FluidIngredient.empty(), 0);

        private Fluid(FluidIngredient ingredient, int amount) {
            super(ingredient, amount);
        }

        @Override
        public void to(RegistryFriendlyByteBuf buff) {
            FluidIngredient.STREAM_CODEC.encode(buff, this.ingredient);
            buff.writeInt(this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof FluidStack;
        }

        @Override
        public Fluid sample() {
            return new Fluid(this.ingredient, this.amount);
        }

        @Override
        public int getStackAmount(FluidStack stack) {
            return stack.getAmount();
        }

        @Override
        public void setStackAmount(FluidStack stack, int amount) {
            stack.setAmount(amount);
        }
    }

}
