package network.roanoke.trivia.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.text.Text;
import network.roanoke.trivia.Trivia;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Messages {
    private HashMap<String, String> messages;
    private String prefix = "";

    public Messages(Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                // Create all directories in the path if they don't exist yet.
                Files.createDirectories(filePath.getParent());

                try (InputStream is = getClass().getResourceAsStream("/trivia-messages.json");
                     OutputStream os = new FileOutputStream(filePath.toFile())) {
                    IOUtils.copy(is, os);
                } catch (FileNotFoundException e) {
                    try {
                        filePath.toFile().createNewFile();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Gson gson = new Gson();
            try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                messages = gson.fromJson(reader, new TypeToken<HashMap<String, String>>(){}.getType());
                prefix = getMessage("trivia.prefix");
            } catch (IOException e) {
                Trivia.LOGGER.info("Failed to load Trivia/messages.json");
                messages = new HashMap<>();
                prefix = "";
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories for path " + filePath, e);
        }
    }

    public String getMessage(String key) {
        String message = messages.getOrDefault(key, "Placeholder message for missing key");
        message = message.replace("{prefix}", this.prefix == null ? "" : this.prefix);
        return message;
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);
        for (String pKey: placeholders.keySet()) {
            message = message.replace(pKey, placeholders.get(pKey));
        }
        return message;
    }

    public Text getDisplayText(String message) {
        if (Trivia.adventure != null)  {
            return Trivia.adventure.toNative(
                    Trivia.mm.deserialize(message)
            );
        }
        return Text.literal("Error converting MiniMessage format");
    }
}