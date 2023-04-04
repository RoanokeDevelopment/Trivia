package network.roanoke.poketrivia.Commands;

import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import network.roanoke.poketrivia.Trivia;
import network.roanoke.poketrivia.Quiz.QuizManager;

import static net.minecraft.server.command.CommandManager.literal;

public class ReloadQuiz {
    public ReloadQuiz() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("quizreload")
                            .requires(Permissions.require("trivia.quizreload", 4))
                            .executes(this::executeReloadQuiz)
            );
        });
    }

    private int executeReloadQuiz(CommandContext<ServerCommandSource> ctx) {
        if (Trivia.getInstance().quiz.quizInProgress()) {
            Trivia.getInstance().quizIntervalCounter = 0;
        }
        Trivia.getInstance().quiz = new QuizManager();
        return 1;
    }
}
