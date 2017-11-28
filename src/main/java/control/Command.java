package control;

import model.ActionType;
import model.Move;
import model.VehicleType;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/23/17.
 */
public class Command {


    private Command nextCommand;
    private Condition condition;
    private Consumer<Move> move;
    private Info oldInfo;
    private Info newInfo;
    private int sleep;

    public Command(Info oldInfo, Info newInfo) {
        this.oldInfo = oldInfo;
        this.newInfo = newInfo;
        sleep = 0;
    }

    public Command scale(double factor){
        move = move -> {
            move.setAction(ActionType.SCALE);
            move.setFactor(factor);
        };

        return createCommand();
    }


    public Command then(Condition condition){
        this.condition = condition;
        return createCommand();
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



    public Command select(double x , double y,VehicleType type){
        move =move ->{
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setRight(x);
            move.setBottom(y);
            move.setVehicleType(type);
        };

        return createCommand();
    }

    public Command select(VehicleType type){
        return select(newInfo.getWorld().getWidth(),newInfo.getWorld().getHeight(),type);
    }

    public Command select(double x , double y){
        return select(x,y,null);
    }

    private Command createCommand(){
        nextCommand = new Command(oldInfo,newInfo);
        return nextCommand;
    }

    public Command nextCommand(){
        return nextCommand;
    }

    public boolean condition(){

        if (sleep != 0){
            System.out.println("SLEEP " + sleep);
            sleep--;
            return false;
        }

        if (condition == null){
            return true;
        }else {
            return condition.check(oldInfo,newInfo);
        }
    }

    public Consumer<Move> getMove() {
        return move;
    }

    public Command sleep(int countTick) {
        sleep = countTick;
        return createCommand();
    }
}
