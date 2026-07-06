package io.github.maplemods.sapling.config;

public class ConfigHandler {

	// Defaults
	public static final boolean DEFAULT_REPLACE_SEASONAL_TEXTURES = true;
	public static final boolean DEFAULT_DISABLE_SEASONS_DURING_HOLIDAY = false;
	public static final boolean DEFAULT_NAMETAG_PREVENTS_TEXTURE_CHANGE = true;

	public boolean replaceSeasonalTextures;
	public boolean disableSeasonsDuringHoliday;
	public boolean nameTagPreventsTextureChange;

	public ConfigHandler() {
		this.replaceSeasonalTextures = DEFAULT_REPLACE_SEASONAL_TEXTURES;
		this.disableSeasonsDuringHoliday = DEFAULT_DISABLE_SEASONS_DURING_HOLIDAY;
		this.nameTagPreventsTextureChange = DEFAULT_NAMETAG_PREVENTS_TEXTURE_CHANGE;
	}

	public ConfigHandler(boolean replaceSeasonalTextures, boolean disableSeasonsDuringHoliday, boolean nameTagPreventsTextureChange) {
		this.replaceSeasonalTextures = replaceSeasonalTextures;
		this.disableSeasonsDuringHoliday = disableSeasonsDuringHoliday;
		this.nameTagPreventsTextureChange = nameTagPreventsTextureChange;
	}
}