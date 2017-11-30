package control;

import model.ActionType;
import model.Move;
import model.VehicleType;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Select extends Command {


    private double top;
    private double right;
    private double bottom;
    private double left;

    public Select(double top, double right,double bottom, double left,VehicleType type, Info oldInfo, Info newInfo) {
        super(oldInfo, newInfo);
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.type = type;

    }

    @Override
    public Consumer<Move> getMove() {
        move = move -> {
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setRight(right);
            move.setBottom(bottom);
            move.setLeft(left);
            move.setTop(top);
            move.setVehicleType(type);
        };
        return move;
    }
}
