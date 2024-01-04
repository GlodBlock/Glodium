package com.glodblock.github.glodium.reflect;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;

import java.lang.reflect.Field;

public class ClientReflect {

    private static final Field fLevelRenderer_renderBuffers;

    static {
        try {
            fLevelRenderer_renderBuffers = ReflectKit.reflectField(LevelRenderer.class, "renderBuffers", "f_109464_");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize reflection hacks!", e);
        }
    }

    public static RenderBuffers getRenderBuffers(LevelRenderer owner) {
        return ReflectKit.readField(owner, fLevelRenderer_renderBuffers);
    }

}
