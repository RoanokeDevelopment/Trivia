package network.roanoke.poketrivia.Commands;

import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import network.roanoke.poketrivia.Trivia;


import static net.minecraft.server.command.CommandManager.literal;

public class StartQuiz {
    public StartQuiz() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("quizstart")
                            .requires(Permissions.require("trivia.quizstart", 4))
                            .executes(this::executeStartQuiz)
            );
        });
    }

    private int executeStartQuiz(CommandContext<ServerCommandSource> ctx) {
        if (!Trivia.getInstance().quiz.quizInProgress()) {
            Trivia.getInstance().quizIntervalCounter = Trivia.getInstance().quizInterval + 1;
        }
        return 1;
    }

}
