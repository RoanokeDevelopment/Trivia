package network.roanoke.poketrivia;

import java.util.List;

// contains a question string, a list of possible answers, and a function to check if its the correct answer
public class TriviaQuestion {

    public String question;
    public List<String> answers;
    public String difficulty;

    public TriviaQuestion(String question, List<String> answers, String difficulty) {
        this.question = question;
        this.answers = answers;
        this.difficulty = difficulty;
        PokeTrivia.LOGGER.info("Loaded question: " + question);
    }

}
