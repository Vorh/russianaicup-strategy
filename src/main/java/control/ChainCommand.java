package control;

import model.Move;
import model_custom.Formation;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/23/17.
 */
public class ChainCommand {



    private Command command;
    private Command previousCommand;

    private boolean isComplete;
    private boolean isAtomic;

    public ChainCommand() {
        isComplete = false;
    }


    public Command createCommand(Formation formation){
        command = new Command(formation);
        return command;
    }


    public boolean isNext(){
        if (previousCommand != null && command != null){
            return previousCommand.isComplete() && command.isComplete();
        }else {
            return true;
        }
    }




    public boolean isComplete(){
        return isComplete && previousCommand.isComplete();
    }


    public List<Consumer<Move>> execute(){

        if (previousCommand == null || previousCommand.isComplete()){

            previousCommand = command;


            command = previousCommand.nextCommand();

            if (command == null){
                isComplete = true;
                return null;
            }

            List<Consumer<Move>> moves = command.getMoves();
            if (moves == null) return execute();
            return moves;

        }else {
            return null;
        }

    }

}
