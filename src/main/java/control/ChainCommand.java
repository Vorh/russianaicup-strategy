package control;

import model.*;
import model_custom.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/23/17.
 */
public class ChainCommand {


    private Info info;
    public Queue<Consumer<Move>> chainQueue;
    public List<Command> conditions = new ArrayList<>();

    public ChainCommand(Info info) {
        this.info = info;
    }


    public Condition then(Condition.Event event){
        Condition condition =new Condition(delayedMoves,event);
        conditions.add(condition);
        return condition;
    };

    public ChainCommand move(double x , double targetX, double y, double targetY){
        chainQueue.add(move->{
            move.setAction(ActionType.MOVE);
            move.setX(x+targetX);
            move.setX(y+targetY);
            System.out.println("MOVE " + targetX + " " + targetY);
        });

        return this;
    }



    public ChainCommand select(VehicleType vehicleType){
        chainQueue.add(move->{
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setRight(info.world.getWidth());
            move.setBottom(info.world.getHeight());
            move.setVehicleType(VehicleType.FIGHTER);
            System.out.println("SELECT " + vehicleType.name());
        });
        
        return this;
    }
    
    public boolean execute(){
        for (Condition condition : conditions) {

        }

        conditions.clear();

        return true;
    }


    public void setDelayedMoves(Queue<Consumer<Move>> delayedMoves) {
        this.delayedMoves = delayedMoves;
    }
}
