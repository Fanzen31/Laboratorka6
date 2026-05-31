package model;

import config.BotConfig;

public class QuizQuestion {
    private final CountryNeighbors countryNeighbors;

    public QuizQuestion(CountryNeighbors countryNeighbors) {
        this.countryNeighbors = countryNeighbors;
    }

    public String getQuestion() {
        return "🌍 Назовите соседа страны: *" + countryNeighbors.getCountry() + "*";
    }

    public boolean isCorrect(String userAnswer) {
        String answer = BotConfig.CASE_INSENSITIVE_ANSWERS
                ? userAnswer.trim().toLowerCase()
                : userAnswer.trim();

        for (String neighbor : countryNeighbors.getNeighbors()) {
            String neighborNormalized = BotConfig.CASE_INSENSITIVE_ANSWERS
                    ? neighbor.toLowerCase()
                    : neighbor;
            if (answer.equals(neighborNormalized)) {
                return true;
            }
        }
        return false;
    }

    public CountryNeighbors getCountryNeighbors() {
        return countryNeighbors;
    }
}
