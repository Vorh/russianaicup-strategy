package control;

import model.ActionType;
import model.Move;
import model.VehicleType;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Mv extends Com {

    private double startX;
    private double startY;
    private VehicleType type;

    private double targetX;
    private double targetY;

    public Mv(double x, double y, VehicleType type, Info oldInfo, Info newInfo) {
        super(oldInfo, newInfo);

        this.type = type;

        startX = newInfo.getX(type);
        startY = newInfo.getY(type);

        targetX = x+startX;
        targetY = y+startY;

        move = move -> {
            move.setAction(ActionType.MOVE);
            move.setVehicleType(type);
            move.setX(x);
            move.setY(y);
        };

    }

    @Override
    public boolean isComplete() {
        return newInfo.getDistanceTo(targetX, targetY, type) < 25;
    }

    @Override
    public Com nextCommand() {
        return super.nextCommand();
    }

    @Override
    public Consumer<Move> getMove() {
        return super.getMove();
    }
}
