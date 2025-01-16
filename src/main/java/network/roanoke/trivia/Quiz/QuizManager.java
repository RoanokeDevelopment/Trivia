package network.roanoke.trivia.Quiz;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import network.roanoke.trivia.Trivia;
import network.roanoke.trivia.Reward.Reward;
import network.roanoke.trivia.Reward.RewardManager;
import network.roanoke.trivia.Utils.Messages;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizManager {

    private Question currentQuestion = null;
    private Long questionTime = System.currentTimeMillis();
    private List<Question> questionPool = new ArrayList<>();
    private RewardManager rewardManager = null;

    public QuizManager() {
        try {
            ensureFilesExist();
        } catch (IOException e) {
            e.printStackTrace();
            Trivia.LOGGER.error("Failed to copy built in default files to Config/Trivia");
        }
        loadQuestions();
        loadRewards();
    }

    public void ensureFilesExist() throws IOException {
        // get the default fabric api config directory and then create a new file called "poketrivia.json"
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("Trivia");
        File configFile = configPath.toFile();
        if (!configFile.exists()) {
            // create the file
            try {
                configFile.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Path questionsPath = FabricLoader.getInstance().getConfigDir().resolve("Trivia/questions.json");
        Path rewardsPath = FabricLoader.getInstance().getConfigDir().resolve("Trivia/rewards.json");
        File questionsFile = questionsPath.toFile();
        File rewardsFile = rewardsPath.toFile();
        if (!questionsFile.exists()) {
            questionsFile.createNewFile();
            // create the file
            InputStream in = Trivia.class.getResourceAsStream("/questions.json");

            // Create a FileOutputStream to write the file to the directory
            OutputStream out = new FileOutputStream(questionsFile);

            // Use a buffer to read and write the file in chunks
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            // Close the input and output streams
            in.close();
            out.close();
        }
        if (!rewardsFile.exists()) {
            rewardsFile.createNewFile();

            InputStream in = Trivia.class.getResourceAsStream("/rewards.json");

            // Create a FileOutputStream to write the file to the directory
            OutputStream out = new FileOutputStream(rewardsFile);

            // Use a buffer to read and write the file in chunks
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            // Close the input and output streams
            in.close();
            out.close();
        }
    }

    // Load the questions from the config file
    public void loadQuestions() {
        // get the default fabric api config directory and then create a new file called "poketrivia.json"
        Path questionsPath = FabricLoader.getInstance().getConfigDir().resolve("Trivia/questions.json");
        // create a file from the path if it does not exist
        File questionFile = questionsPath.toFile();


        // Read the JSON file
        JsonElement root;
        try {
            root = JsonParser.parseReader(new FileReader(questionFile));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Get the "questions" object
        JsonObject questionsObj = root.getAsJsonObject();
        Trivia.LOGGER.info("Loading the questions...");
        // Loop over the difficulty levels
        for (String difficulty : questionsObj.keySet()) {
            JsonArray questionsArr = questionsObj.get(difficulty).getAsJsonArray();

            // Loop over the questions in the difficulty level
            for (JsonElement questionElem : questionsArr) {
                JsonObject questionObj = questionElem.getAsJsonObject();

                // Get the question text
                String questionText = questionObj.get("question").getAsString();

                // Get the list of answers
                JsonArray answersArr = questionObj.get("answers").getAsJsonArray();
                List<String> answers = new ArrayList<>();
                for (JsonElement answerElem : answersArr) {
                    answers.add(answerElem.getAsString().toLowerCase());
                }

                // Create the TriviaQuestion object and add it to the list
                Question question = new Question(questionText, answers, difficulty);
                questionPool.add(question);
            }
        }
        Trivia.LOGGER.info("Loaded " + questionPool.size() + " questions.");
    }

    public void loadRewards() {
        // get the default fabric api config directory and then create a new file called "poketrivia.json"
        Path rewardsPath = FabricLoader.getInstance().getConfigDir().resolve("Trivia/rewards.json");
        // create a file from the path if it does not exist
        File rewardsFile = rewardsPath.toFile();

        JsonElement root;
        try {
            root = JsonParser.parseReader(new FileReader(rewardsFile));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        JsonObject rewardsObj = root.getAsJsonObject();
        rewardManager = new RewardManager(rewardsObj);
    }

    public Boolean quizInProgress() {
        return currentQuestion != null;
    }

    public Boolean isRightAnswer(String guess) {
        for (String answer: currentQuestion.answers) {
            if (answer.equalsIgnoreCase(guess)) {
                return true;
            }
        }
        return false;
    }

    public void startQuiz(MinecraftServer server) {
        // Get a random question from the pool
        currentQuestion = questionPool.get((int) (Math.random() * questionPool.size()));

        server.getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(
                Trivia.messages.getDisplayText(
                        Trivia.messages.getMessage("trivia.ask_question",
                                Map.of("{question}", currentQuestion.question))
                )
        ));

        // Set the time the question was asked
        questionTime = System.currentTimeMillis();
    }

    public void processQuizWinner(ServerPlayerEntity player, MinecraftServer server) {
        Reward reward = rewardManager.giveReward(player, currentQuestion);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{player}", player.getGameProfile().getName());
        placeholders.put("{reward}", reward.itemDisplayName == null ? "REWARD_ERROR" : reward.itemDisplayName);
        placeholders.put("{time}", String.valueOf(((System.currentTimeMillis() - questionTime) / 1000)));
        placeholders.put("{answer}", String.join(", ", currentQuestion.answers));

        server.getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(
                Trivia.messages.getDisplayText(
                        Trivia.messages.getMessage("trivia.correct_answer", placeholders)
                )
        ));

        currentQuestion = null;
    }

    public void timeOutQuiz(MinecraftServer server) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{answer}", String.join(", ", currentQuestion.answers));

        server.getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(
                Trivia.messages.getDisplayText(Trivia.messages.getMessage("trivia.no_answer", placeholders)))
        );
        this.currentQuestion = null;
    }

}
