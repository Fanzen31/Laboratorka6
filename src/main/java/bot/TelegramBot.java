package bot;

import config.BotConfig;
import game.QuizGame;
import loader.DataLoader;
import model.CountryNeighbors;
import model.QuizQuestion;
import session.SessionManager;
import session.UserSession;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private final SessionManager sessionManager = new SessionManager();
    private final List<CountryNeighbors> countriesData;

    public TelegramBot() throws IOException {
        this.countriesData = DataLoader.loadFromCSV(BotConfig.COUNTRIES_DATA_FILE);

        if (BotConfig.USE_PROXY) {
            setupProxy();
        }
    }

    private void setupProxy() {
        if (BotConfig.PROXY_TYPE.equalsIgnoreCase("SOCKS")) {
            System.setProperty("socksProxyHost", BotConfig.PROXY_HOST);
            System.setProperty("socksProxyPort", String.valueOf(BotConfig.PROXY_PORT));
        } else {
            System.setProperty("http.proxyHost", BotConfig.PROXY_HOST);
            System.setProperty("http.proxyPort", String.valueOf(BotConfig.PROXY_PORT));
            System.setProperty("https.proxyHost", BotConfig.PROXY_HOST);
            System.setProperty("https.proxyPort", String.valueOf(BotConfig.PROXY_PORT));
        }
        System.out.println("✅ Прокси настроен: " + BotConfig.PROXY_HOST + ":" + BotConfig.PROXY_PORT);
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BotConfig.BOT_USERNAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();
            String text = update.getMessage().getText();
            String userName = update.getMessage().getFrom().getFirstName();

            UserSession session = sessionManager.getSession(userId);



            switch (text.toLowerCase()) {
                case "/start":
                    // Стартовое приветствие (стандартная команда)
                    sendMessage(chatId, "🌍 *Добро пожаловать в викторину «Географический сосед»!*\n\n" +
                            "👋 Привет, " + userName + "!\n\n" +
                            "🎮 *Доступные команды:*\n" +
                            "/start_quiz - 🌟 начать новую викторину\n" +
                            "/stats - 📊 показать мою статистику\n" +
                            "/stop - 🛑 остановить текущую игру\n" +
                            "/help - 📖 показать справку\n\n" +
                            "🇪🇺 Я буду показывать страну, а ты называй её соседей!\n" +
                            "💡 *Совет:* достаточно назвать ОДНОГО соседа!");
                    break;

                case "/start_quiz":
                case "/game":
                case "/play":

                    handleQuizStart(chatId, session);
                    break;

                case "/help":
                    sendMessage(chatId, "📖 *Правила игры:*\n\n" +
                            "1️⃣ Введи /start_quiz для начала викторины\n" +
                            "2️⃣ Я покажу название страны\n" +
                            "3️⃣ Напиши *одного* соседа этой страны\n" +
                            "4️⃣ За каждый правильный ответ +1 балл\n" +
                            "5️⃣ Игра продолжается, пока не закончатся вопросы\n\n" +
                            "📊 *Команды:*\n" +
                            "/start_quiz - начать игру\n" +
                            "/stats - посмотреть текущий счёт\n" +
                            "/stop - завершить игру досрочно\n" +
                            "/help - показать это сообщение");
                    break;

                case "/stats":
                    handleStats(chatId, session);
                    break;

                case "/stop":
                    handleStop(chatId, session);
                    break;

                default:
                    handleAnswer(chatId, userId, session, text);
                    break;
            }
        }
    }


    private void handleQuizStart(long chatId, UserSession session) {

        if (session.isInQuiz()) {
            sendMessage(chatId, "⚠️ *У вас уже активна викторина!*\n\n" +
                    "➡️ Продолжайте отвечать на текущий вопрос\n" +
                    "🛑 Или введите /stop для завершения\n" +
                    "🔄 Или подождите, пока не ответите на все вопросы");
            return;
        }

        /
        if (countriesData.isEmpty()) {
            sendMessage(chatId, "❌ *Ошибка:* База данных стран пуста.\n\n" +
                    "Проверьте файл `" + BotConfig.COUNTRIES_DATA_FILE + "`\n" +
                    "Он должен находиться в папке `src/main/resources/`");
            return;
        }

        // Создаём новую игру
        QuizGame game = new QuizGame(countriesData);
        QuizQuestion firstQuestion = game.getRandomUnaskedQuestion();

        if (firstQuestion == null) {
            sendMessage(chatId, "❌ *Викторина недоступна:* Все вопросы были заданы!\n\n" +
                    "📝 Добавьте новые страны в файл `neighbors.csv`\n" +
                    "🔄 Или перезапустите бота");
            return;
        }

        // Сохраняем сессию
        session.startQuiz(game);
        session.setCurrentQuestion(firstQuestion);

        // Отправляем приветствие и первый вопрос
        sendMessage(chatId, String.format(
                "🎉 *Викторина началась!* 🎉\n\n" +
                        "\n" +
                        "📊 *Всего вопросов:* %d\n" +
                        "\n\n" +
                        "❓ *Вопрос №1:*\n" +
                        "%s\n\n" +
                        "💡 *Подсказка:* достаточно назвать ОДНОГО соседа!\n" +
                        "✏️ *Формат ответа:* просто напишите название страны",
                game.getTotalQuestions(),
                firstQuestion.getQuestion()));
    }

    private void handleStats(long chatId, UserSession session) {
        if (!session.isInQuiz()) {
            sendMessage(chatId, "📊 *Статистика отсутствует*\n\n" +
                    "🌟 Начните игру командой /start_quiz, чтобы накапливать статистику!\n\n" +
                    "💡 *Пример:* /start_quiz");
            return;
        }

        QuizGame game = session.getQuizGame();
        int score = game.getScore();
        int asked = game.getAskedCount();
        int total = game.getTotalQuestions();
        double percent = asked > 0 ? (score * 100.0 / asked) : 0;

        String progressBar = generateProgressBar(score, asked);

        sendMessage(chatId, String.format(
                "📊 *Ваша статистика викторины*\n\n" +
                        "\n" +
                        "🎯 Правильных ответов: *%d*\n" +
                        "❓ Всего отвечено: *%d*\n" +
                        "📚 Всего вопросов: *%d*\n" +
                        "⭐ Точность: *%.1f%%*\n" +
                        "\n\n" +
                        "%s\n\n" +
                        "📈 *Прогресс:* %d/%d вопросов\n\n" +
                        "➡️ Продолжайте отвечать или введите /stop",
                score, asked, total, percent, progressBar, asked, total));
    }

    private void handleStop(long chatId, UserSession session) {
        if (!session.isInQuiz()) {
            sendMessage(chatId, "❌ *Нет активной викторины*\n\n" +
                    "🌟 Используйте /start_quiz для начала игры.");
            return;
        }

        QuizGame game = session.getQuizGame();
        int finalScore = game.getScore();
        int asked = game.getAskedCount();
        int total = game.getTotalQuestions();

        session.endQuiz();

        sendMessage(chatId, String.format(
                "🛑 *Викторина остановлена*\n\n" +
                        "\n" +
                        "🏆 *Ваш итоговый результат:*\n" +
                        "✅ Правильных ответов: *%d*\n" +
                        "📝 Всего отвечено: *%d* из %d\n" +
                        "\n\n" +
                        "🌟 Напишите /start_quiz, чтобы начать новую игру!",
                finalScore, asked, total));
    }

    private void handleAnswer(long chatId, long userId, UserSession session, String answer) {
        if (!session.isInQuiz()) {
            sendMessage(chatId, "❓ *Нет активной викторины*\n\n" +
                    "🌟 Напишите /start_quiz, чтобы начать игру!\n" +
                    "📖 Или /help для списка всех команд.");
            return;
        }

        QuizGame game = session.getQuizGame();
        QuizQuestion currentQuestion = session.getCurrentQuestion();

        if (currentQuestion == null) {
            sendMessage(chatId, "⚠️ *Техническая ошибка:* вопрос не найден.\n\n" +
                    "Пожалуйста, начните заново с /start_quiz");
            session.endQuiz();
            return;
        }

        // Проверяем ответ
        boolean isCorrect = game.checkAnswer(currentQuestion, answer);

        if (isCorrect) {
            String[] congratulations = {
                    "✅ *Верно!* +1 балл! 🎉",
                    "🌟 *Правильно!* Отличная работа!",
                    "🏆 *Точно!* Вы знаете географию!",
                    "📚 *Правильный ответ!* Так держать!",
                    "🎯 *Бинго!* Сосед угадан!",
                    "💪 *Молодец!* Продолжайте в том же духе!"
            };
            String congrats = congratulations[(int)(Math.random() * congratulations.length)];
            sendMessage(chatId, congrats);
        } else {
            String correctNeighbors = String.join(", ",
                    currentQuestion.getCountryNeighbors().getNeighbors());
            sendMessage(chatId, String.format(
                    "❌ *Неверно*\n\n" +
                            "Страна: *%s*\n" +
                            "✅ Правильные соседи:\n" +
                            "`%s`\n\n" +
                            "💪 Попробуй следующий вопрос!",
                    currentQuestion.getCountryNeighbors().getCountry(),
                    correctNeighbors));
        }

        // Задаём следующий вопрос
        QuizQuestion nextQuestion = game.getRandomUnaskedQuestion();

        if (nextQuestion == null) {
            // Викторина закончилась
            int finalScore = game.getScore();
            int totalAnswered = game.getAskedCount();
            int total = game.getTotalQuestions();

            sendMessage(chatId, String.format(
                    "🏆 *ПОБЕДА! Викторина завершена!* 🏆\n\n" +
                            "\n" +
                            "🎉 *Поздравляем!* Вы прошли все вопросы!\n" +
                            "⭐ *Итоговый результат:* %d из %d\n" +
                            "📊 *Точность:* %.1f%%\n" +
                            "\n\n" +
                            "🌟 Напишите /start_quiz для новой игры!",
                    finalScore, total, (finalScore * 100.0 / totalAnswered)));
            session.endQuiz();
        } else {
            session.setCurrentQuestion(nextQuestion);
            sendMessage(chatId, String.format(
                    "➡️ *Следующий вопрос (%d/%d)*\n\n" +
                            "\n" +
                            "%s\n\n" +
                            "✏️ Ваш ответ:",
                    game.getAskedCount(), game.getTotalQuestions(),
                    nextQuestion.getQuestion()));
        }
    }

    private String generateProgressBar(int score, int total) {
        if (total == 0) return "⚪ [Нет вопросов]";

        int filled = (int) Math.round((score * 20.0) / total);
        StringBuilder bar = new StringBuilder();
        bar.append("📊 ");
        for (int i = 0; i < 20; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        return bar.toString();
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
            // Пробуем отправить без форматирования
            message.setParseMode(null);
            try {
                execute(message);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }
}