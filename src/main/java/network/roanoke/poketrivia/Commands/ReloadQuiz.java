package network.roanoke.poketrivia.Commands;

import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import network.roanoke.poketrivia.PokeTrivia;
import network.roanoke.poketrivia.Trivia.QuizManager;

import static net.minecraft.server.command.CommandManager.literal;

public class ReloadQuiz {
    public ReloadQuiz() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("quizreload")
                            .requires(Permissions.require("poketrivia.quizreload", 4))
                            .executes(this::executeReloadQuiz)
            );
        });
    }

    private int executeReloadQuiz(CommandContext<ServerCommandSource> ctx) {
        if (PokeTrivia.getInstance().quiz.quizInProgress()) {
            PokeTrivia.getInstance().quizIntervalCounter = 0;
        }
        PokeTrivia.getInstance().quiz = new QuizManager();
        return 1;
    }
}
