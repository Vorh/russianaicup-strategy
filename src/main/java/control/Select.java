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


    private double x;
    private double y;

    public Select(double x, double y, VehicleType type, Info oldInfo, Info newInfo) {
        super(oldInfo, newInfo);
        this.x = x;
        this.y = y;
        this.type = type;

    }

    @Override
    public Consumer<Move> getMove() {
        move = move -> {
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setRight(x);
            move.setBottom(y);
            move.setVehicleType(type);
        };
        return move;
    }
}
