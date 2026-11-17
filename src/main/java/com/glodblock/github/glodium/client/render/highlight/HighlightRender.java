package com.glodblock.github.glodium.client.render.highlight;

import com.glodblock.github.glodium.client.render.ColorData;
import net.minecraft.client.Minecraft;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

public class HighlightRender {
    public static final HighlightRender INSTANCE = new HighlightRender();
    private static final float LINE_WIDTH = 3;

    public static void hook(SubmitCustomGeometryEvent event) {
        HighlightRender.INSTANCE.tick();
    }

    public void tick() {
        var world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        this.invalidate();
        var drawList = HighlightHandler.getBlockData();
        if (drawList.isEmpty()) {
            return;
        }
        for (var block : drawList) {
            if (block.checkDim(world.dimension()) && block.allowRender()) {
                drawBlockOutline(block.box(), block.color());
            }
        }
    }

    private void invalidate() {
        while (HighlightHandler.getFirst() != null) {
            var info = HighlightHandler.getFirst();
            if (System.currentTimeMillis() > info.time()) {
                HighlightHandler.expire();
            } else {
                break;
            }
        }
    }

    private void drawBlockOutline(AABB box, ColorData color) {
        int argb = ARGB.colorFromFloat(color.getAf(), color.getRf(), color.getGf(), color.getBf());
        Gizmos.cuboid(box, GizmoStyle.stroke(argb, LINE_WIDTH)).setAlwaysOnTop();
    }
}
