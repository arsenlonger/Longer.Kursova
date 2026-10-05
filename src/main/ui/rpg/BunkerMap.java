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

    // БІЛЬШІ КІМНАТИ БУНКЕРА (Розмір поля 1040 x 520)
    public static final Room OXYGEN_ROOM = new Room("💨 Кисневий Блок", new Rectangle(30, 30, 310, 220), new Color(30, 50, 80));
    public static final Room COUNCIL_ROOM = new Room("🗳️ Зал Засідань (Стіл)", new Rectangle(360, 30, 330, 220), new Color(70, 45, 35));
    public static final Room SLEEPING_QUARTERS = new Room("🛏️ Спальня (Кімната Відпочинку)", new Rectangle(710, 30, 270, 220), new Color(50, 40, 70));

    public static final Room LIBRARY_MED_BAY = new Room("📚 Медпункт & Бібліотека", new Rectangle(30, 270, 310, 220), new Color(30, 70, 50));
    public static final Room HYDROPONICS_ROOM = new Room("🍲 Теплиця (Їжа)", new Rectangle(360, 270, 330, 220), new Color(50, 70, 35));
    public static final Room WATER_STATION = new Room("💧 Водна Станція", new Rectangle(710, 270, 270, 220), new Color(30, 60, 70));

    // Дверні проходи (Doorways)
    public static final Rectangle DOOR_OXYGEN_COUNCIL = new Rectangle(340, 110, 25, 55);
    public static final Rectangle DOOR_COUNCIL_SLEEP = new Rectangle(690, 110, 25, 55);
    public static final Rectangle DOOR_COUNCIL_HYDRO = new Rectangle(500, 250, 55, 25);
    public static final Rectangle DOOR_MED_HYDRO = new Rectangle(340, 350, 25, 55);
    public static final Rectangle DOOR_HYDRO_WATER = new Rectangle(690, 350, 25, 55);

    public static Room[] getAllRooms() {
        return new Room[]{OXYGEN_ROOM, COUNCIL_ROOM, SLEEPING_QUARTERS, LIBRARY_MED_BAY, HYDROPONICS_ROOM, WATER_STATION};
    }

    public static Rectangle[] getAllDoors() {
        return new Rectangle[]{DOOR_OXYGEN_COUNCIL, DOOR_COUNCIL_SLEEP, DOOR_COUNCIL_HYDRO, DOOR_MED_HYDRO, DOOR_HYDRO_WATER};
    }

    public static boolean isWalkablePosition(int x, int y) {
        Point p = new Point(x, y);

        for (Rectangle door : getAllDoors()) {
            if (door.contains(p)) return true;
        }

        for (Room room : getAllRooms()) {
            if (room.bounds.contains(p)) return true;
        }

        return false;
    }
}
