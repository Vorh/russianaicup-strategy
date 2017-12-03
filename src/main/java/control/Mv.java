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
    private final Type typeCommand;
    private final Vehicle[] vehicles;

    private double targetX;
    private double targetY;
    private double condition;

    public Mv(double x, double y, Formation formation,Type typeCommand) {

        this.x = x;
        this. y = y;
        this.typeCommand = typeCommand;
        this.formation = formation;
        this.vehicles = formation.getVehicles();
        this.type = formation.getVehicleType();
        condition =vehicles.length*2;



        System.out.println("Mv x " + x + " Y " + y + " Group " + formation.getGroupId());
    }

    @Override
    public boolean isComplete() {
        boolean isComplete = newInfo.getDistanceTo(x, y, vehicles) < condition;
//        System.out.println("Is complete " + isComplete + " group id " + formation.getGroupId());
        return isComplete;
    }

    @Override
    public Command nextCommand() {
        return super.nextCommand();
    }

    @Override
    public Consumer<Move> getMove() {

        if (typeCommand == Type.MAP){
            double startX = Arrays.stream(vehicles).mapToDouble(Unit::getX).average().orElse(0);
            double startY = Arrays.stream(vehicles).mapToDouble(Unit::getY).average().orElse(0);
            x = x - startX;
            y = y - startY;
        }

        move = move -> {
            move.setAction(ActionType.MOVE);
            move.setX(this.x);
            move.setY(this.y);
        };

        return move;
    }


    public enum Type {
        RELATIVE,
        MAP
    }
}
