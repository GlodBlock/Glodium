package com.glodblock.github.glodium.registry;

import com.glodblock.github.glodium.Glodium;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RegistryHandler {

    protected final String id;
    protected final List<Pair<String, Block>> blocks = new ArrayList<>();
    protected final List<Pair<String, Item>> items = new ArrayList<>();
    protected final List<Pair<String, BlockEntityType<?>>> tiles = new ArrayList<>();
    protected final Object2ReferenceMap<String, Function<Block, Item>> itemBlocks = new Object2ReferenceOpenHashMap<>();

    public RegistryHandler(String modid) {
        this.id = modid;
    }

    public void block(String name, Block block) {
        this.blocks.add(Pair.of(name, block));
    }

    public void block(String name, Block block, Function<Block, Item> itemWrapper) {
        this.blocks.add(Pair.of(name, block));
        this.itemBlocks.put(name, itemWrapper);
    }

    public void item(String name, Item item) {
        this.items.add(Pair.of(name, item));
    }

    public void tile(String name, BlockEntityType<?> type) {
        this.tiles.add(Pair.of(name, type));
    }

    public void runRegister() {
        onRegisterBlocks();
        onRegisterItems();
        onRegisterTileEntities();
    }

    protected void onRegisterBlocks() {
        this.blocks.forEach(e -> Registry.register(BuiltInRegistries.BLOCK, Glodium.id(this.id, e.getLeft()), e.getRight()));
    }

    protected void onRegisterItems() {
        for (Pair<String, Block> e : blocks) {
            if (this.itemBlocks.containsKey(e.getLeft())) {
                Registry.register(BuiltInRegistries.ITEM, Glodium.id(this.id, e.getLeft()), this.itemBlocks.get(e.getLeft()).apply(e.getRight()));
            } else {
                Registry.register(BuiltInRegistries.ITEM, Glodium.id(this.id, e.getLeft()), new BlockItem(e.getRight(), new Item.Properties()));
            }
        }
        this.items.forEach(e -> Registry.register(BuiltInRegistries.ITEM, Glodium.id(this.id, e.getLeft()), e.getRight()));
    }

    protected void onRegisterTileEntities() {
        this.tiles.forEach(e -> Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Glodium.id(this.id, e.getLeft()), e.getRight()));
    }

}
