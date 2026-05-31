package loader;

import config.BotConfig;
import model.CountryNeighbors;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DataLoader {

    public static List<CountryNeighbors> loadFromCSV(String filename) throws IOException {
        List<CountryNeighbors> list = new ArrayList<>();
        String filePath = BotConfig.RESOURCES_PATH + filename;

        // Пробуем загрузить из resources через ClassLoader
        InputStream is = DataLoader.class.getClassLoader().getResourceAsStream(filename);

        if (is == null) {
            // Если не нашли в resources, пробуем по прямому пути
            try {
                is = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                System.err.println("❌ Файл не найден: " + filePath);
                System.err.println("Проверьте, что файл neighbors.csv находится в папке src/main/resources/");
                return list;
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line = br.readLine(); // читаем заголовок
            if (line == null) {
                System.err.println("❌ Файл пуст!");
                return list;
            }

            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length < 2) {
                    System.err.println("⚠️ Пропущена строка " + lineNumber + ": неверный формат -> " + line);
                    continue;
                }

                String country = parts[0].trim();
                String[] neighborsArray = parts[1].split(";");
                List<String> neighbors = new ArrayList<>();
                for (String n : neighborsArray) {
                    String neighbor = n.trim();
                    if (!neighbor.isEmpty()) {
                        neighbors.add(neighbor);
                    }
                }

                if (neighbors.isEmpty()) {
                    System.err.println("⚠️ У страны '" + country + "' нет соседей (строка " + lineNumber + ")");
                }

                list.add(new CountryNeighbors(country, neighbors));
                System.out.println("✅ Загружена: " + country + " -> " + neighbors);
            }
        }

        System.out.println("📊 Всего загружено стран: " + list.size());
        return list;
    }
}
