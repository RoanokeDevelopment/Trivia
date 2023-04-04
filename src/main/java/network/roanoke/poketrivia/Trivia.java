package network.roanoke.poketrivia;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import network.roanoke.poketrivia.Commands.QuizInterval;
import network.roanoke.poketrivia.Commands.ReloadQuiz;
import network.roanoke.poketrivia.Commands.StartQuiz;
import network.roanoke.poketrivia.Quiz.QuizManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trivia implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Trivia");
    public static Trivia instance;
    public QuizManager quiz = new QuizManager();
    public Integer quizIntervalCounter = 0;
    public Integer quizTimeOutCounter = 0;
    public Integer quizTimeOut = 120 * 20;
    
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
            } else {
                if (quizTimeOutCounter >= quizTimeOut) {
                    quizTimeOutCounter = 0;
                    quizIntervalCounter = 0;
                    server.getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(Text.literal("No one answered the quiz in time!").formatted(Formatting.GOLD)));
                    quiz.startQuiz(server);
                } else {
                    quizTimeOutCounter++;
                }

            }
        });

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            if (quiz.quizInProgress()) {
                if (quiz.isRightAnswer(message.getContent().getString())) {
                    LOGGER.info("Chat was correct answer");
                    quiz.processQuizWinner(sender, sender.server);}
            }
        });


    }

    public static Trivia getInstance() {
        return instance;
    }

}