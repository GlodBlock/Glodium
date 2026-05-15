package com.glodblock.github.glodium.client.render.highlight;

import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.client.render.ColorData;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class HighlightRender {

    public static final HighlightRender INSTANCE = new HighlightRender();
    private final RenderPipeline.Snippet BLOCK_HIGHLIGHT_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withVertexShader("core/rendertype_lines")
            .withFragmentShader("core/rendertype_lines")
            .withColorTargetState(new ColorTargetState(BlendFunction.GLINT))
            .withDepthStencilState(new DepthStencilState(CompareOp.NOT_EQUAL, true))
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
            .buildSnippet();
    private final RenderPipeline BLOCK_HIGHLIGHT_PIPELINE = RenderPipeline.builder(BLOCK_HIGHLIGHT_SNIPPET)
            .withLocation(Glodium.id(Glodium.MODID, "pipeline/block_highlight"))
            .build();

    private final RenderType BLOCK_HIGHLIGHT_LINE = RenderType.create(
            "glodium:block_highlight_line",
            RenderSetup.builder(BLOCK_HIGHLIGHT_PIPELINE)
                    .bufferSize(2048)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .createRenderSetup()
    );

    public static void hook(RenderLevelStageEvent.AfterTranslucentParticles event) {
        HighlightRender.INSTANCE.tick(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), Minecraft.getInstance().gameRenderer.getMainCamera());
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
        for (var block : drawList) {
            if (block.checkDim(world.dimension()) && block.allowRender()) {
                drawBlockOutline(block.box(), block.color(), stack, camera, multiBuf);
            }
        }
        multiBuf.endBatch();
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
            Vec3 vec3 = camera.position().reverse();
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
        buf.addVertex(mat, (float) from.x, (float) from.y, (float) from.z).setColor(r, g, b, a).setNormal(pose.last(), (float) normal.x, (float) normal.y, (float) normal.z).setLineWidth(3);
        buf.addVertex(mat, (float) to.x, (float) to.y, (float) to.z).setColor(r, g, b, a).setNormal(pose.last(), (float) normal.x, (float) normal.y, (float) normal.z).setLineWidth(3);
    }

}
