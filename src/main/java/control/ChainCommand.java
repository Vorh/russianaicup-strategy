package control;

import model.Move;
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
    private Consumer<Move> currentMove;

    public ChainCommand(Info oldInfo, Info newInfo, Queue<Consumer<Move>> chainQueue) {
        this.oldInfo = oldInfo;
        this.newInfo = newInfo;
        this.chainQueue = chainQueue;
    }


    public Command createCommand(){
        command = new Start(oldInfo, newInfo);
        return command;
    }

    
    public boolean execute(){

        if (currentMove == null){
            currentMove = command.getMove();
            if (currentMove !=null) chainQueue.add(currentMove);
        }

        if (command.isComplete()){
            currentMove=null;
            command = command.nextCommand();
            return command == null;
        }else {
            return false;
        }
    }

}
