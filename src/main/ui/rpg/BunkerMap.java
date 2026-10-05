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

    // Дверні проходи (Doorways)
    public static final Rectangle DOOR_OXYGEN_COUNCIL = new Rectangle(305, 110, 35, 50);
    public static final Rectangle DOOR_COUNCIL_SLEEP = new Rectangle(635, 110, 35, 50);
    public static final Rectangle DOOR_COUNCIL_HYDRO = new Rectangle(460, 225, 50, 35);
    public static final Rectangle DOOR_MED_HYDRO = new Rectangle(305, 320, 35, 50);
    public static final Rectangle DOOR_HYDRO_WATER = new Rectangle(635, 320, 35, 50);

    public static Room[] getAllRooms() {
        return new Room[]{OXYGEN_ROOM, COUNCIL_ROOM, SLEEPING_QUARTERS, LIBRARY_MED_BAY, HYDROPONICS_ROOM, WATER_STATION};
    }

    public static Rectangle[] getAllDoors() {
        return new Rectangle[]{DOOR_OXYGEN_COUNCIL, DOOR_COUNCIL_SLEEP, DOOR_COUNCIL_HYDRO, DOOR_MED_HYDRO, DOOR_HYDRO_WATER};
    }

    /**
     * Перевірка: позиція (x, y) припустима лише якщо вона знаходиться ВСЕРЕДИНІ однієї з кімнат АБО всередині дверного проходу!
     */
    public static boolean isWalkablePosition(int x, int y) {
        Point p = new Point(x, y);

        // 1. Перевірка дверних проходів
        for (Rectangle door : getAllDoors()) {
            if (door.contains(p)) return true;
        }

        // 2. Перевірка кімнат
        for (Room room : getAllRooms()) {
            if (room.bounds.contains(p)) return true;
        }

        return false;
    }
}
