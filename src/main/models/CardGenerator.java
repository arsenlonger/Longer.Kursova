package main.models;

import java.util.*;

public class CardGenerator {
    private static final Random random = new Random();

    private static final String[] MALE_NAMES = {"Олександр", "Максим", "Дмитро", "Артем", "Богдан", "Владислав", "Тарас", "Ігор", "Віктор", "Сергій"};
    private static final String[] FEMALE_NAMES = {"Олена", "Анна", "Марія", "Ольга", "Катерина", "Наталія", "Ірина", "Вікторія", "Софія", "Юлія"};

    private static final String[] PROFESSIONS = {
            "Хірург", "Вірусолог", "Терапевт", "Інженер-електрик", "Механік",
            "Фізик-ядерник", "Будівельник", "Військовий", "Поліцейський", "Пожежник",
            "Агроном", "Повар", "Швачка", "Водій вантажівки", "Програміст",
            "Вчитель хімії", "Психолог", "Мисливець", "Блогер", "Юрист"
    };

    private static final String[] HEALTH_CONDITIONS = {
            "Абсолютно здоровий", "Астма", "Цукровий діабет I типу", "Променева хвороба",
            "Сліпота на одне око", "Глухота", "Безпліддя", "Хронічна алергія",
            "Перелом ноги (на киликах)", "Туберкульоз", "Артрит", "Дальтонізм",
            "Залежність від алкоголю", "Серцева недостатність", "Хронічне безсоння"
    };

    private static final String[] HOBBIES = {
            "Стрільба з лука", "Перша домедична допомога", "Гітара", "Кулінарія",
            "Радіоаматорство", "Резьблення по дереву", "Пчелярство", "Бокс",
            "В'язання", "Орієнтування на місцевості", "Шахи", "Плавання"
    };

    private static final String[] PHOBIAS = {
            "Клаустрофобія (страх замкненого простору)", "Паранойя", "Піроманія (потяг до вогню)",
            "Мизофобія (страх бруду)", "Арахнофобія", "Пацифізм (категорична відмова від зброї)",
            "Ніктофобія (страх темряви)", "Клептоманія"
    };

    private static final String[] BAGGAGE_ITEMS = {
            "Аптечка першої допомоги", "Дробовик з 5 набоями", "Набір інструментів",
            "Сухпайок на 7 днів", "Портативна рація", "Каністра бензину (10л)",
            "Фільтр для очищення води", "Комплект насіння овочів", "Портативний генератор",
            "Ніж мисливський", "Намет 4-місний", "Ліхтарик на сонячних батареях",
            "Книга з виживання", "Настільна гра", "Мультивітаміни (100 табл)"
    };

    public static Disaster generateRandomDisaster() {
        List<Disaster> disasters = Arrays.asList(
                new Disaster("Ядерна зима", "Температура на поверхні впала до -40°C. Радіаційне зараження території.",
                        Arrays.asList("Інженер-електрик", "Механік", "Фізик-ядерник", "Будівельник"),
                        Arrays.asList("Портативний генератор", "Каністра бензину (10л)", "Аптечка першої допомоги")),

                new Disaster("Мутагенний Вірус", "Незвіданий біологічний вірус вразив 90% населення Землі.",
                        Arrays.asList("Вірусолог", "Хірург", "Терапевт", "Вчитель хімії"),
                        Arrays.asList("Аптечка першої допомоги", "Мультивітаміни (100 табл)", "Фільтр для очищення води")),

                new Disaster("Зомбі-Апокаліпсис", "Орда інфікованих агресивних мутантів захопила міста.",
                        Arrays.asList("Військовий", "Поліцейський", "Пожежник", "Мисливець"),
                        Arrays.asList("Дробовик з 5 набоями", "Ніж мисливський", "Сухпайок на 7 днів")),

                new Disaster("Сонячний Спалах", "Потужна сонячна буря знищила всі електромережі планети.",
                        Arrays.asList("Інженер-електрик", "Програміст", "Агроном", "Повар"),
                        Arrays.asList("Комплект насіння овочів", "Портативна рація", "Набір інструментів"))
        );

        return disasters.get(random.nextInt(disasters.size()));
    }

    public static CharacterCard generateRandomCard() {
        boolean isMale = random.nextBoolean();
        String name = isMale ? MALE_NAMES[random.nextInt(MALE_NAMES.length)] : FEMALE_NAMES[random.nextInt(FEMALE_NAMES.length)];
        String gender = isMale ? "Чоловік" : "Жінка";
        int age = 18 + random.nextInt(45);
        String profession = PROFESSIONS[random.nextInt(PROFESSIONS.length)];
        int expYears = Math.min(age - 18, 1 + random.nextInt(25));
        String health = HEALTH_CONDITIONS[random.nextInt(HEALTH_CONDITIONS.length)];
        String hobby = HOBBIES[random.nextInt(HOBBIES.length)];
        String phobia = PHOBIAS[random.nextInt(PHOBIAS.length)];
        String baggage = BAGGAGE_ITEMS[random.nextInt(BAGGAGE_ITEMS.length)];

        SpecialCard specialCard = generateRandomSpecialCard();

        return new CharacterCard(name, age, gender, profession, expYears, health, hobby, phobia, baggage, specialCard);
    }

    private static SpecialCard generateRandomSpecialCard() {
        SpecialCard.CardType[] types = SpecialCard.CardType.values();
        SpecialCard.CardType selectedType = types[random.nextInt(types.length)];

        switch (selectedType) {
            case IMMUNITY:
                return new SpecialCard(selectedType, "🛡️ Імунітет", "Дає вам повний захист від вигнання з бункера у поточному раунді.");
            case SWAP_BAGGAGE:
                return new SpecialCard(selectedType, "🔄 Обмін багажем", "Дозволяє помінятися багажем із будь-яким іншим гравцем.");
            case XRAY_INSPECT:
                return new SpecialCard(selectedType, "🕵️ Рентген", "Дозволяє дізнатися таємну хворобу або фобію обраного гравця.");
            case DOUBLE_VOTE:
                return new SpecialCard(selectedType, "🗳️ Подвійний голос", "Ваш голос у поточному раунді підрахується як 2 голоси.");
            case CANCEL_VOTE:
                return new SpecialCard(selectedType, "⚖️ Переголосування", "Скасовує результати голосування та проводить повторний вибір.");
            case DOCTOR_CURE:
                return new SpecialCard(selectedType, "💊 Зцілення", "Повністю позбавляє вас або вашого союзника від хвороби.");
            case BLOCK_PLAYER:
                return new SpecialCard(selectedType, "🚫 Блокування", "Забороняє вибраному гравцю голосувати у цьому раунді.");
            case FORCE_REVEAL:
                return new SpecialCard(selectedType, "📢 Примус", "Змушує обраного гравця відкрити свою спец-карту.");
            default:
                return new SpecialCard(SpecialCard.CardType.IMMUNITY, "🛡️ Імунітет", "Захист від вигнання.");
        }
    }
}
