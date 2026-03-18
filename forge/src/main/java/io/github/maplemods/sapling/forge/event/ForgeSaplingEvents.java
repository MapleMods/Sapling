package io.github.maplemods.sapling.forge.event;

import io.github.maplemods.sapling.cmd.MapleModsCommand;
import io.github.maplemods.sapling.event.SaplingEvents;
import io.github.maplemods.sapling.functions.ConfigFunctions;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeSaplingEvents {
	@SubscribeEvent
	public static void onServerStarted(ServerAboutToStartEvent e) {
		ConfigFunctions.initConfig(e.getServer());
	}

	@SubscribeEvent
	public static void onSpawn(EntityJoinLevelEvent e) {
		SaplingEvents.onEntityJoinLevel(e.getLevel(), e.getEntity());
	}

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent e) {
    	MapleModsCommand.register(e.getDispatcher());
    }
}
