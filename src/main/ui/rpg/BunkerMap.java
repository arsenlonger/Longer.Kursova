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

    public static final Room OXYGEN_ROOM = new Room("💨 Киснева Кімната", new Rectangle(50, 50, 260, 200), new Color(40, 60, 90));
    public static final Room COUNCIL_ROOM = new Room("🗳️ Зал Засідань (Стіл)", new Rectangle(330, 50, 320, 200), new Color(80, 50, 40));
    public static final Room AIRLOCK_ROOM = new Room("🚪 Шлюз Вигнання", new Rectangle(670, 50, 220, 200), new Color(90, 40, 40));

    public static final Room LIBRARY_MED_BAY = new Room("📚 Медпункт & Бібліотека", new Rectangle(50, 270, 260, 200), new Color(40, 80, 60));
    public static final Room HYDROPONICS_ROOM = new Room("🍲 Теплиця (Їжа)", new Rectangle(330, 270, 320, 200), new Color(60, 80, 40));
    public static final Room WATER_STATION = new Room("💧 Водна Станція", new Rectangle(670, 270, 220, 200), new Color(40, 70, 80));

    public static Room[] getAllRooms() {
        return new Room[]{OXYGEN_ROOM, COUNCIL_ROOM, AIRLOCK_ROOM, LIBRARY_MED_BAY, HYDROPONICS_ROOM, WATER_STATION};
    }
}
