package com.glodblock.github.glodium.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

public abstract class GlodUtil {

    private GlodUtil() {
        // NO-OP
    }

    public static <T> DataComponentType<T> getComponentType(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> stream) {
        return DataComponentType.<T>builder().persistent(codec).networkSynchronized(stream).build();
    }

    public static boolean checkInvalidRL(String rl, Registry<?> registry) {
        return checkInvalidRL(Identifier.parse(rl), registry);
    }

    public static boolean checkInvalidRL(Identifier rl, Registry<?> registry) {
        return registry.containsKey(rl);
    }

    public static double clamp(double num, double floor, double ceil) {
        return Math.min(ceil, Math.max(floor, num));
    }

    public static long clamp(long num, long floor, long ceil) {
        return Math.min(ceil, Math.max(floor, num));
    }

    public static boolean checkMod(String modid) {
        if (ModList.get() == null) {
            return FMLLoader.getCurrent().getLoadingModList().getMods()
                    .stream().map(ModInfo::getModId)
                    .anyMatch(modid::equals);
        } else {
            return ModList.get().isLoaded(modid);
        }
    }

}
