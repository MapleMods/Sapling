package io.github.maplemods.sapling;


import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.neoforge.event.NeoForgeSaplingClientEvents;
import io.github.maplemods.sapling.neoforge.event.NeoForgeSaplingEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class NeoForgeMod {

    public NeoForgeMod(IEventBus eventBus) {
        CommonMod.init();

        eventBus.addListener(this::loadComplete);
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        NeoForge.EVENT_BUS.register(NeoForgeSaplingEvents.class);

        if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
            NeoForge.EVENT_BUS.register(NeoForgeSaplingClientEvents.class);
        }
    }
}