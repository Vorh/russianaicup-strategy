package control;

import model.*;
import model_custom.Info;

import java.util.Queue;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/23/17.
 */
public class ChainCommand {


    private Info info;
    public Queue<Consumer<Move>> chainQueue;

    private Command command;

    public ChainCommand(Info info,Queue<Consumer<Move>> chainQueue ) {
        this.info = info;
        this.chainQueue = chainQueue;
    }


    
    public boolean execute(){

        if (command.condition()){
            chainQueue.add(command.getMove());
            command = command.nextCommand();
            return command == null;
        }else {
            return false;
        }
    }

}
