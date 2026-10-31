package de.lmcstudio.keyall.key;

public record KeyEntry(
        String id,
        String display,
        String color,
        double chance,
        boolean giveNothing
) {}