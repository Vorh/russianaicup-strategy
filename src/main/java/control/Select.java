package control;

import model.ActionType;
import model.Move;
import model_custom.Formation;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Select extends Command {


    private double top;
    private double right;
    private double bottom;
    private double left;

    public Select(Formation formation){
        this.formation = formation;

        move = move ->{
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setGroup(formation.getGroupId());
        };

        System.out.println("Select group " + formation.getGroupId() + " length " + formation.getVehicles().length);
    }

    public Select(double top, double right, double bottom, double left, Formation formation) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.formation = formation;
        this.type = formation.getVehicleType();


        move = move -> {
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setRight(right);
            move.setBottom(bottom);
            move.setLeft(left);
            move.setTop(top);
            move.setVehicleType(type);
        };

        System.out.println("Select " + formation.getVehicles().length + " Type " + type.name() + " formation type " + formation.getType());

    }



    @Override
    public Consumer<Move> getMove() {
        return move;
    }
}
