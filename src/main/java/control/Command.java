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
    private VehicleType type;
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

        double futureX = x+targetX;
        double futureY = y+targetY;
        move = move->{
            move.setAction(ActionType.MOVE);
            move.setX(futureX);
            move.setY(futureY);
            System.out.println("MOVE " + futureX + " " + futureY);
        };

        Condition cont = (oldInfo1, newInfo1) -> {
            System.out.println(newInfo.getDistanceTo(futureX,futureY,type));
          return   newInfo.getDistanceTo(futureX,futureY,type) == 0;
        };

        return createCommand(cont);
    }



    public Command select(double x , double y,VehicleType type){
        move =move ->{
            move.setAction(ActionType.CLEAR_AND_SELECT);
            move.setRight(x);
            move.setBottom(y);
            move.setVehicleType(type);
        };

        this.type = type;

        return createCommand();
    }

    public Command select(VehicleType type){
        return select(newInfo.getWorld().getWidth(),newInfo.getWorld().getHeight(),type);
    }

    public Command select(double x , double y){
        return select(x,y,null);
    }

    private Command createCommand(Condition condition){
        Command command = createCommand();
        command.condition = condition;
        return command;
    }
    private Command createCommand(){
        nextCommand = new Command(oldInfo,newInfo);
        nextCommand.setType(type);
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

    public void setType(VehicleType type) {
        this.type = type;
    }
}
