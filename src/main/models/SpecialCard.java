package main.models;

public class SpecialCard {
    public enum CardType {
        IMMUNITY,          // Імунітет від голосування
        SWAP_BAGGAGE,      // Обмін багажем
        XRAY_INSPECT,      // Перегляд прихованих вад
        DOUBLE_VOTE,       // Подвійний голос
        CANCEL_VOTE,       // Скасування голосування
        DOCTOR_CURE,       // Вилікувати хворобу
        BLOCK_PLAYER,      // Блокування голосу іншого гравця
        FORCE_REVEAL       // Примусове відкриття риси
    }

    private final CardType type;
    private final String title;
    private final String description;
    private boolean isUsed;

    public SpecialCard(CardType type, String title, String description) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.isUsed = false;
    }

    public CardType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
