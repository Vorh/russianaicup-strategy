package control;

import model.Move;
import model.VehicleType;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public abstract class Com {


    protected Info oldInfo;
    protected Info newInfo;

    protected Com next;
    protected Consumer<Move> move;
    private boolean isReturnMove;

    public Com(Info oldInfo, Info newInfo) {
        this.oldInfo = oldInfo;
        this.newInfo = newInfo;
    }

    public Com select(double x , double y, VehicleType type){
        Select select = new Select(x, y, type,oldInfo,newInfo);
        next = select;
        return select;
    }

    public Com move(double x , double y , VehicleType type){
        Mv mv = new Mv(x, y, type,oldInfo,newInfo);
        next = mv;
        return mv;
    }

    public boolean isComplete() {
        return true;
    }

    public Com nextCommand() {
        return next;
    }

    public Consumer<Move> getMove() {
        if (isReturnMove){
            return null;
        }else {
            isReturnMove =true;
            return move;
        }
    }
}
