package control;

import model.ActionType;
import model.Move;
import model_custom.Formation;

import java.util.function.Consumer;

/**
 * Created by vorh on 12/3/17.
 */
public class Assign extends Command{


    public Assign(Formation formation) {
        this.formation = formation;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public Consumer<Move> getMove() {
        return move ->{
            move.setAction(ActionType.ASSIGN);
            move.setGroup(formation.getGroupId());
        };
    }
}
