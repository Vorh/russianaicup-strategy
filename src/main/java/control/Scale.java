package control;

import model.ActionType;
import model.Move;
import model_custom.Formation;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/30/17.
 */
public class Scale extends Command {


    private double factor;
    private int condition = 0;

    public Scale(double factor, Formation formation) {
        this.factor = factor;
        this.formation = formation;

    }




    @Override
    public boolean isComplete() {
        if (!newInfo.isMove(formation.getVehicles())) {
            condition++;
        }

//        System.out.println("Is complete " + " group id " + formation.getGroupId());
        return condition > 10;
    }

    @Override
    public Consumer<Move> getMove() {

        System.out.println("Scale " + factor + " Group id " + formation.getGroupId());


        double x = newInfo.getX(formation.getGroupId());
        double y = newInfo.getY(formation.getGroupId());

        move = move ->{
            move.setAction(ActionType.SCALE);
            move.setX(x);
            move.setY(y);
            move.setFactor(factor);
        };
        return move;
    }
}
