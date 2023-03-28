package network.roanoke.poketrivia.Trivia;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import network.roanoke.poketrivia.PokeTrivia;
import network.roanoke.poketrivia.Reward.Reward;
import network.roanoke.poketrivia.Reward.RewardManager;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class QuizManager {

    private TriviaQuestion currentQuestion = null;
    private Long questionTime = System.currentTimeMillis();
    private List<TriviaQuestion> questionPool = new ArrayList<>();
    private RewardManager rewardManager = null;

    public QuizManager() {
        loadQuestions();
    }

    // Load the questions from the config file
    public void loadQuestions() {
        // get the default fabric api config directory and then create a new file called "poketrivia.json"
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("poketrivia.json");
        // create a file from the path if it does not exist
        File configFile = configPath.toFile();
        if (!configFile.exists()) {
            // create the file
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // Read the JSON file
        JsonElement root;
        try {
            root = JsonParser.parseReader(new FileReader(configFile));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Get the "questions" object
        JsonObject questionsObj = root.getAsJsonObject().get("questions").getAsJsonObject();
        PokeTrivia.LOGGER.info("Loading the rewards...");
        JsonObject rewardsObj = root.getAsJsonObject().get("rewards").getAsJsonObject();
        rewardManager = new RewardManager(rewardsObj);

        PokeTrivia.LOGGER.info("Loading the questions...");
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
                TriviaQuestion question = new TriviaQuestion(questionText, answers, difficulty);
                questionPool.add(question);
            }
        }
    }

    public Boolean quizInProgress() {
        return currentQuestion != null;
    }

    public Boolean isRightAnswer(String answer) {
        return currentQuestion.answers.contains(answer);
    }

    public void startQuiz(MinecraftServer server) {
        // Get a random question from the pool
        currentQuestion = questionPool.get((int) (Math.random() * questionPool.size()));
        // Send the question to all players
        MutableText toSend = Text.literal(currentQuestion.question).formatted(Formatting.GOLD);
        server.getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(toSend));
        // Set the time the question was asked
        questionTime = System.currentTimeMillis();
    }

    public void processQuizWinner(ServerPlayerEntity player, MinecraftServer server) {
        Reward reward = rewardManager.giveReward(player, currentQuestion);

        MutableText toSend = player.getDisplayName().copy()
                .append(Text.literal(" got the answer right in ")
                        .append(Text.literal(((System.currentTimeMillis() - questionTime) / 1000) + " seconds!").formatted(Formatting.GOLD)));

        if (reward == null) {
            PokeTrivia.LOGGER.error("Failed to get reward for " + player.getName() + " for question " + currentQuestion.question);
        } else {
            toSend.append(Text.literal(" and they won a " + reward.itemDisplayName + "!").formatted(Formatting.WHITE));
        }

        server.getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(toSend));
        currentQuestion = null;
    }

}
