package control;

import model.ActionType;
import model.Move;
import model_custom.Info;

import java.util.Queue;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/23/17.
 */
public class Condition {


    private Queue<Consumer<Move>> delayedMoves;
    private Event event;


    public Condition(Queue<Consumer<Move>> delayedMoves, Event event) {
        this.delayedMoves = delayedMoves;
        this.event = event;
    }

    public Condition scale(double factor){
        delayedMoves.add(move->{
            move.setAction(ActionType.SCALE);
            move.setFactor(factor);
        });

        return this;
    }


    public interface Event{
        boolean check(Info oldInfo, Info newInfo);
    }

}
