package com.glodblock.github.glodium.registry;

import com.glodblock.github.glodium.registry.defer.DeferredDataComponentRegister;
import com.glodblock.github.glodium.registry.defer.DeferredDataComponentType;
import com.glodblock.github.glodium.registry.defer.DeferredTileEntityType;
import com.glodblock.github.glodium.registry.defer.DeferredTileTypeRegister;
import com.glodblock.github.glodium.registry.token.TileToken;
import com.glodblock.github.glodium.xmod.XModManager;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegistryHandler {

    protected final String id;
    protected final DeferredRegister.Items items;
    protected final DeferredRegister.Blocks blocks;
    protected final DeferredTileTypeRegister tiles;
    protected final DeferredDataComponentRegister components;
    protected final List<Pair<TileToken, Set<Block>>> tileBind = new ArrayList<>();
    protected final List<TileToken> tileTypes = new ArrayList<>();
    protected final List<TileCapabilityMap<?, ?, ?>> tileCaps = new ArrayList<>();
    protected final List<ItemCapabilityMap<?, ?, ?>> itemCaps = new ArrayList<>();

    public RegistryHandler(String modid, IEventBus modBus) {
        this.id = modid;
        this.items = DeferredRegister.createItems(modid);
        this.blocks = DeferredRegister.createBlocks(modid);
        this.tiles = new DeferredTileTypeRegister(modid);
        this.components = new DeferredDataComponentRegister(modid);
        this.blocks.register(modBus);
        this.items.register(modBus);
        this.tiles.register(modBus);
        this.components.register(modBus);
        modBus.addListener(RegisterCapabilitiesEvent.class, event -> {
            for (var token : this.tileTypes) {
                this.tileCaps.forEach(tcm -> tcm.register(event, token));
            }
            for (var item : this.items.getEntries()) {
                this.itemCaps.forEach(icm -> icm.register(event, item.get()));
            }
        });
        XModManager.register(modid, this);
    }

    public <T extends Block> DeferredBlock<@NotNull T> block(String name, Function<BlockBehaviour.Properties, T> builder, BlockBehaviour.Properties properties) {
        return this.block(name, builder, properties, BlockItem::new, new Item.Properties());
    }

    public <T extends Block> DeferredBlock<@NotNull T> block(String name, Function<BlockBehaviour.Properties, T> builder, BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        return this.block(name, builder, properties, BlockItem::new, itemProperties);
    }

    public <T extends Block> DeferredBlock<@NotNull T> block(String name, Function<BlockBehaviour.Properties, T> builder, BlockBehaviour.Properties properties, BiFunction<Block, Item.Properties, Item> itemWrapper) {
        return this.block(name, builder, properties, itemWrapper, new Item.Properties());
    }

    public <T extends Block> DeferredBlock<@NotNull T> block(String name, Function<BlockBehaviour.Properties, T> builder, BlockBehaviour.Properties properties, BiFunction<Block, Item.Properties, Item> itemWrapper, Item.Properties itemProperties) {
        var block = this.blocks.register(name, key -> builder.apply(properties.setId(ResourceKey.create(Registries.BLOCK, key))));
        this.item(name, prop -> itemWrapper.apply(block.get(), prop), itemProperties);
        return block;
    }

    public <T extends Item> DeferredItem<@NotNull T> item(String name, Function<Item.Properties, T> builder) {
        return this.item(name, builder, new Item.Properties());
    }

    public DeferredItem<@NotNull Item> item(String name, Item.Properties properties) {
        return this.item(name, Item::new, properties);
    }

    public <T extends Item> DeferredItem<@NotNull T> item(String name, Function<Item.Properties, T> builder, Item.Properties properties) {
        return this.items.register(name, key -> builder.apply(properties.setId(ResourceKey.create(Registries.ITEM, key))));
    }

    public <T extends BlockEntity> DeferredTileEntityType<T> tile(String name, Class<T> tileClass, BlockEntityType.BlockEntitySupplier<@NotNull T> factory, DeferredBlock<?>... blocks) {
        var blockSet = Stream.of(blocks).map(DeferredHolder::get).map(b -> (Block) b).collect(Collectors.toSet());
        var type = (DeferredTileEntityType<T>) this.tiles.register(name, () -> new BlockEntityType<>(factory, blockSet));
        var token = new TileToken(type, tileClass);
        this.tileTypes.add(token);
        this.tileBind.add(Pair.of(token, blockSet));
        return type;
    }

    public <T> DeferredDataComponentType<T> comp(String name, UnaryOperator<DataComponentType.Builder<@NotNull T>> builder) {
        return (DeferredDataComponentType<T>) this.components.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }

    public <T> DeferredDataComponentType<T> comp(String name, Codec<T> codec) {
        return this.comp(name, builder -> builder.persistent(codec));
    }

    public <T> DeferredDataComponentType<T> comp(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, @NotNull T> netCodec) {
        return this.comp(name, builder -> builder.persistent(codec).networkSynchronized(netCodec));
    }

    public <T, C, X> void cap(Class<T> capInterface, BlockCapability<@NotNull C, X> cap, ICapabilityProvider<@NotNull T, X, @NotNull C> map) {
        this.tileCaps.add(new TileCapabilityMap<>(capInterface, cap, map));
    }

    public <T, C, X> void cap(Class<T> capInterface, ItemCapability<@NotNull C, X> cap, ICapabilityProvider<@NotNull ItemStack, X, @NotNull C> map) {
        this.itemCaps.add(new ItemCapabilityMap<>(capInterface, cap, map));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public record TileCapabilityMap<T, C, X>(Class<T> capInterface, BlockCapability<@NotNull C, X> cap, ICapabilityProvider<@NotNull T, X, @NotNull C> map) {

        public void register(RegisterCapabilitiesEvent event, TileToken token) {
            if (capInterface.isAssignableFrom(token.token())) {
                event.registerBlockEntity(
                        (BlockCapability) this.cap,
                        token.type().get(),
                        (ICapabilityProvider) this.map
                );
            }
        }

    }

    public record ItemCapabilityMap<T, C, X>(Class<T> capInterface, ItemCapability<@NotNull C, X> cap, ICapabilityProvider<@NotNull ItemStack, X, @NotNull C> map) {

        public void register(RegisterCapabilitiesEvent event, Item item) {
            if (capInterface.isAssignableFrom(item.getClass())) {
                event.registerItem(
                        this.cap,
                        this.map,
                        item
                );
            }
        }

    }

}
