package main.models;

import java.util.List;

public class Disaster {
    private final String name;
    private final String description;
    private final List<String> priorityProfessions;
    private final List<String> requiredItems;

    public Disaster(String name, String description, List<String> priorityProfessions, List<String> requiredItems) {
        this.name = name;
        this.description = description;
        this.priorityProfessions = priorityProfessions;
        this.requiredItems = requiredItems;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPriorityProfessions() {
        return priorityProfessions;
    }

    public List<String> getRequiredItems() {
        return requiredItems;
    }

    @Override
    public String toString() {
        return "Disaster{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
