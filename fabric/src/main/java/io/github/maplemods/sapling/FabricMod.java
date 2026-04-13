package io.github.maplemods.sapling;

import io.github.maplemods.sapling.cmd.MapleModsCommand;
import io.github.maplemods.sapling.event.SaplingEvents;
import io.github.maplemods.sapling.functions.ConfigFunctions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FabricMod implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonMod.init();

		ServerLifecycleEvents.SERVER_STARTING.register(ConfigFunctions::initConfig);

		ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
			SaplingEvents.onEntityJoinLevel(serverLevel, entity);
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			MapleModsCommand.register(dispatcher);
		});
    }
}
