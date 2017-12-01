package control;

import model.*;
import model_custom.Formation;
import model_custom.Info;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Mv extends Command {


    private double x;
    private double y;
    private final Vehicle[] vehicles;
    private final Formation formation;

    private double targetX;
    private double targetY;

    public Mv(double x, double y, Info oldInfo, Info newInfo, Formation formation) {
        super(oldInfo, newInfo);
        this.x = x;
        this.y = y;
        this.vehicles = formation.getVehicles();
        this.formation = formation;
        this.type = formation.getVehicleType();
    }

    @Override
    public boolean isComplete() {
        return newInfo.getDistanceTo(targetX, targetY, type) < 25;
    }

    @Override
    public Command nextCommand() {
        return super.nextCommand();
    }

    @Override
    public Consumer<Move> getMove() {

        double startX = Arrays.stream(vehicles).mapToDouble(Unit::getX).average().orElse(0);
        double startY = Arrays.stream(vehicles).mapToDouble(Unit::getY).average().orElse(0);

        targetX = x + startX;
        targetY = y + startY;

        move = move -> {
            move.setAction(ActionType.MOVE);
            move.setX(x);
            move.setY(y);
            move.setGroup(formation.getGroupId());
        };
        return move;
    }
}
