package control;

import model.ActionType;
import model.Move;
import model.VehicleType;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/23/17.
 */
public class Command {


    private Command nextCommand;
    private Condition condition;
    private Consumer<Move> move;


    public Command scale(double factor){
        move = move -> {
            move.setAction(ActionType.SCALE);
            move.setFactor(factor);
        };

        return createCommand();
    }


    public Command then(Condition condition){
        this.condition = condition;

    }

    public Command move(double x , double targetX, double y, double targetY){
        move = move->{
            move.setAction(ActionType.MOVE);
            move.setX(x+targetX);
            move.setX(y+targetY);
            System.out.println("MOVE " + targetX + " " + targetY);
        };

        return createCommand();
    }



    public Command select(VehicleType vehicleType){
        move =move ->{
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setRight(info.world.getWidth());
            move.setBottom(info.world.getHeight());
            move.setVehicleType(VehicleType.FIGHTER);
            System.out.println("SELECT " + vehicleType.name());
        };

        return createCommand();
    }

    private Command createCommand(){
        nextCommand = new Command();
        return nextCommand;
    }

    public Command nextCommand(){
        return nextCommand;
    }

    public boolean condition(){
        return condition.check();
    }

    public Consumer<Move> getMove() {
        return move;
    }
}
