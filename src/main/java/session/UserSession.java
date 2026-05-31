package session;

import game.QuizGame;
import model.QuizQuestion;

public class UserSession {
    private final long userId;
    private QuizGame quizGame;
    private QuizQuestion currentQuestion;
    private boolean inQuiz;

    public UserSession(long userId) {
        this.userId = userId;
        this.inQuiz = false;
        this.quizGame = null;
        this.currentQuestion = null;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isInQuiz() {
        return inQuiz;
    }

    public void startQuiz(QuizGame game) {
        this.quizGame = game;
        this.inQuiz = true;
        this.currentQuestion = null;
    }

    public void endQuiz() {
        this.inQuiz = false;
        this.quizGame = null;
        this.currentQuestion = null;
    }

    public QuizGame getQuizGame() {
        return quizGame;
    }

    public QuizQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(QuizQuestion question) {
        this.currentQuestion = question;
    }
}