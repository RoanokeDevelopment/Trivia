package dev.roanoke.trivia;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import dev.roanoke.trivia.Commands.QuizCommands;
import dev.roanoke.trivia.Quiz.QuizManager;
import dev.roanoke.trivia.Utils.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trivia implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Trivia");
    public static FabricServerAudiences adventure;
    public static MiniMessage mm = MiniMessage.miniMessage();

    public static Messages messages = new Messages(FabricLoader.getInstance().getConfigDir().resolve("Trivia/messages.json"));
    public static Trivia instance;
    public QuizManager quiz = new QuizManager();
    public Config config = new Config();
    public Integer quizIntervalCounter = 0;
    public Integer quizTimeOutCounter = 0;

    @Override
    public void onInitialize() {

        instance = this;

        new QuizCommands();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            adventure = FabricServerAudiences.of(server);
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (!quiz.quizInProgress() && (server.getPlayerManager().getPlayerList().size() > 0)) {
                if (quizIntervalCounter >= config.getQuizInterval()) {
                    quizIntervalCounter = 0;
                    quiz.startQuiz(server);
                } else {
                    quizIntervalCounter++;
                }
            } else {
                if (quizTimeOutCounter >= config.getQuizTimeOut()) {
                    quizTimeOutCounter = 0;
                    quizIntervalCounter = 0;
                    quiz.timeOutQuiz(server); // move timeout message to this function later
                } else {
                    quizTimeOutCounter++;
                }

            }
        });

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            if (quiz.quizInProgress()) {
                if (quiz.isRightAnswer(message.getContent().getString())) {
                    LOGGER.info("Trivia question was answered correctly.");
                    quiz.processQuizWinner(sender, sender.server, message.getContent().getString());
                }
            }
        });


    }

    public static Trivia getInstance() {
        return instance;
    }

}