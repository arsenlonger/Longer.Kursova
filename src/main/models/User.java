package main.models;

public class User {
    private final int id;
    private final String username;
    private int avatarIndex;
    private int level;
    private int experience;
    private int gamesPlayed;
    private int gamesWon;

    public User(int id, String username, int avatarIndex, int level, int experience, int gamesPlayed, int gamesWon) {
        this.id = id;
        this.username = username;
        this.avatarIndex = avatarIndex;
        this.level = level;
        this.experience = experience;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public int getAvatarIndex() { return avatarIndex; }
    public void setAvatarIndex(int avatarIndex) { this.avatarIndex = avatarIndex; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getGamesWon() { return gamesWon; }
    public void setGamesWon(int gamesWon) { this.gamesWon = gamesWon; }
}
