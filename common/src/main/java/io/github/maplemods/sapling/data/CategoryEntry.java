package io.github.maplemods.sapling.data;

import java.time.MonthDay;
import java.util.List;

public class CategoryEntry {
    public final String category;
    public final MonthDay dateFrom;
    public final MonthDay dateTo;
    public List<TextureEntry> textures;

    public CategoryEntry(String category, MonthDay dateFrom, MonthDay dateTo, List<TextureEntry> textures) {
        this.category = category;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.textures = textures;
    }

    public boolean isDateActive() {
        if (category.startsWith("_")) {
            return true;
        }

        MonthDay today = MonthDay.now();
        MonthDay effectiveDateTo = dateTo;

        // If range ends on Feb 28, extend to Feb 29 in leap years
        if (dateTo.getMonthValue() == 2 && dateTo.getDayOfMonth() == 28) {
            int currentYear = java.time.Year.now().getValue();
            if (java.time.Year.of(currentYear).isLeap()) {
                effectiveDateTo = MonthDay.of(2, 29);
            }
        }

        if (dateFrom.isAfter(effectiveDateTo)) {
            return !today.isBefore(dateFrom) || !today.isAfter(effectiveDateTo);
        }

        return !today.isBefore(dateFrom) && !today.isAfter(effectiveDateTo);
    }

    public CategoryEntry withTextures(List<TextureEntry> newTextures) {
        return new CategoryEntry(category, dateFrom, dateTo, newTextures);
    }

    public CategoryEntry withDateRange(MonthDay newFrom, MonthDay newTo) {
        return new CategoryEntry(category, newFrom, newTo, textures);
    }

    // Default date range: Jan 1 -> Dec 31
    public static MonthDay defaultDateFrom() {
        return MonthDay.of(1, 1);
    }

    public static MonthDay defaultDateTo() {
        return MonthDay.of(12, 31);
    }
}