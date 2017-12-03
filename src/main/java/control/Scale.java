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
    private int time = 0;

    public Scale(double factor, Formation formation) {
        this.factor = factor;
        this.formation = formation;

    }


    @Override
    public boolean isComplete() {
        if (time == 15){
            return true;
        }else {
            time++;
            return false;
        }
    }

    @Override
    public Consumer<Move> getMove() {

        System.out.println("Scale " + factor + " Group id " + formation.getGroupId());


        double left = newInfo.getLeft(formation.getVehicles());
        double top = newInfo.getTop(formation.getVehicles());

        double x = left + formation.getVehicles().length/2;
        double y = top + formation.getVehicles().length/2;

        move = move ->{
            move.setAction(ActionType.SCALE);
            move.setX(x);
            move.setY(y);
            move.setFactor(factor);
        };
        return move;
    }
}
