package com.glodblock.github.glodium.util;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

public class GlodUtil {

    private static final Object2ReferenceMap<Class<?>, BlockEntityType<? extends BlockEntity>> TILE_CACHE = new Object2ReferenceOpenCustomHashMap<>(HashUtil.CLASS);
    private static final Reference2ObjectMap<BlockEntityType<? extends BlockEntity>, Class<?>> REVERSE_CACHE = new Reference2ObjectOpenHashMap<>();

    @SuppressWarnings("all")
    public static <T extends BlockEntity> BlockEntityType<T> getTileType(Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier, Block block) {
        if (block == null) {
            return (BlockEntityType<T>) TILE_CACHE.get(clazz);
        }
        var type = (BlockEntityType<T>) TILE_CACHE.computeIfAbsent(
                clazz,
                k -> BlockEntityType.Builder.of(supplier, block).build(null)
        );
        if (!REVERSE_CACHE.containsKey(type)) {
            REVERSE_CACHE.put(type, clazz);
        }
        return type;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityType<T> getTileType(Class<T> clazz) {
        if (!TILE_CACHE.containsKey(clazz)) {
            throw new IllegalArgumentException(String.format("%s isn't an tile entity!", clazz.getName()));
        }
        return (BlockEntityType<T>) TILE_CACHE.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> Class<T> getTileClass(BlockEntityType<T> type) {
        if (!REVERSE_CACHE.containsKey(type)) {
            throw new IllegalArgumentException(String.format("%s isn't registered!", type));
        }
        return (Class<T>) REVERSE_CACHE.get(type);
    }

    public static <T> DataComponentType<T> getComponentType(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> stream) {
        return DataComponentType.<T>builder().persistent(codec).networkSynchronized(stream).build();
    }

    public static boolean checkInvalidRL(String rl, Registry<?> registry) {
        return checkInvalidRL(ResourceLocation.parse(rl), registry);
    }

    public static boolean checkInvalidRL(ResourceLocation rl, Registry<?> registry) {
        return registry.containsKey(rl);
    }

    public static double clamp(double num, double floor, double ceil) {
        return Math.min(ceil, Math.max(floor, num));
    }

    public static boolean checkMod(String modid) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods()
                    .stream().map(ModInfo::getModId)
                    .anyMatch(modid::equals);
        } else {
            return ModList.get().isLoaded(modid);
        }
    }

}
