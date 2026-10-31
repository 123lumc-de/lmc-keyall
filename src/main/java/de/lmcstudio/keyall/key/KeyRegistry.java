package de.lmcstudio.keyall.key;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KeyRegistry {

    private final List<KeyEntry> entries;

    public KeyRegistry(List<KeyEntry> entries) {
        this.entries = List.copyOf(entries);
    }

    public List<KeyEntry> entries() {
        return entries;
    }

    /**
     * Gewichtete Zufallsauswahl. chance-Werte werden als Gewichte interpretiert.
     * Es wird immer ein Eintrag zurückgegeben (Fallback = letzter Eintrag).
     */
    public KeyEntry roll() {
        if (entries.isEmpty()) {
            return new KeyEntry("none", "&7Nichts", "GRAY", 0, true);
        }
        double total = entries.stream().mapToDouble(KeyEntry::chance).sum();
        if (total <= 0) return entries.getLast();

        double roll = ThreadLocalRandom.current().nextDouble() * total;
        double current = 0;
        for (KeyEntry e : entries) {
            current += e.chance();
            if (roll <= current) return e;
        }
        return entries.getLast();
    }

    /**
     * Nur die "echten" Keys (ohne give-nothing Einträge) – für die Roulette-Animation.
     */
    public List<KeyEntry> realEntries() {
        return entries.stream().filter(e -> !e.giveNothing()).toList();
    }
}