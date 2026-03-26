package io.github.maplemods.sapling.data;

import java.time.MonthDay;
import java.util.HashMap;
import java.util.Map;

public class CategoryDefaults {
    private static final Map<String, MonthDay[]> DATE_RANGES = new HashMap<>();

    static {
        DATE_RANGES.put("spring",
                new MonthDay[]{
                        MonthDay.of(3, 1), MonthDay.of(5, 31)
        });
        DATE_RANGES.put("summer",
                new MonthDay[]{
                        MonthDay.of(6, 1), MonthDay.of(8, 31)
        });
        DATE_RANGES.put("autumn",
                new MonthDay[]{
                        MonthDay.of(9, 1), MonthDay.of(11, 30)
        });
        DATE_RANGES.put("winter",
                new MonthDay[]{
                        MonthDay.of(12, 1),MonthDay.of(2, 28)
        });
        DATE_RANGES.put("easter",
                new MonthDay[]{
                        MonthDay.of(3, 25), MonthDay.of(4, 25)
        });
        DATE_RANGES.put("halloween",
                new MonthDay[]{
                        MonthDay.of(10, 24), MonthDay.of(10, 31)
        });
        DATE_RANGES.put("christmas",
                new MonthDay[]{
                        MonthDay.of(12, 1), MonthDay.of(12, 26)
        });
    }

    public static MonthDay getDateFrom(String category) {
        MonthDay[] range = DATE_RANGES.get(category.toLowerCase());
        return range != null ? range[0] : CategoryEntry.defaultDateFrom();
    }

    public static MonthDay getDateTo(String category) {
        MonthDay[] range = DATE_RANGES.get(category.toLowerCase());
        return range != null ? range[1] : CategoryEntry.defaultDateTo();
    }
}