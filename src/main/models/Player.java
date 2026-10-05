package main.models;

public class Player {
    private final int id;
    private final String name;
    private final boolean isBot;
    private final int avatarIndex;
    private CharacterCard card;
    private boolean isEliminated;
    private boolean hasImmunity;

    public Player(int id, String name, boolean isBot, int avatarIndex, CharacterCard card) {
        this.id = id;
        this.name = name;
        this.isBot = isBot;
        this.avatarIndex = avatarIndex;
        this.card = card;
        this.isEliminated = false;
        this.hasImmunity = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isBot() { return isBot; }
    public int getAvatarIndex() { return avatarIndex; }
    public CharacterCard getCard() { return card; }
    public void setCard(CharacterCard card) { this.card = card; }

    public boolean isEliminated() { return isEliminated; }
    public void setEliminated(boolean eliminated) { isEliminated = eliminated; }

    public boolean hasImmunity() { return hasImmunity; }
    public void setHasImmunity(boolean hasImmunity) { this.hasImmunity = hasImmunity; }
}
