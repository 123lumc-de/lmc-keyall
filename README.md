# LMC Keyall

Keyall-Plugin für Minecraft Paper 26.2 – von **LMC Studio**.

## Features

- Keyall für **alle** Online-Spieler – jeder würfelt individuell
- Gewichtete Chancen pro Key (`chance` in der Config)
- Console-Command pro Gewinner, konfigurierbar (`%player%`, `%key%`)
- **Roulette-Title** in der Bildschirmmitte pro Spieler
- **H2** oder **MySQL** Support (HikariCP)
- **MiniMessage** primär, **Legacy (`&`)** als Fallback
- PlaceholderAPI: `%lmckeyall_next%` (z.B. `1H, 22Min, 33Sek`), `%lmckeyall_next_seconds%`

## Voraussetzungen

- **Paper 26.2** (Java 25)
- Optional: PlaceholderAPI 2.12.3+

## Build

```bash
mvn clean package