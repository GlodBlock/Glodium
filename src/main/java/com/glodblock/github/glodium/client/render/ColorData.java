package com.glodblock.github.glodium.client.render;

public class ColorData {

    float a;
    float r;
    float g;
    float b;

    public ColorData(float a, float r, float g, float b) {
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColorData(float r, float g, float b) {
        this(1, r, g, b);
    }

    public ColorData(int a, int r, int g, int b) {
        this(a / 255f, r / 255f, g / 255f, b / 255f);
    }

    public ColorData(int r, int g, int b) {
        this(255, r, g, b);
    }

    public ColorData(int argb) {
        this((argb >>> 24) & 0xFF, (argb >>> 16) & 0xFF, (argb >>> 8) & 0xFF, argb & 0xFF);
    }

    public float getAf() {
        return this.a;
    }

    public float getRf() {
        return this.r;
    }

    public float getGf() {
        return this.g;
    }

    public float getBf() {
        return this.b;
    }

    public int getAi() {
        return (int) (this.a * 255);
    }

    public int getRi() {
        return (int) (this.r * 255);
    }

    public int getGi() {
        return (int) (this.g * 255);
    }

    public int getBi() {
        return (int) (this.b * 255);
    }

    public int toARGB() {
        return (getAi() << 24) | (getRi() << 16) | (getGi() << 8) | getBi();
    }

    public int toRGBA() {
        return (getRi() << 24) | (getGi() << 16) | (getBi() << 8) | getAi();
    }

    public int toRGB() {
        return (getRi() << 16) | (getGi() << 8) | getBi();
    }

    @Override
    public String toString() {
        return "[a=%s, r=%s, g=%s, b=%s]".formatted(this.a, this.r, this.g, this.b);
    }

    @Override
    public int hashCode() {
        return toARGB();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ColorData cd) {
            return cd.toARGB() == toARGB();
        }
        return false;
    }
}
