package com.glodblock.github.glodium.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class GlodClientUtil {

    public static Vec3 rotor(Vec3 point, Vec3 center, Direction.Axis axis, float a) {
        Vec3 normal = Vec3.ZERO;
        switch (axis) {
            case X -> normal = point.subtract(center).xRot(a);
            case Y -> normal = point.subtract(center).yRot(a);
            case Z -> normal = point.subtract(center).zRot(a);
        }
        return normal.add(center);
    }

}
