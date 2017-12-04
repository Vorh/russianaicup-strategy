package control;

import model.ActionType;
import model.Move;
import model.Unit;
import model.Vehicle;
import model_custom.Formation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Mv extends Command {


    private double x;
    private double y;
    private final Type typeCommand;
    private final Vehicle[] vehicles;

    private int condition;

    public Mv(double x, double y, Formation formation, Type typeCommand) {
        super(formation);
        this.x = x;
        this.y = y;
        this.typeCommand = typeCommand;
        this.formation = formation;
        this.vehicles = formation.getVehicles();

    }

    @Override
    public boolean isComplete() {

        if (!newInfo.isMove(vehicles)) {
            condition++;
        }

//        System.out.println("Is complete " + " group id " + formation.getGroupId());
        return condition > 10;
    }

    @Override
    public Command nextCommand() {
        return super.nextCommand();
    }

    @Override
    public List<Consumer<Move>> getMoves() {
        System.out.println("Mv x " + x + " Y " + y + " Group " + formation.getGroupId());

        double startX = Arrays.stream(vehicles).mapToDouble(Unit::getX).average().orElse(0);
        double startY = Arrays.stream(vehicles).mapToDouble(Unit::getY).average().orElse(0);
        if (typeCommand == Type.MAP) {
            x = x - startX;
            y = y - startY;

        }

        moves.add(getSelectMove());
        moves.add(move -> {
            move.setAction(ActionType.MOVE);
            move.setX(this.x);
            move.setY(this.y);
        });

        return moves;
    }


    public enum Type {
        RELATIVE,
        MAP
    }
}
