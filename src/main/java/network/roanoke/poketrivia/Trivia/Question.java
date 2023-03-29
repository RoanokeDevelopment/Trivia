package network.roanoke.poketrivia.Trivia;

import network.roanoke.poketrivia.PokeTrivia;

import java.util.List;

// contains a question string, a list of possible answers, and a function to check if its the correct answer
public class Question {

    public String question;
    public List<String> answers;
    public String difficulty;

    public Question(String question, List<String> answers, String difficulty) {
        this.question = question;
        this.answers = answers;
        this.difficulty = difficulty;
        PokeTrivia.LOGGER.info("Loaded question: " + question);
    }

}
