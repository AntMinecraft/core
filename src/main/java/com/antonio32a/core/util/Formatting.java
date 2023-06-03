package com.antonio32a.core.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.util.Tuple;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public final class Formatting {
    public static final int GUI_TITLE_PREFIX_LENGTH = 8;
    public static final int GUI_WIDTH = 176;
    private static final Gson gson = new Gson();
    private static final PlainTextComponentSerializer plainTextSerializer = PlainTextComponentSerializer.plainText();
    private static final MiniMessage miniMessage = MiniMessage.builder()
        .strict(true)
        .tags(TagResolver.standard())
        .build();

    private static final HashMap<CharacterInfo, Integer> characterWidthMap = getCharacterWidthMap();

    private Formatting() {}

    /**
     * Parses a string into a component
     *
     * @param text The string to parse
     * @return The parsed component
     */
    @NotNull
    public static Component parse(@NotNull String text) {
        return miniMessage.deserialize(text);
    }

    /**
     * Parses a string into a component and returns it wrapped in an {@link AdventureComponentWrapper}.
     *
     * @param text The string to parse
     * @return The parsed component wrapped in an {@link AdventureComponentWrapper}
     */
    @NotNull
    public static AdventureComponentWrapper parseUI(@NotNull String text) {
        return new AdventureComponentWrapper(parse(text));
    }

    /**
     * Parses a string into a component with resolvers
     *
     * @param text      The string to parse
     * @param resolvers The resolvers to use
     * @return The parsed component
     */
    @NotNull
    public static Component parse(@NotNull String text, @NotNull TagResolver... resolvers) {
        return miniMessage.deserialize(text, resolvers);
    }

    /**
     * Parses a string into a component and returns it wrapped in an {@link AdventureComponentWrapper}.
     *
     * @param text      The string to parse
     * @param resolvers The resolvers to use
     * @return The parsed component wrapped in an {@link AdventureComponentWrapper}
     */
    @NotNull
    public static AdventureComponentWrapper parseUI(@NotNull String text, @NotNull TagResolver... resolvers) {
        return new AdventureComponentWrapper(parse(text, resolvers));
    }

    /**
     * Formats a texture which is used in the UI as the title.
     * By default, the title is moved by 8 pixels to the right, and it's colored gray.
     * This method will automatically add the spacing to the texture and re-color it back to white.
     *
     * @param texture The texture to format.
     * @return The formatted texture.
     */
    @NotNull
    public static Component formatUITexture(String texture) {
        return Component.text(Spacing.calculateSpacing(-GUI_TITLE_PREFIX_LENGTH) + texture)
            .color(NamedTextColor.WHITE);
    }

    /**
     * Splits a component into a list of tuples containing the text and style of each part.
     * The tuple's A value is the text, and the tuple's B value is the style.
     *
     * @param component The component to split.
     * @return The list of tuples.
     */
    @NotNull
    public static List<Tuple<String, Style>> splitComponent(@NotNull Component component) {
        List<Tuple<String, Style>> result = component.children()
            .stream()
            .map(Formatting::splitComponent)
            .flatMap(List::stream)
            .collect(Collectors.toList());

        Component newComponent = component.children(Collections.emptyList());
        result.add(0, new Tuple<>(plainTextSerializer.serialize(newComponent), newComponent.style()));
        return result;
    }

    /**
     * Calculates the actual width of a component which ignores spacing between characters.
     *
     * @param component The component to calculate the width of.
     * @return The actual width of the component.
     */
    public static int calculateActualWidth(@NotNull Component component) {
        Integer charCount = splitComponent(component)
            .stream()
            .map(c -> c.getA().length())
            .reduce(Integer::sum)
            .orElseThrow();

        return calculateWidth(component) - charCount;
    }

    /**
     * Calculates the total width of a component.
     *
     * @param component The component to calculate the width of.
     * @return The width of the component.
     */
    public static int calculateWidth(@NotNull Component component) {
        List<CharacterInfo> characters = splitComponent(component).stream()
            .flatMap(tuple -> {
                StyleInfo style = StyleInfo.fromStyle(tuple.getB());
                return tuple.getA().chars().mapToObj(c ->
                    new CharacterInfo((char) c, style)
                );
            }).toList();

        int width = 0;
        for (CharacterInfo charInfo : characters) {
            @Nullable Integer charWidth = characterWidthMap.get(charInfo);
            if (charWidth == null) {
                throw new IllegalStateException("Failed to get character width for character " + charInfo);
            }
            width += charWidth;
        }
        return width;
    }

    /**
     * Gets the character width map from the character_widths.json file.
     *
     * @return The character width map.
     */
    private static HashMap<CharacterInfo, Integer> getCharacterWidthMap() {
        try (InputStream stream = Formatting.class.getClassLoader().getResourceAsStream("character_widths.json.gz")) {
            if (stream == null) {
                throw new IllegalStateException("Failed to load character widths, stream is null.");
            }

            HashMap<CharacterInfo, Integer> result = new HashMap<>();
            GZIPInputStream gzipStream = new GZIPInputStream(stream);
            String text = IOUtils.toString(gzipStream, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<CharacterWidth>>() {}.getType();
            List<CharacterWidth> characterWidths = gson.fromJson(text, listType);
            for (CharacterWidth characterWidth : characterWidths) {
                result.put(
                    new CharacterInfo(
                        characterWidth.getCharacter(),
                        characterWidth.getStyle()
                    ),
                    characterWidth.getWidth()
                );
            }
            return result;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to load character widths", exception);
        }
    }

    @Data
    private static class CharacterWidth {
        private final char character;
        private final StyleInfo style;
        private final int width;
    }

    @Data
    private static class CharacterInfo {
        private final char character;
        private final StyleInfo style;
    }

    @Data
    private static class StyleInfo {
        private final boolean bold;
        @Nullable private final String font;

        public static StyleInfo fromStyle(Style style) {
            @Nullable Key font = style.font();
            return new StyleInfo(style.hasDecoration(TextDecoration.BOLD), font == null ? null : font.asString());
        }
    }
}
