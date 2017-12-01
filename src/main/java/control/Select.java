package control;

import model.ActionType;
import model.Move;
import model.Vehicle;
import model_custom.Formation;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Select extends Command {


    private double top;
    private double right;
    private double bottom;
    private double left;
    private final Formation formation;
    private Vehicle[] vehicles;

    public Select(double top, double right, double bottom, double left, Info oldInfo, Info newInfo, Formation formation) {
        super(oldInfo, newInfo);
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.formation = formation;
        this.vehicles = formation.getVehicles();
        this.type = formation.getVehicleType();


    }

    public Command move(double x , double y ){
        Mv mv = new Mv(x, y,oldInfo,newInfo,formation);
        next = mv;
        return mv;
    }


    public Command scale(double factor){
        Scale scale = new Scale(factor,oldInfo,newInfo,vehicles);
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
        };
        return move;
    }
}
