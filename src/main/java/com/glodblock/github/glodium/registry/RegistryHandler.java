package com.glodblock.github.glodium.registry;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
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
    protected final DeferredRegister<Block> BLOCK;
    protected final DeferredRegister<Item> ITEM;
    protected final DeferredRegister<BlockEntityType<?>> TILE_TYPE;

    public RegistryHandler(String modid) {
        this.id = modid;
        this.BLOCK = DeferredRegister.create(Registries.BLOCK, modid);
        this.ITEM = DeferredRegister.create(Registries.ITEM, modid);
        this.TILE_TYPE = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modid);
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

    public final void runRegister(IEventBus bus) {
        doRegister();
        collectHolder(bus);
    }

    @MustBeInvokedByOverriders
    public void collectHolder(IEventBus bus) {
        this.BLOCK.register(bus);
        this.ITEM.register(bus);
        this.TILE_TYPE.register(bus);
    }

    @MustBeInvokedByOverriders
    public void doRegister() {
        onRegisterBlocks();
        onRegisterItems();
        onRegisterTileEntities();
    }

    protected void onRegisterBlocks() {
        this.blocks.forEach(e -> this.BLOCK.register(e.getLeft(), e::getRight));
    }

    protected void onRegisterItems() {
        for (Pair<String, Block> entry : blocks) {
            if (this.itemBlocks.containsKey(entry.getLeft())) {
                this.ITEM.register(entry.getLeft(), () -> this.itemBlocks.get(entry.getLeft()).apply(entry.getRight()));
            } else {
                this.ITEM.register(entry.getLeft(), () -> new BlockItem(entry.getRight(), new Item.Properties()));
            }
        }
        this.items.forEach(e -> this.ITEM.register(e.getLeft(), e::getRight));
    }

    protected void onRegisterTileEntities() {
        this.tiles.forEach(e -> this.TILE_TYPE.register(e.getLeft(), e::getRight));
    }

}
