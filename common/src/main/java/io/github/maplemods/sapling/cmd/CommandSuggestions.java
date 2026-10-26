package io.github.maplemods.sapling.cmd;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.maplemods.sapling.data.CategoryEntry;
import io.github.maplemods.sapling.data.Textures;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CommandSuggestions {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM");

    public static final SuggestionProvider<CommandSourceStack> ENTITY_TYPE_SUGGESTIONS = (context, builder) -> {
        Textures.texturePairs.keySet().forEach(entityType ->
                builder.suggest(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath()));
        return builder.buildFuture();
    };

    public static final SuggestionProvider<CommandSourceStack> TEXTURE_SUGGESTIONS = (context, builder) -> {
        EntityType<?> entityType = MapleModsCommand.resolveEntityType(StringArgumentType.getString(context, "entityType"));
        if (entityType != null) {
            List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
            if (categories != null) {
                categories.stream()
                        .flatMap(c -> c.textures.stream())
                        .forEach(entry -> builder.suggest(entry.textureName));
            }
        }
        return builder.buildFuture();
    };

    public static final SuggestionProvider<CommandSourceStack> CATEGORY_SUGGESTIONS = (context, builder) -> {
        EntityType<?> entityType = MapleModsCommand.resolveEntityType(StringArgumentType.getString(context, "entityType"));
        if (entityType != null) {
            List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
            if (categories != null) {
                categories.stream()
                        .map(c -> c.category)
                        .distinct()
                        .forEach(builder::suggest);
            }
        }
        return builder.buildFuture();
    };

    public static final SuggestionProvider<CommandSourceStack> DATE_FROM_SUGGESTIONS = (context, builder) -> {
        EntityType<?> entityType = MapleModsCommand.resolveEntityType(StringArgumentType.getString(context, "entityType"));
        String categoryName = StringArgumentType.getString(context, "category");
        if (entityType != null) {
            List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
            if (categories != null) {
                categories.stream()
                        .filter(c -> c.category.equals(categoryName))
                        .findFirst()
                        .ifPresent(c -> builder.suggest(c.dateFrom.format(DATE_FORMATTER)));
            }
        }
        return builder.buildFuture();
    };

    public static final SuggestionProvider<CommandSourceStack> DATE_TO_SUGGESTIONS = (context, builder) -> {
        EntityType<?> entityType = MapleModsCommand.resolveEntityType(StringArgumentType.getString(context, "entityType"));
        String categoryName = StringArgumentType.getString(context, "category");
        if (entityType != null) {
            List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
            if (categories != null) {
                categories.stream()
                        .filter(c -> c.category.equals(categoryName))
                        .findFirst()
                        .ifPresent(c -> builder.suggest(c.dateTo.format(DATE_FORMATTER)));
            }
        }
        return builder.buildFuture();
    };

    public static final SuggestionProvider<CommandSourceStack> ALL_CATEGORY_SUGGESTIONS = (context, builder) -> {
        Textures.texturePairs.values().stream()
                .flatMap(List::stream)
                .map(c -> c.category)
                .distinct()
                .sorted()
                .forEach(builder::suggest);
        return builder.buildFuture();
    };

    public static final SuggestionProvider<CommandSourceStack> COLLECTION_SUGGESTIONS = (context, builder) -> {
        Textures.texturePairs.values().stream()
                .flatMap(List::stream)
                .flatMap(c -> c.textures.stream())
                .map(e -> e.modId)
                .distinct()
                .sorted()
                .forEach(builder::suggest);
        return builder.buildFuture();
    };

    public static final SuggestionProvider<CommandSourceStack> COLLECTION_CATEGORY_SUGGESTIONS = (context, builder) -> {
        String modId = StringArgumentType.getString(context, "collection");
        Textures.texturePairs.values().stream()
                .flatMap(List::stream)
                .filter(c -> c.textures.stream().anyMatch(e -> e.modId.equals(modId)))
                .map(c -> c.category)
                .distinct()
                .sorted()
                .forEach(builder::suggest);
        return builder.buildFuture();
    };

    public static final SuggestionProvider<CommandSourceStack> GROUP_DATE_FROM_SUGGESTIONS = (context, builder) -> {
        firstCategoryNamed(context).ifPresent(c -> builder.suggest(c.dateFrom.format(DATE_FORMATTER)));
        return builder.buildFuture();
    };

    public static final SuggestionProvider<CommandSourceStack> GROUP_DATE_TO_SUGGESTIONS = (context, builder) -> {
        firstCategoryNamed(context).ifPresent(c -> builder.suggest(c.dateTo.format(DATE_FORMATTER)));
        return builder.buildFuture();
    };

    private static Optional<CategoryEntry> firstCategoryNamed(CommandContext<CommandSourceStack> context) {
        String categoryName = StringArgumentType.getString(context, "category");
        return Textures.texturePairs.values().stream()
                .flatMap(List::stream)
                .filter(c -> c.category.equals(categoryName))
                .findFirst();
    }
}
