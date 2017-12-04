package control;

import model.ActionType;
import model.Move;
import model_custom.Formation;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class CreateGroup extends Command {


    private double top;
    private double right;
    private double bottom;
    private double left;
    private Consumer<Move> move;

    public CreateGroup(Formation formation){
        super(formation);

        move = move ->{
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setGroup(formation.getGroupId());
        };
    }

    public CreateGroup(double top, double right, double bottom, double left, Formation formation) {
        super(formation);
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;

        move = move -> {
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setRight(right);
            move.setBottom(bottom);
            move.setLeft(left);
            move.setTop(top);
            move.setVehicleType(formation.getVehicleType());
        };

    }



    @Override
    public List<Consumer<Move>> getMoves() {
        moves.add(move);
        moves.add(move ->{
            move.setAction(ActionType.ASSIGN);
            move.setGroup(formation.getGroupId());
        });

        return moves;
    }
}
