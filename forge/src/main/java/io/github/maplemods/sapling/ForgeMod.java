package io.github.maplemods.sapling;

import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.forge.event.ForgeSaplingClientEvents;
import io.github.maplemods.sapling.forge.event.ForgeSaplingEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class ForgeMod {

    public ForgeMod(FMLJavaModLoadingContext modLoadingContext) {
        CommonMod.init();

        IEventBus modEventBus = modLoadingContext.getModEventBus();
        modEventBus.addListener(this::loadComplete);
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        MinecraftForge.EVENT_BUS.register(ForgeSaplingEvents.class);

        if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
            MinecraftForge.EVENT_BUS.register(ForgeSaplingClientEvents.class);
        }
    }
}