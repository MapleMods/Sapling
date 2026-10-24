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

    private static final Map<String, MonthDay[]> REGISTERED_RANGES = new HashMap<>();
    private static final Map<String, String> REGISTERED_KINDS = new HashMap<>();
    private static final Map<String, String> REGISTERED_BY = new HashMap<>();

    public static boolean register(String modId, String category, String kind, MonthDay from, MonthDay to) {
        String key = category.toLowerCase();
        if (REGISTERED_KINDS.containsKey(key)) {
            MonthDay[] existing = REGISTERED_RANGES.get(key);
            if (!REGISTERED_KINDS.get(key).equals(kind) || !existing[0].equals(from) || !existing[1].equals(to)) {
                Constants.LOG.warn("[" + Constants.MOD_NAME + "] {} registers the category '{}' differently than {}, keeping the first.",
                        modId, category, REGISTERED_BY.get(key));
            }
            return false;
        }
        REGISTERED_RANGES.put(key, new MonthDay[]{from, to});
        REGISTERED_KINDS.put(key, kind);
        REGISTERED_BY.put(key, modId);
        return true;
    }

    public static MonthDay getDateFrom(String category) {
        MonthDay[] range = REGISTERED_RANGES.getOrDefault(category.toLowerCase(), DATE_RANGES.get(category.toLowerCase()));
        return range != null ? range[0] : CategoryEntry.defaultDateFrom();
    }

    public static MonthDay getDateTo(String category) {
        MonthDay[] range = REGISTERED_RANGES.getOrDefault(category.toLowerCase(), DATE_RANGES.get(category.toLowerCase()));
        return range != null ? range[1] : CategoryEntry.defaultDateTo();
    }

    public static boolean isSeason(String category) {
        String kind = REGISTERED_KINDS.get(category.toLowerCase());
        return kind != null ? kind.equals("season") : Constants.SEASONAL_CATEGORIES.contains(category);
    }

    public static boolean isHoliday(String category) {
        return !category.startsWith("_") && !isSeason(category) && !"theme".equals(REGISTERED_KINDS.get(category.toLowerCase()));
    }
}