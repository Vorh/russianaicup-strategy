package control;

import model.ActionType;
import model.Move;
import model.Vehicle;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/30/17.
 */
public class Scale extends Command {


    private double factor;

    public Scale(double factor,  Vehicle[] vehicles) {
        this.factor = factor;
    }

    @Override
    public Consumer<Move> getMove() {

        double x = newInfo.getX(type);
        double y = newInfo.getY(type);


        move = move ->{
            move.setAction(ActionType.SCALE);
            move.setX(x);
            move.setY(y);
            move.setFactor(factor);
            move.setVehicleType(type);
        };

        return move;
    }
}
