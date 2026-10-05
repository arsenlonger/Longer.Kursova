package main.models;

public class CharacterCard {
    private final String name;
    private final int age;
    private final String gender;
    private final String profession;
    private final int experienceYears;
    private final String healthCondition;
    private final String hobby;
    private final String phobia;
    private String baggage;
    private final SpecialCard specialCard;

    // Прапори відкритих рис для інших гравців
    private boolean professionRevealed;
    private boolean healthRevealed;
    private boolean hobbyRevealed;
    private boolean phobiaRevealed;
    private boolean baggageRevealed;
    private boolean specialCardRevealed;

    public CharacterCard(String name, int age, String gender, String profession, int experienceYears,
                         String healthCondition, String hobby, String phobia, String baggage, SpecialCard specialCard) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.profession = profession;
        this.experienceYears = experienceYears;
        this.healthCondition = healthCondition;
        this.hobby = hobby;
        this.phobia = phobia;
        this.baggage = baggage;
        this.specialCard = specialCard;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getProfession() { return profession; }
    public int getExperienceYears() { return experienceYears; }
    public String getHealthCondition() { return healthCondition; }
    public String getHobby() { return hobby; }
    public String getPhobia() { return phobia; }
    public String getBaggage() { return baggage; }
    public void setBaggage(String baggage) { this.baggage = baggage; }
    public SpecialCard getSpecialCard() { return specialCard; }

    public boolean isProfessionRevealed() { return professionRevealed; }
    public void setProfessionRevealed(boolean revealed) { this.professionRevealed = revealed; }

    public boolean isHealthRevealed() { return healthRevealed; }
    public void setHealthRevealed(boolean revealed) { this.healthRevealed = revealed; }

    public boolean isHobbyRevealed() { return hobbyRevealed; }
    public void setHobbyRevealed(boolean revealed) { this.hobbyRevealed = revealed; }

    public boolean isPhobiaRevealed() { return phobiaRevealed; }
    public void setPhobiaRevealed(boolean revealed) { this.phobiaRevealed = revealed; }

    public boolean isBaggageRevealed() { return baggageRevealed; }
    public void setBaggageRevealed(boolean revealed) { this.baggageRevealed = revealed; }

    public boolean isSpecialCardRevealed() { return specialCardRevealed; }
    public void setSpecialCardRevealed(boolean revealed) { this.specialCardRevealed = revealed; }
}
