package main.ai;

import main.models.CharacterCard;
import main.models.Disaster;
import main.models.Player;
import java.util.*;

public class BotAI {
    public enum Personality {
        LOGICAL,    // Раціональний бот (рахує точну користь)
        AGGRESSIVE, // Агресивний бот (голосує проти тих, хто його підозрює)
        ALTRUIST,   // Альтруїст (береже лікарів та слабких)
        PARANOID    // Параноїк (рандомні підозри)
    }

    private static final Random random = new Random();

    /**
     * Оцінка корисності гравця для розрахунку голосування
     */
    public static double calculateUtilityScore(Player target, Disaster disaster) {
        if (target == null || target.isEliminated()) return -999;

        double score = 50.0;
        CharacterCard card = target.getCard();

        // 1. Оцінка професії за пріоритетами катастрофи
        if (card.isProfessionRevealed()) {
            if (disaster.getPriorityProfessions().contains(card.getProfession())) {
                score += 35.0;
            } else {
                score += 10.0;
            }
            // Досвід роботи
            score += Math.min(card.getExperienceYears() * 1.5, 15.0);
        }

        // 2. Оцінка здоров'я
        if (card.isHealthRevealed()) {
            if (card.getHealthCondition().contains("Абсолютно здоровий")) {
                score += 20.0;
            } else if (card.getHealthCondition().contains("Променева") || card.getHealthCondition().contains("Туберкульоз")) {
                score -= 30.0;
            } else {
                score -= 15.0;
            }
        }

        // 3. Оцінка багажу
        if (card.isBaggageRevealed()) {
            if (disaster.getRequiredItems().contains(card.getBaggage())) {
                score += 25.0;
            } else {
                score += 5.0;
            }
        }

        // 4. Віковий фактор (середній вік від 22 до 45 цінується вище)
        if (card.getAge() >= 22 && card.getAge() <= 45) {
            score += 10.0;
        } else {
            score -= 5.0;
        }

        return score;
    }

    /**
     * Бот вибирає ціль для голосування
     */
    public static Player selectVoteTarget(Player botPlayer, List<Player> activePlayers, Disaster disaster) {
        Player worstTarget = null;
        double lowestScore = Double.MAX_VALUE;

        for (Player target : activePlayers) {
            if (target.isEliminated() || target.getId() == botPlayer.getId() || target.hasImmunity()) {
                continue;
            }

            double score = calculateUtilityScore(target, disaster);
            // Додаємо випадкову варіативність в залежності від особистості
            score += (random.nextDouble() * 10.0 - 5.0);

            if (score < lowestScore) {
                lowestScore = score;
                worstTarget = target;
            }
        }

        return worstTarget;
    }

    /**
     * Генерація аргументованого коментаря бота в чат українською мовою
     */
    public static String generateChatComment(Player botPlayer, Player target, Disaster disaster) {
        if (target == null) return "🤖 " + botPlayer.getName() + ": Усі здаються корисними, складно вибрати...";

        CharacterCard targetCard = target.getCard();
        String targetName = target.getName();

        List<String> templates = new ArrayList<>();

        if (targetCard.isHealthRevealed() && !targetCard.getHealthCondition().contains("здоровий")) {
            templates.add(String.format("🤖 %s: Я подивився на %s. Зі станом здоров'я (%s) довго в бункері не виживеш...",
                    botPlayer.getName(), targetName, targetCard.getHealthCondition()));
        }

        if (targetCard.isProfessionRevealed()) {
            if (!disaster.getPriorityProfessions().contains(targetCard.getProfession())) {
                templates.add(String.format("🤖 %s: %s має професію '%s'. Для катастрофи '%s' це майже марно!",
                        botPlayer.getName(), targetName, targetCard.getProfession(), disaster.getName()));
            } else {
                templates.add(String.format("🤖 %s: %s корисний як %s, але наразі нам потрібно економити ресурси.",
                        botPlayer.getName(), targetName, targetCard.getProfession()));
            }
        }

        templates.add(String.format("🤖 %s: Моя аналітика показує, що проти %s найменше аргументів на користь залишення.",
                botPlayer.getName(), targetName));

        return templates.get(random.nextInt(templates.size()));
    }
}
