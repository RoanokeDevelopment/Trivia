package network.roanoke.trivia.Commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import network.roanoke.trivia.Config;
import network.roanoke.trivia.Quiz.QuizManager;
import network.roanoke.trivia.Trivia;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class QuizCommands {
    public QuizCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("quiz")
                            .then(
                                    literal("interval")
                                            .requires(Permissions.require("trivia.interval", 4))
                                            .then(argument("intervalSeconds", IntegerArgumentType.integer(1, 999999)).executes(this::executeQuizInterval))
                            )
                            .then(
                                    literal("start").requires(Permissions.require("trivia.start", 4))
                                            .executes(this::executeStartQuiz)
                            )
                            .then(
                                    literal("reload").requires(Permissions.require("trivia.reload", 4))
                                            .executes(this::executeReloadQuiz)
                            )
                            .then(
                                    literal("timeout").requires(Permissions.require("trivia.timeout", 4))
                                            .then(argument("timeoutSeconds", IntegerArgumentType.integer(1, 999999)).executes(this::executeQuizTimeout))
                            )
            );
        });
    }

    private int executeQuizTimeout(CommandContext<ServerCommandSource> ctx) {
        Trivia.getInstance().config.setQuizTimeOut(ctx.getArgument("timeoutSeconds", Integer.class) * 20);
        ctx.getSource().sendMessage(Text.literal("Updated Quiz Timeout to " + ctx.getArgument("timeoutSeconds", Integer.class) + " seconds."));
        return 1;
    }

    private int executeQuizInterval(CommandContext<ServerCommandSource> ctx) {
        Trivia.getInstance().config.setQuizInterval(ctx.getArgument("intervalSeconds", Integer.class) * 20);
        ctx.getSource().sendMessage(Text.literal("Updated Quiz Interval to " + ctx.getArgument("intervalSeconds", Integer.class) + " seconds."));
        return 1;
    }

    private int executeStartQuiz(CommandContext<ServerCommandSource> ctx) {
        if (!Trivia.getInstance().quiz.quizInProgress()) {
            Trivia.getInstance().quizIntervalCounter = Trivia.getInstance().config.getQuizInterval() + 1;
        } else {
            Trivia.getInstance().quizIntervalCounter = 0;
            Trivia.getInstance().quizTimeOutCounter = 0;
            Trivia.getInstance().quiz.startQuiz(ctx.getSource().getServer());
        }
        return 1;
    }

    private int executeReloadQuiz(CommandContext<ServerCommandSource> ctx) {
        if (Trivia.getInstance().quiz.quizInProgress()) {
            Trivia.getInstance().quizIntervalCounter = 0;
        }
        Trivia.getInstance().quiz = new QuizManager();
        Trivia.getInstance().config = new Config();
        return 1;
    }

}
