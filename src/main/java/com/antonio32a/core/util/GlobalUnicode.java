package com.antonio32a.core.util;

import lombok.Getter;
import net.kyori.adventure.text.Component;

// Unicodes in this case are better as \uAAAAAA because that's how they're represented in the pack
@SuppressWarnings("UnnecessaryUnicodeEscape")
@Getter
public enum GlobalUnicode {
    ACTIONBAR_SETTINGS('\uFA00');

    private final char character;
    private final int width;
    private final int border;

    GlobalUnicode(char character, int border) {
        this.character = character;
        this.width = Formatting.calculateWidth(Component.text(String.valueOf(character)));
        this.border = border;
    }

    GlobalUnicode(char character) {
        this(character, 0);
    }

    /**
     * Gets the actual width of the character which excludes the space.
     *
     * @return The actual width of the character.
     */
    public int getActualWidth() {
        return width - 1;
    }

    @Override
    public String toString() {
        return String.valueOf(character);
    }
}
