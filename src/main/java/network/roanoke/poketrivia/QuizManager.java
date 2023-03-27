package network.roanoke.poketrivia;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
                    answers.add(answerElem.getAsString());
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
        MutableText toSend = player.getDisplayName().copy().formatted(Formatting.GOLD)
                .append(Text.literal(" got the answer right ").formatted(Formatting.GREEN)
                        .append(Text.literal(" in " + ((System.currentTimeMillis() - questionTime) / 1000) + " seconds!").formatted(Formatting.GOLD)));
        server.getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(toSend));
        rewardManager.giveReward(player, currentQuestion);
        currentQuestion = null;
    }

}
