package com.antonio32a.core.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.util.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FormattingTests {
    @Test
    void testSplitComponentBasic() {
        Component component = Formatting.parse("unformatted text");
        List<Tuple<String, Style>> result = Formatting.splitComponent(component);

        assertEquals("unformatted text", result.get(0).getA());
        assertEquals(Style.empty(), result.get(0).getB());
    }

    @Test
    void testSplitComponentStyle() {
        Component component = Formatting.parse("unformatted<red>red text</red>unformatted again");
        List<Tuple<String, Style>> result = Formatting.splitComponent(component);

        assertEquals("unformatted", result.get(0).getA());
        assertEquals(Style.empty(), result.get(0).getB());

        assertEquals("red text", result.get(1).getA());
        assertEquals(Style.empty().color(NamedTextColor.RED), result.get(1).getB());

        assertEquals("unformatted again", result.get(2).getA());
        assertEquals(Style.empty(), result.get(2).getB());
    }

    @Test
    void testSplitComponentMultipleStyles() {
        Component component = Formatting.parse("unformatted<red><bold>bold red</bold></red>unformatted again");
        List<Tuple<String, Style>> result = Formatting.splitComponent(component);

        assertEquals("unformatted", result.get(0).getA());
        assertEquals(Style.empty(), result.get(0).getB());

        assertEquals("bold red", result.get(1).getA());
        assertEquals(Style.empty().color(NamedTextColor.RED).decorate(TextDecoration.BOLD), result.get(1).getB());

        assertEquals("unformatted again", result.get(2).getA());
        assertEquals(Style.empty(), result.get(2).getB());
    }

    @Test
    void testSplitComponentHoverStyle() {
        Component component = Formatting.parse("unformatted<hover:show_text:'<red>test</red>'>hi</hover>unformatted again");
        List<Tuple<String, Style>> result = Formatting.splitComponent(component);

        assertEquals("unformatted", result.get(0).getA());
        assertEquals(Style.empty(), result.get(0).getB());

        assertEquals("hi", result.get(1).getA());
        assertEquals(
            Style.style()
                .hoverEvent(HoverEvent.showText(Formatting.parse("<red>test</red>")))
                .build(),
            result.get(1).getB()
        );

        assertEquals("unformatted again", result.get(2).getA());
        assertEquals(Style.empty(), result.get(2).getB());
    }

    @Test
    void testSplitComponentInnerFont() {
        Component component = Formatting.parse("<font:a>test<red>colored test</red></font>");
        List<Tuple<String, Style>> result = Formatting.splitComponent(component);

        assertEquals("test", result.get(0).getA());
        assertEquals(Style.style().font(Key.key("a")).build(), result.get(0).getB());

        assertEquals("colored test", result.get(1).getA());
        assertEquals(Style.style().font(Key.key("a")).color(NamedTextColor.RED).build(), result.get(1).getB());
    }

    @Test
    void testSplitComponentInnerStyles() {
        Component component = Formatting.parse("unformatted<red>red <bold>bold red</bold> red again</red>unformatted again");
        List<Tuple<String, Style>> result = Formatting.splitComponent(component);

        assertEquals("unformatted", result.get(0).getA());
        assertEquals(Style.empty(), result.get(0).getB());

        assertEquals("red ", result.get(1).getA());
        assertEquals(Style.empty().color(NamedTextColor.RED), result.get(1).getB());

        assertEquals("bold red", result.get(2).getA());
        assertEquals(Style.empty().color(NamedTextColor.RED).decorate(TextDecoration.BOLD), result.get(2).getB());

        assertEquals(" red again", result.get(3).getA());
        assertEquals(Style.empty().color(NamedTextColor.RED), result.get(3).getB());

        assertEquals("unformatted again", result.get(4).getA());
        assertEquals(Style.empty(), result.get(4).getB());
    }

    @Test
    void testCalculateWidthBasic() {
        assertEquals(8, Formatting.calculateWidth(Component.text("hi")));
    }

    @Test
    void testCalculateWidthStyle() {
        assertEquals(8, Formatting.calculateWidth(Component.text("hi").style(Style.style().color(NamedTextColor.RED).build())));
        assertEquals(10, Formatting.calculateWidth(Formatting.parse("<b>hi</b>")));
    }

    @Test
    void testCalculateWidthCustomUnicode() {
        assertEquals(180, Formatting.calculateWidth(Component.text("\uFB00")));
    }

    @Test
    void testCalculateActualWidth() {
        assertEquals(6, Formatting.calculateActualWidth(Component.text("hi")));
        assertEquals(8, Formatting.calculateActualWidth(Formatting.parse("<b>hi</b>")));
        assertEquals(179, Formatting.calculateActualWidth(Component.text("\uFB00")));
    }
}
