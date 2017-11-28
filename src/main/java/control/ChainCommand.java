package control;

import model.*;
import model_custom.Info;

import java.util.Queue;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/23/17.
 */
public class ChainCommand {


    private Info oldInfo;
    private Info newInfo;
    public Queue<Consumer<Move>> chainQueue;

    private Command command;

    public ChainCommand(Info oldInfo, Info newInfo, Queue<Consumer<Move>> chainQueue) {
        this.oldInfo = oldInfo;
        this.newInfo = newInfo;
        this.chainQueue = chainQueue;
    }


    public Command createCommand(){
        command = new Command(oldInfo, newInfo);
        return command;
    }

    
    public boolean execute(){

        if (command.condition()){

            Consumer<Move> move = command.getMove();
            if (move != null){
                chainQueue.add(move);
            }

            command = command.nextCommand();
            return command == null;
        }else {
            return false;
        }
    }

}
