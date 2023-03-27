package network.roanoke.poketrivia;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PokeTrivia implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Trivia");
    QuizManager quiz = new QuizManager();
    Integer quizIntervalCounter = 0;
    Integer quizInterval = 30 * 20;

    @Override
    public void onInitialize() {

        LOGGER.info("Starting up PokeTrivia");

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (!quiz.quizInProgress() && (server.getPlayerManager().getPlayerList().size() > 0)) {
                if (quizIntervalCounter >= quizInterval) {
                    quizIntervalCounter = 0;
                    quiz.startQuiz(server);
                } else {
                    quizIntervalCounter++;
                }
            }
        });

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            LOGGER.info("Checking chat message");
            if (quiz.quizInProgress()) {
                LOGGER.info("Quiz is in progress....");
                LOGGER.info(message.getContent().getString());
                if (quiz.isRightAnswer(message.getContent().getString())) {
                    LOGGER.info("Chat was correct answer");
                    quiz.processQuizWinner(sender, sender.server);
                }
            }
        });


    }

}