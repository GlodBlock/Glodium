package com.glodblock.github.glodium.registry;

import com.glodblock.github.glodium.registry.defer.DeferredDataComponentType;
import com.glodblock.github.glodium.registry.defer.DeferredTileEntityType;
import com.glodblock.github.glodium.registry.token.TileToken;
import com.glodblock.github.glodium.xmod.XModManager;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class RegistryHandler {

    protected final String id;
    protected final DeferredRegister.Items items;
    protected final DeferredRegister.Blocks blocks;
    protected final DeferredRegister<@NotNull BlockEntityType<?>> tiles;
    protected final DeferredRegister.DataComponents components;
    protected final List<Pair<TileToken, Block[]>> tileBind = new ArrayList<>();
    protected final List<TileToken> tileTypes = new ArrayList<>();
    protected final List<TileCapabilityMap<?, ?, ?>> tileCaps = new ArrayList<>();
    protected final List<ItemCapabilityMap<?, ?, ?>> itemCaps = new ArrayList<>();

    public RegistryHandler(String modid, IEventBus modBus) {
        this.id = modid;
        this.items = DeferredRegister.createItems(modid);
        this.blocks = DeferredRegister.createBlocks(modid);
        this.tiles = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, modid);
        this.components = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, modid);
        this.blocks.register(modBus);
        this.items.register(modBus);
        this.tiles.register(modBus);
        this.components.register(modBus);
        modBus.addListener(BlockEntityTypeAddBlocksEvent.class, this::onBindTileEntity);
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

    public <T extends Block> DeferredBlock<@NotNull T> block(String name, Supplier<T> builder) {
        return this.block(name, builder, block -> new BlockItem(block, new Item.Properties()));
    }

    public <T extends Block> DeferredBlock<@NotNull T> block(String name, Supplier<T> builder, Function<Block, Item> itemWrapper) {
        var block = this.blocks.register(name, builder);
        this.items.register(name, () -> itemWrapper.apply(block.get()));
        return block;
    }

    public <T extends Item> DeferredItem<@NotNull T> item(String name, Supplier<T> builder) {
        return this.items.register(name, builder);
    }

    public <T extends Item> DeferredItem<@NotNull T> item(String name, Function<Item.Properties, T> builder) {
        return this.item(name, builder, new Item.Properties());
    }

    public DeferredItem<@NotNull Item> item(String name, Item.Properties properties) {
        return this.item(name, Item::new, properties);
    }

    public <T extends Item> DeferredItem<@NotNull T> item(String name, Function<Item.Properties, T> builder, Item.Properties properties) {
        return this.items.register(name, () -> builder.apply(properties));
    }

    public <T extends BlockEntity> DeferredTileEntityType<T> tile(String name, Class<T> tileClass, BlockEntityType.BlockEntitySupplier<@NotNull T> factory) {
        var type = (DeferredTileEntityType<T>) this.tiles.register(name, () -> new BlockEntityType<>(factory, Set.of()));
        this.tileTypes.add(new TileToken(type, tileClass));
        return type;
    }

    public <T> DeferredDataComponentType<T> comp(String name, UnaryOperator<DataComponentType.Builder<@NotNull T>> builder) {
        return (DeferredDataComponentType<T>) this.components.registerComponentType(name, builder);
    }

    public <T> DeferredDataComponentType<T> comp(String name, Codec<T> codec) {
        return this.comp(name, builder -> builder.persistent(codec));
    }

    public <T> DeferredDataComponentType<T> comp(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, @NotNull T> netCodec) {
        return this.comp(name, builder -> builder.persistent(codec).networkSynchronized(netCodec));
    }

    public void bind(Supplier<BlockEntityType<?>> type, Class<? extends BlockEntity> tileClass, Block... blocks) {
        this.tileBind.add(Pair.of(new TileToken(type, tileClass), blocks));
    }

    public <T, C, X> void cap(Class<T> capInterface, BlockCapability<@NotNull C, X> cap, ICapabilityProvider<@NotNull T, X, @NotNull C> map) {
        this.tileCaps.add(new TileCapabilityMap<>(capInterface, cap, map));
    }

    public <T, C, X> void cap(Class<T> capInterface, ItemCapability<@NotNull C, X> cap, ICapabilityProvider<@NotNull ItemStack, X, @NotNull C> map) {
        this.itemCaps.add(new ItemCapabilityMap<>(capInterface, cap, map));
    }

    protected void onBindTileEntity(BlockEntityTypeAddBlocksEvent event) {
        this.tileBind.forEach(c -> event.modify(c.getKey().type().get(), c.getValue()));
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
