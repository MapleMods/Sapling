package io.github.maplemods.sapling;

import net.fabricmc.api.ModInitializer;

public class FabricMod implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonMod.init();
    }
}
