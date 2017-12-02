package control;

import model.Facility;
import model.Move;
import model.Vehicle;
import model.VehicleType;
import model_custom.Formation;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public abstract class Command {


    protected static Info oldInfo;
    protected static Info newInfo;
    protected VehicleType type;

    protected Command next;
    protected Consumer<Move> move;


    public static void setOldInfo(Info oldInfo) {
        Command.oldInfo = oldInfo;
    }

    public static void setNewInfo(Info newInfo) {
        Command.newInfo = newInfo;
    }

    public Select select(VehicleType type, Formation.Type formationType){

        double bottom = newInfo.getBottom(type);
        double left = newInfo.getLeft(type);
        double top = newInfo.getTop(type);
        double right = newInfo.getRight(type);


        switch (formationType){
            case CAURUS:
                bottom = bottom-27;
                right = right - 27;
                break;
            case EUROBOREUS:
                left = left + 27;
                bottom = bottom - 27;
                break;
            case MERIDIEM:
                left = left + 27;
                top = top + 27;
                break;
            case MERIDIANAM:
                right = right -27;
                top = top + 27;
                break;
        }


        return select(top,right,bottom,left,type,formationType);
    }

    public Select select(double top ,
                         double right,
                         double bottom,
                         double left,
                         VehicleType type,
                         Formation.Type formationType
    ){

         Vehicle[] vehicles = newInfo.streamVehicles(Info.Ownership.ALLY, type)
                .filter(vehicle -> {
                 return vehicle.getX()>= left &&
                        vehicle.getX()<= right &&
                        vehicle.getY()>= top &&
                        vehicle.getY()<= bottom;
                })
                .toArray(Vehicle[]::new);

        System.out.println("Select " + vehicles.length + " Type " + type.name());

        Formation formation = new Formation(vehicles,formationType,0,type);
        Select select = new Select(top, right,bottom,left,formation );
        next = select;
        this.type = type;
        return select;
    }
    public Command move(double x , double y ,Formation formation){
        Mv mv = new Mv(x, y,formation);
        next = mv;
        return mv;
    }



    public boolean isComplete() {
        return true;
    }

    public Command nextCommand() {
        return next;
    }

    public abstract Consumer<Move> getMove();

    public Command capture(Facility facility){


    }
}
