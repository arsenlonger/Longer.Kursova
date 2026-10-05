package main.logic;

import main.models.CharacterCard;
import main.models.Disaster;
import main.models.Player;
import java.util.List;

public class SurvivalCalculator {

    public static class SurvivalResult {
        private final int scorePercent;
        private final String epilogue;
        private final boolean isVictory;

        public SurvivalResult(int scorePercent, String epilogue, boolean isVictory) {
            this.scorePercent = scorePercent;
            this.epilogue = epilogue;
            this.isVictory = isVictory;
        }

        public int getScorePercent() { return scorePercent; }
        public String getEpilogue() { return epilogue; }
        public boolean isVictory() { return isVictory; }
    }

    public static SurvivalResult calculateBunkerSurvival(List<Player> survivors, Disaster disaster) {
        if (survivors.isEmpty()) {
            return new SurvivalResult(0, "💀 Бункер опустів. Жоден гравець не вижив у пустелі.", false);
        }

        int score = 40; // Базовий шанс виживання
        boolean hasDoctor = false;
        boolean hasEngineer = false;
        boolean hasFoodOrSeeds = false;
        boolean hasWeapon = false;

        for (Player p : survivors) {
            CharacterCard card = p.getCard();
            String prof = card.getProfession();
            String bag = card.getBaggage();

            if (prof.contains("Хірург") || prof.contains("Терапевт") || prof.contains("Вірусолог")) hasDoctor = true;
            if (prof.contains("Інженер") || prof.contains("Механік") || prof.contains("Будівельник")) hasEngineer = true;

            if (bag.contains("Сухпайок") || bag.contains("насіння")) hasFoodOrSeeds = true;
            if (bag.contains("Дробовик") || bag.contains("Ніж")) hasWeapon = true;

            // Оцінка відповідності катастрофі
            if (disaster.getPriorityProfessions().contains(prof)) score += 15;
            if (disaster.getRequiredItems().contains(bag)) score += 10;
        }

        if (hasDoctor) score += 15;
        if (hasEngineer) score += 15;
        if (hasFoodOrSeeds) score += 10;
        if (hasWeapon) score += 10;

        int finalScore = Math.min(100, score);
        boolean isVictory = finalScore >= 60;

        StringBuilder epilogue = new StringBuilder();
        epilogue.append("=== 📜 ЕПІЛОГ: 5 РОКІВ ПОТОРУ ===\n\n");
        epilogue.append("Шанс відновлення цивілізації: ").append(finalScore).append("%.\n\n");

        if (isVictory) {
            epilogue.append("🎉 УСПІХ! Ваша команда у бункері змогла вистояти під час катастрофи '")
                    .append(disaster.getName()).append("'. ");
            if (hasDoctor) epilogue.append("Медична підтримка врятувала групу від смертельних хвороб. ");
            if (hasEngineer) epilogue.append("Інженери відновили роботу енергомережі бункера. ");
            epilogue.append("\nЧерез 5 років двері бункера відкрилися, і ви заснували нове поселення!");
        } else {
            epilogue.append("❌ ТРАГЕДІЯ! Групі у бункері не вистачило критичних навичок та ресурсів для подолання '")
                    .append(disaster.getName()).append("'. ");
            if (!hasDoctor) epilogue.append("Відсутність кваліфікованого лікаря призвела до поширення інфекцій. ");
            if (!hasEngineer) epilogue.append("Система життєзабезпечення згодом вийшла з ладу. ");
            epilogue.append("\nБункер не зміг стати новим домом для людства.");
        }

        return new SurvivalResult(finalScore, epilogue.toString(), isVictory);
    }
}
