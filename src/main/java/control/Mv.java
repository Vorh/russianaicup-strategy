package control;

import model.ActionType;
import model.Move;
import model.Unit;
import model.Vehicle;
import model_custom.Formation;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Mv extends Command {


    private double x;
    private double y;
    private final Vehicle[] vehicles;

    private double targetX;
    private double targetY;
    private double condition;

    public Mv(double x, double y, Formation formation) {
        this.x = x;
        this.y = y;
        this.formation = formation;
        this.vehicles = formation.getVehicles();
        this.type = formation.getVehicleType();
        condition =vehicles.length/2;
    }

    @Override
    public boolean isComplete() {
        boolean isComplete = newInfo.getDistanceTo(x, y, vehicles) < 0;
        System.out.println("Is complete " + isComplete + " group id " + formation.getGroupId());
        return isComplete;
    }

    @Override
    public Command nextCommand() {
        return super.nextCommand();
    }

    @Override
    public Consumer<Move> getMove() {

        double startX = Arrays.stream(vehicles).mapToDouble(Unit::getX).average().orElse(0);
        double startY = Arrays.stream(vehicles).mapToDouble(Unit::getY).average().orElse(0);

//        targetX = x + startX;
//        targetY = y + startY;
        x = x - startX;
        y = y - startY;

        move = move -> {
            move.setAction(ActionType.MOVE);
            move.setX(x);
            move.setY(y);
        };
        return move;
    }
}
