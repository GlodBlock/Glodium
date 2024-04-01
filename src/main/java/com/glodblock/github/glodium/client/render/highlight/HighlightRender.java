package com.glodblock.github.glodium.client.render.highlight;

import com.glodblock.github.glodium.client.render.ColorData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.OptionalDouble;

public class HighlightRender extends RenderType {

    public static final HighlightRender INSTANCE = new HighlightRender();
    private final LineStateShard LINE_3 = new LineStateShard(OptionalDouble.of(3.0));
    private final RenderType BLOCK_HIGHLIGHT_LINE = create("glodium_block_highlight_line",
            DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 65536, false, false,
            CompositeState.builder()
                    .setLineState(LINE_3)
                    .setTransparencyState(TransparencyStateShard.GLINT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .createCompositeState(false)
    );

    public static void hook(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            HighlightRender.INSTANCE.tick(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), event.getCamera());
        }
    }

    public void tick(PoseStack stack, MultiBufferSource.BufferSource multiBuf, Camera camera) {
        var world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        this.invalidate();
        var drawList = HighlightHandler.getBlockData();
        if (drawList.isEmpty()) {
            return;
        }
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        for (var block : drawList) {
            if (block.checkDim(world.dimension()) && block.allowRender()) {
                drawBlockOutline(block.box(), block.color(), stack, camera, multiBuf);
            }
        }
        multiBuf.endBatch();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
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

    private void drawBlockOutline(AABB box, ColorData color, PoseStack stack, Camera camera, MultiBufferSource multiBuf) {
        var r = color.getRf();
        var g = color.getGf();
        var b = color.getBf();
        var a = color.getAf();
        if (camera.isInitialized()) {
            Vec3 vec3 = camera.getPosition().reverse();
            AABB aabb = box.move(vec3);
            var topRight = new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ);
            var bottomRight = new Vec3(aabb.maxX, aabb.minY, aabb.maxZ);
            var bottomLeft = new Vec3(aabb.minX, aabb.minY, aabb.maxZ);
            var topLeft = new Vec3(aabb.minX, aabb.maxY, aabb.maxZ);
            var topRight2 = new Vec3(aabb.maxX, aabb.maxY, aabb.minZ);
            var bottomRight2 = new Vec3(aabb.maxX, aabb.minY, aabb.minZ);
            var bottomLeft2 = new Vec3(aabb.minX, aabb.minY, aabb.minZ);
            var topLeft2 = new Vec3(aabb.minX, aabb.maxY, aabb.minZ);
            var buf = multiBuf.getBuffer(BLOCK_HIGHLIGHT_LINE);
            renderBox(buf, stack, topLeft, bottomLeft, topRight, bottomRight, r, g, b, a);
            renderBox(buf, stack, topLeft2, bottomLeft2, topRight2, bottomRight2, r, g, b, a);
            renderLine(buf, stack, topRight, topRight2, r, g, b, a);
            renderLine(buf, stack, bottomRight, bottomRight2, r, g, b, a);
            renderLine(buf, stack, bottomLeft, bottomLeft2, r, g, b, a);
            renderLine(buf, stack, topLeft, topLeft2, r, g, b, a);
        }
    }

    private void renderBox(VertexConsumer buf, PoseStack stack, Vec3 topLeft, Vec3 bottomLeft, Vec3 topRight, Vec3 bottomRight, float r, float g, float b, float a) {
        renderLine(buf, stack, topLeft, bottomLeft, r, g, b, a);
        renderLine(buf, stack, topLeft, topRight, r, g, b, a);
        renderLine(buf, stack, bottomRight, bottomLeft, r, g, b, a);
        renderLine(buf, stack, bottomRight, topRight, r, g, b, a);
    }

    private void renderLine(VertexConsumer buf, PoseStack pose, Vec3 from, Vec3 to, float r, float g, float b, float a) {
        var mat = pose.last().pose();
        var normal = from.subtract(to);
        buf.vertex(mat, (float) from.x, (float) from.y, (float) from.z).color(r, g, b, a).normal((float) normal.x, (float) normal.y, (float) normal.z).endVertex();
        buf.vertex(mat, (float) to.x, (float) to.y, (float) to.z).color(r, g, b, a).normal((float) normal.x, (float) normal.y, (float) normal.z).endVertex();
    }

    private HighlightRender() {
        super("", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 0, false, false, () -> {}, () -> {});
    }

}
