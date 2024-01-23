package com.glodblock.github.glodium.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class TargetPoint {

    public final ServerPlayer excluded;
    public final double x;
    public final double y;
    public final double z;
    public final double r2;
    public final Level level;

    public TargetPoint(double x, double y, double z, double r2, Level level) {
        this(null, x, y, z, r2, level);
    }

    public TargetPoint(ServerPlayer excluded, double x, double y, double z, double r2, Level level) {
        this.excluded = excluded;
        this.x = x;
        this.y = y;
        this.z = z;
        this.r2 = r2;
        this.level = level;
    }

    public static TargetPoint at(double x, double y, double z, double r2, Level level) {
        return new TargetPoint(x, y, z, r2, level);
    }

    public static TargetPoint at(ServerPlayer excluded, double x, double y, double z, double r2, Level level) {
        return new TargetPoint(excluded, x, y, z, r2, level);
    }

}
