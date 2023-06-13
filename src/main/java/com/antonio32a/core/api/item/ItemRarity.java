package com.antonio32a.core.api.item;

import com.antonio32a.core.util.Formatting;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum ItemRarity {
    COMMON(Formatting.parse("<gray>Common</gray>")),
    RARE(Formatting.parse("<blue>Rare</blue>")),
    LEGENDARY(Formatting.parse("<gold>Legendary</gold>")),
    SPECIAL(Formatting.parse("<light_purple>Special</light_purple>"));

    private final Component name;

    ItemRarity(Component name) {
        this.name = name;
    }
}
