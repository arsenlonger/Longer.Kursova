package main.ui.rpg;

import java.awt.*;

public class BunkerMap {
    public static class Room {
        public String name;
        public Rectangle bounds;
        public Color color;

        public Room(String name, Rectangle bounds, Color color) {
            this.name = name;
            this.bounds = bounds;
            this.color = color;
        }
    }

    public static final Room OXYGEN_ROOM = new Room("💨 Кисневий Блок", new Rectangle(40, 40, 270, 190), new Color(30, 50, 80));
    public static final Room COUNCIL_ROOM = new Room("🗳️ Зал Засідань (Стіл)", new Rectangle(330, 40, 310, 190), new Color(70, 45, 35));
    public static final Room SLEEPING_QUARTERS = new Room("🛏️ Спальня (Кімната Відпочинку)", new Rectangle(660, 40, 240, 190), new Color(50, 40, 70));

    public static final Room LIBRARY_MED_BAY = new Room("📚 Медпункт & Бібліотека", new Rectangle(40, 250, 270, 190), new Color(30, 70, 50));
    public static final Room HYDROPONICS_ROOM = new Room("🍲 Теплиця (Їжа)", new Rectangle(330, 250, 310, 190), new Color(50, 70, 35));
    public static final Room WATER_STATION = new Room("💧 Водна Станція", new Rectangle(660, 250, 240, 190), new Color(30, 60, 70));

    public static Room[] getAllRooms() {
        return new Room[]{OXYGEN_ROOM, COUNCIL_ROOM, SLEEPING_QUARTERS, LIBRARY_MED_BAY, HYDROPONICS_ROOM, WATER_STATION};
    }
}
