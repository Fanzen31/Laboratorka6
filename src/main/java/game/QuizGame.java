package game;



import model.CountryNeighbors;
import model.QuizQuestion;

import java.util.*;

public class QuizGame {
    private final List<QuizQuestion> allQuestions;
    private final Set<Integer> askedIndices;
    private int score;

    public QuizGame(List<CountryNeighbors> countriesData) {
        this.allQuestions = new ArrayList<>();
        for (CountryNeighbors cn : countriesData) {
            allQuestions.add(new QuizQuestion(cn));
        }
        this.askedIndices = new HashSet<>();
        this.score = 0;
    }

    public QuizQuestion getRandomUnaskedQuestion() {
        if (askedIndices.size() >= allQuestions.size()) {
            return null;
        }
        Random rand = new Random();
        int index;
        do {
            index = rand.nextInt(allQuestions.size());
        } while (askedIndices.contains(index));
        askedIndices.add(index);
        return allQuestions.get(index);
    }

    public boolean checkAnswer(QuizQuestion question, String userAnswer) {
        boolean correct = question.isCorrect(userAnswer);
        if (correct) {
            score++;
        }
        return correct;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return allQuestions.size();
    }

    public int getAskedCount() {
        return askedIndices.size();
    }

    public Set<Integer> getAskedIndices() {
        return askedIndices;
    }
}

