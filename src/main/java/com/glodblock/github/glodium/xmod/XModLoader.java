package com.glodblock.github.glodium.xmod;

import com.glodblock.github.glodium.registry.RegistryHandler;

public interface XModLoader {

    String modid();

    void loadCommon();

    void loadClient();

    void onRegister(RegistryHandler handler);

}
