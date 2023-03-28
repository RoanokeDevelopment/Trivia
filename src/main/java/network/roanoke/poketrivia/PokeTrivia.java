package network.roanoke.poketrivia;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import network.roanoke.poketrivia.Commands.QuizInterval;
import network.roanoke.poketrivia.Commands.ReloadQuiz;
import network.roanoke.poketrivia.Commands.StartQuiz;
import network.roanoke.poketrivia.Trivia.QuizManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PokeTrivia implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Trivia");
    public static PokeTrivia instance;
    public QuizManager quiz = new QuizManager();
    public Integer quizIntervalCounter = 0;
    public Integer quizInterval = 600 * 20;

    @Override
    public void onInitialize() {

        LOGGER.info("Starting up PokeTrivia");
        instance = this;

        new StartQuiz();
        new ReloadQuiz();
        new QuizInterval();

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
            if (quiz.quizInProgress()) {
                if (quiz.isRightAnswer(message.getContent().getString())) {
                    LOGGER.info("Chat was correct answer");
                    quiz.processQuizWinner(sender, sender.server);
                }
            }
        });


    }

    public static PokeTrivia getInstance() {
        return instance;
    }

}