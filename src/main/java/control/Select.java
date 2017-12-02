package control;

import model.ActionType;
import model.Move;
import model.Vehicle;
import model_custom.Formation;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Select extends Command {


    private double top;
    private double right;
    private double bottom;
    private double left;
    private Formation formation;
    private Vehicle[] vehicles;

    public Select(double top, double right, double bottom, double left,  Formation formation) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.formation = formation;
        this.vehicles = formation.getVehicles();
        this.type = formation.getVehicleType();


    }

    public Select(Formation formation){

    }

    public Command scale(double factor){
        Scale scale = new Scale(factor,vehicles);
        next = scale;
        return scale;
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
//            move.setGroup(formation.getGroupId());
        };
        return move;
    }
}
