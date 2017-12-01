package control;

import model.Move;
import model.Unit;
import model.VehicleType;
import model_custom.Type;
import model_custom.Info;

import java.util.Comparator;
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


    public Command select(VehicleType type, Type formation){
        double bottom = newInfo.streamVehicles(Info.Ownership.ALLY, type).max(Comparator.comparingDouble(Unit::getY)).get().getY();

        double left = newInfo.streamVehicles(Info.Ownership.ALLY, type).min(Comparator.comparingDouble(Unit::getX)).get().getX();

        double top = newInfo.streamVehicles(Info.Ownership.ALLY, type).min(Comparator.comparingDouble(Unit::getY)).get().getY();

        double right = newInfo.streamVehicles(Info.Ownership.ALLY, type).max(Comparator.comparingDouble(Unit::getX)).get().getX();


        switch (formation){
            case CAURUS:
                bottom = bottom-27;
                right = right - 27;
                break;
            case EUROBOREUS:
                left = left + 27;
                bottom = bottom + 27;
                break;
            case MERIDIEM:
                left = left + 27;
                top = top - 27;
                break;
            case MERIDIANAM:
                right = right -27;
                top = top - 27;
                break;
        }

        return select(top,right,bottom,left,type);
    }

    public Command select(VehicleType type){
        double bottom = newInfo.streamVehicles(Info.Ownership.ALLY, type).max(Comparator.comparingDouble(Unit::getY)).get().getY();

        double left = newInfo.streamVehicles(Info.Ownership.ALLY, type).min(Comparator.comparingDouble(Unit::getX)).get().getX();

        double top = newInfo.streamVehicles(Info.Ownership.ALLY, type).min(Comparator.comparingDouble(Unit::getY)).get().getY();

        double right = newInfo.streamVehicles(Info.Ownership.ALLY, type).max(Comparator.comparingDouble(Unit::getX)).get().getX();

        return select(top,right,bottom,left,type);
    }

    public Command select(double top , double right,double bottom , double left, VehicleType type){
        Select select = new Select(top, right,bottom,left, type,oldInfo,newInfo);
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
