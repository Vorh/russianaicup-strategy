package control;

import model.Move;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/23/17.
 */
public class ChainCommand {


    private Info oldInfo;
    private Info newInfo;

    private Command command;
    private Command previousCommand;

    private boolean isComplete;

    public ChainCommand(Info oldInfo, Info newInfo) {
        this.oldInfo = oldInfo;
        this.newInfo = newInfo;
        isComplete = false;
    }


    public Command createCommand(){
        command = new Start(oldInfo,newInfo);
        return command;
    }


    public boolean isNext(){
        if (previousCommand != null){
            return previousCommand.isComplete();
        }else {
            return true;
        }
    }

    public boolean isComplete(){
        return isComplete;
    }

    
    public Consumer<Move> execute(){
        if (previousCommand == null || previousCommand.isComplete()){
            previousCommand = command;
            command = previousCommand.nextCommand();

            if (command == null){
                isComplete = true;
                return null;
            }

            Consumer<Move> move = command.getMove();
            if (move == null) return execute();
            return move;

        }else {
            return null;
        }

    }

}
