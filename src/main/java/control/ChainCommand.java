package control;

import model.Move;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/23/17.
 */
public class ChainCommand {



    private Command command;
    private Command previousCommand;

    private boolean isComplete;

    public ChainCommand() {
        isComplete = false;
    }


    public Command createCommand(){
        command = new Start();
        return command;
    }


    public boolean isNext(){
        if (previousCommand != null && command != null){
            return previousCommand.isComplete() && command.isComplete();
        }else {
            return true;
        }
    }

    public String getInfo(){
        return command.info();
    }


    public boolean isComplete(){
        return isComplete && previousCommand.isComplete();
    }

    public boolean isRepeat(){
        return true;
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
