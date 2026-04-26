package io.github.maplemods.sapling.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Set;

public class Constants {
	public static final String MOD_ID = "sapling";
	public static final String MOD_NAME = "Sapling";
	public static final String MOD_VERSION = "2.4";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final RandomSource randomSource = RandomSource.create();
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM");

	public static final Set<String> SEASONAL_CATEGORIES = Set.of("spring", "summer", "autumn", "winter");
}