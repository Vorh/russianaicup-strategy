package control;

import model.Move;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Start extends Command {
    public Start(Info oldInfo, Info newInfo) {
        super(oldInfo, newInfo);
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
