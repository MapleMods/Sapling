package io.github.maplemods.sapling.data;

import net.minecraft.util.RandomSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String MOD_ID = "sapling";
	public static final String MOD_NAME = "Sapling";
	public static final String MOD_VERSION = "1.3";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final RandomSource randomSource = RandomSource.create();
}