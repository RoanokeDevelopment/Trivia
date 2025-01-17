package dev.roanoke.trivia;

import net.fabricmc.loader.api.FabricLoader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {
    private static final Path CONFIG_FILE_PATH = FabricLoader.getInstance().getConfigDir().resolve("Trivia/config.properties");
    private final Properties properties;

    public Config() {
        properties = new Properties();
        try {
            if (Files.exists(CONFIG_FILE_PATH)) {
                FileInputStream file = new FileInputStream(CONFIG_FILE_PATH.toFile());
                properties.load(file);
                file.close();
            } else {
                // If the file doesn't exist, create it with default values
                setQuizTimeOut(450);
                setQuizInterval(120);
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getQuizTimeOut() {
        return Integer.parseInt(properties.getProperty("quizTimeOut")) * 20;
    }

    public void setQuizTimeOut(int timeout) {
        properties.setProperty("quizTimeOut", String.valueOf(timeout));
        save();
    }

    public int getQuizInterval() {
        return Integer.parseInt(properties.getProperty("quizInterval")) * 20;
    }

    public void setQuizInterval(int interval) {
        properties.setProperty("quizInterval", String.valueOf(interval));
        save();
    }

    public void save() {
        try {
            if (!CONFIG_FILE_PATH.toFile().exists()) {
                Files.createFile(CONFIG_FILE_PATH);
            }
            FileOutputStream file = new FileOutputStream(CONFIG_FILE_PATH.toFile());
            properties.store(file, null);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}