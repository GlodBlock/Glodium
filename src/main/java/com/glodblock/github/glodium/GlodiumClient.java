package com.glodblock.github.glodium;

import com.glodblock.github.glodium.client.render.highlight.HighlightRender;
import net.fabricmc.api.ClientModInitializer;

public class GlodiumClient extends Glodium implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HighlightRender.hook();
    }

}
