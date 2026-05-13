package com.glodblock.github.glodium.xmod;

import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.reflect.moon.Moon;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.util.GlodUtil;
import net.neoforged.fml.ModList;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public final class XModManager {

    private static final List<XMod> LOADERS = new ArrayList<>();

    public static void scan() {
        var annoType = Type.getType(ThirdParty.class);
        for (var mod : ModList.get().getMods()) {
            var modid = mod.getModId();
            var scanData = ModList.get().getModFileById(modid).getFile().getScanResult();
            for (var anno : scanData.getAnnotations()) {
                if (annoType.equals(anno.annotationType())) {
                    String xmod = (String) anno.annotationData().get("value");
                    LOADERS.add(new XMod(modid, xmod, anno.memberName()));
                }
            }
        }
    }

    public static void init() {
        for (var loader: LOADERS) {
            if (GlodUtil.checkMod(loader.xmod)) {
                try {
                    loader.instance = (XModLoader) Moon.instantiate(Class.forName(loader.pluginPath));
                } catch (Exception e) {
                    Glodium.LOGGER.error("Unable to load third party plugin: {} for mod: {}", loader.pluginPath, loader.xmod);
                }
            }
        }
    }

    public static void common() {
        for (var loader: LOADERS) {
            if (loader.instance != null) {
                loader.instance.loadCommon();
            }
        }
    }

    public static void client() {
        for (var loader: LOADERS) {
            if (loader.instance != null) {
                loader.instance.loadClient();
            }
        }
    }

    public static void register(String host, RegistryHandler handler) {
        for (var loader: LOADERS) {
            if (loader.instance != null && loader.host.equals(host)) {
                loader.instance.onRegister(handler);
            }
        }
    }

    static class XMod {

        private final String host;
        private final String xmod;
        private final String pluginPath;
        private XModLoader instance;

        XMod(String host, String xmod, String pluginPath) {
            this.host = host;
            this.xmod = xmod;
            this.pluginPath = pluginPath;
        }

    }

}
