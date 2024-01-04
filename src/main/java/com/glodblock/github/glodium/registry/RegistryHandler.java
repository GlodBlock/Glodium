package com.glodblock.github.glodium.registry;

import com.glodblock.github.glodium.Glodium;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;

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
        blocks.add(Pair.of(name, block));
    }

    public void block(String name, Block block, Function<Block, Item> itemWrapper) {
        block(name, block);
        itemBlocks.put(name, itemWrapper);
    }

    public void item(String name, Item item) {
        items.add(Pair.of(name, item));
    }

    public void tile(String name, BlockEntityType<?> type) {
        tiles.add(Pair.of(name, type));
    }

    @SubscribeEvent
    public final void runRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            this.register(event);
        }
    }

    public void register(RegisterEvent event) {
        onRegisterBlocks();
        onRegisterItems();
        onRegisterTileEntities();
    }

    protected void onRegisterBlocks() {
        for (Pair<String, Block> entry : blocks) {
            String key = entry.getLeft();
            Block block = entry.getRight();
            ForgeRegistries.BLOCKS.register(Glodium.id(this.id, key), block);
        }
    }

    protected void onRegisterItems() {
        for (Pair<String, Block> entry : blocks) {
            if (this.itemBlocks.containsKey(entry.getLeft())) {
                ForgeRegistries.ITEMS.register(Glodium.id(this.id, entry.getLeft()), this.itemBlocks.get(entry.getLeft()).apply(entry.getRight()));
            } else {
                ForgeRegistries.ITEMS.register(Glodium.id(this.id, entry.getLeft()), new BlockItem(entry.getRight(), new Item.Properties()));
            }
        }
        for (Pair<String, Item> entry : items) {
            ForgeRegistries.ITEMS.register(Glodium.id(this.id, entry.getLeft()), entry.getRight());
        }
    }

    protected void onRegisterTileEntities() {
        for (Pair<String, BlockEntityType<?>> entry : tiles) {
            ForgeRegistries.BLOCK_ENTITY_TYPES.register(Glodium.id(this.id, entry.getLeft()), entry.getRight());
        }
    }

}
