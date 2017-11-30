package control;

import model.Move;
import model.VehicleType;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public abstract class Command {


    protected Info oldInfo;
    protected Info newInfo;
    protected VehicleType type;

    protected Command next;
    protected Consumer<Move> move;

    public Command(Info oldInfo, Info newInfo) {
        this.oldInfo = oldInfo;
        this.newInfo = newInfo;
    }

    public Command select(double x , double y, VehicleType type){
        Select select = new Select(x, y, type,oldInfo,newInfo);
        next = select;
        this.type = type;
        return select;
    }

    public Command move(double x , double y ){
        Mv mv = new Mv(x, y,type,oldInfo,newInfo);
        next = mv;
        return mv;
    }


    public Command scale(double factor){
        Scale scale = new Scale(factor,oldInfo,newInfo);
        next = scale;
        return scale;
    }

    public boolean isComplete() {
        return true;
    }

    public Command nextCommand() {
        return next;
    }

    public abstract Consumer<Move> getMove();

}
