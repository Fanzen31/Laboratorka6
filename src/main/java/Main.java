import bot.TelegramBot;
import config.BotConfig;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Запуск бота-викторины «Географический сосед»...");
        System.out.println("📋 Bot username: " + BotConfig.BOT_USERNAME);
        System.out.println("🔧 Proxy enabled: " + BotConfig.USE_PROXY);

        try {
            TelegramBot bot = new TelegramBot();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            System.out.println("✅ Бот успешно запущен!");
            System.out.println("💡 Доступные команды: /quiz, /stats, /stop, /help");
        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка регистрации бота: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}