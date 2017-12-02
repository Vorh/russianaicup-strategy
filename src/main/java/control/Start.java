package control;

import model.Move;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Start extends Command {
    public Start() {
    }

    @Override
    public Consumer<Move> getMove() {
        return move;
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}
