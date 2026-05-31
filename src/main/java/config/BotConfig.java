package config;

public class BotConfig {
    // ========== Telegram настройки ==========
    // ⚠️ ВНИМАНИЕ: Замените на свой токен!
    public static final String BOT_USERNAME = "@quiz_neighbors31_bot";
    public static final String BOT_TOKEN = "8787486207:AAFYFnHE4aaKzQyuFD0Hi575K6kO_c8dk2k";

    // ========== Прокси настройки ==========
    public static final boolean USE_PROXY = true;     // ВКЛЮЧАЕМ ПРОКСИ

    // Если используешь VPN клиент (например, WireGuard, OpenVPN) - прокси не нужен
    // Если используешь SOCKS5 прокси (например, Tor или прокси-сервис)
    public static final String PROXY_HOST = "127.0.0.1";
    public static final int PROXY_PORT = 10808;       // SOCKS5 порт
    public static final String PROXY_TYPE = "SOCKS";  // SOCKS или HTTP

    // Если прокси требует логин/пароль
    public static final boolean PROXY_AUTH = false;
    public static final String PROXY_USER = "";
    public static final String PROXY_PASS = "";

    // ========== Пути к ресурсам ==========
    public static final String RESOURCES_PATH = "src/main/resources/";
    public static final String COUNTRIES_DATA_FILE = "neighbors.csv";

    // ========== Настройки викторины ==========
    public static final boolean CASE_INSENSITIVE_ANSWERS = true;
}
