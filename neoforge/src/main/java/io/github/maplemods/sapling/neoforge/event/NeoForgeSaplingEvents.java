package io.github.maplemods.sapling.neoforge.event;

import io.github.maplemods.sapling.cmd.MapleModsCommand;
import io.github.maplemods.sapling.event.SaplingEvents;
import io.github.maplemods.sapling.functions.ConfigFunctions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

public class NeoForgeSaplingEvents {
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
