package dev.roanoke.trivia.Reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import dev.roanoke.trivia.Trivia;
import dev.roanoke.trivia.Quiz.Question;

import java.util.ArrayList;
import java.util.HashMap;

public class RewardManager {

    private HashMap<String, ArrayList<Reward>> rewardPools = new HashMap<>();

    public RewardManager(JsonObject rewardsObj) {

        Trivia.LOGGER.info("Loading rewards...");

        for (String difficulty : rewardsObj.keySet()) {
            JsonArray questionsArr = rewardsObj.get(difficulty).getAsJsonArray();

            // Loop over the questions in the difficulty level
            for (JsonElement rewardElem : questionsArr) {
                JsonObject rewardObj = rewardElem.getAsJsonObject();

                // Get the question text
                String itemName = rewardObj.get("item_name").getAsString();
                String itemDisplayName = rewardObj.get("display_name").getAsString();
                Integer quantity = rewardObj.get("quantity").getAsInt();

                // Create the TriviaQuestion object and add it to the list
                Reward reward = new Reward(itemName, itemDisplayName, quantity);
                if (reward.itemStack == null) {
                    continue;
                }
                if (rewardPools.containsKey(difficulty)) {
                    rewardPools.get(difficulty).add(reward);
                } else {
                    ArrayList<Reward> rewards = new ArrayList<>();
                    rewards.add(reward);
                    rewardPools.put(difficulty, rewards);
                }
            }
        }

        // output the amount of rewards loaded
        for (String difficulty : rewardPools.keySet()) {
            System.out.println("Loaded " + rewardPools.get(difficulty).size() + " rewards for difficulty " + difficulty);
        }
    }

    // give the winner a random reward from the difficulty pool
    public Reward giveReward(ServerPlayerEntity player, Question question) {
        if (rewardPools.containsKey(question.difficulty)) {
            ArrayList<Reward> rewards = rewardPools.get(question.difficulty);
            Reward reward = rewards.get((int) (Math.random() * rewards.size()));

            if (!player.giveItemStack(reward.itemStack.copy())) {
                player.dropItem(reward.itemStack.copy(), false);
            }

            return reward;
        }
        return null;
    }

}
