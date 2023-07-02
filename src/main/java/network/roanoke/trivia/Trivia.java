package network.roanoke.trivia;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import network.roanoke.trivia.Commands.QuizCommands;
import network.roanoke.trivia.Quiz.QuizManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trivia implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Trivia");
    public static Trivia instance;
    public QuizManager quiz = new QuizManager();
    public Config config = new Config();
    public Integer quizIntervalCounter = 0;
    public Integer quizTimeOutCounter = 0;

    @Override
    public void onInitialize() {

        instance = this;

        new QuizCommands();

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
                    server.getPlayerManager().getPlayerList().forEach(serverPlayer -> serverPlayer.sendMessage(Text.literal("No one answered the quiz in time!").formatted(Formatting.GOLD)));
                    quiz.timeOutQuiz(); // move timeout message to this function later
                } else {
                    quizTimeOutCounter++;
                }

            }
        });

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            if (quiz.quizInProgress()) {
                if (quiz.isRightAnswer(message.getContent().getString())) {
                    LOGGER.info("Trivia question was answered correctly.");
                    quiz.processQuizWinner(sender, sender.server);}
            }
        });


    }

    public static Trivia getInstance() {
        return instance;
    }

}