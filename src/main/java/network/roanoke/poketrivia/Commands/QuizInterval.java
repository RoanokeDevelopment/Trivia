package network.roanoke.poketrivia.Commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import network.roanoke.poketrivia.PokeTrivia;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class QuizInterval {
    public QuizInterval() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("quizinterval")
                            .requires(Permissions.require("poketrivia.quizinterval", 4))
                            .then(argument("intervalSeconds", IntegerArgumentType.integer(1, 999999)).executes(this::executeQuizInterval))
            );
        });
    }

    private int executeQuizInterval(CommandContext<ServerCommandSource> ctx) {
        PokeTrivia.getInstance().quizInterval = ctx.getArgument("intervalSeconds", Integer.class) * 20;
        PokeTrivia.LOGGER.info("Updated Quiz Interval to " + ctx.getArgument("intervalSeconds", Integer.class) + " seconds.");
        return 1;
    }
}
