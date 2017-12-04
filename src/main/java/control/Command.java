package control;

import model.*;
import model_custom.Formation;
import model_custom.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by vorh on 11/29/17.
 */
public class Command {

    protected static Info oldInfo;
    protected static Info newInfo;

    protected Command next;
    protected List<Consumer<Move>> moves= new ArrayList<>();
    protected Formation formation;
    protected static CommandCenter commandCenter;


    public Command(Formation formation) {
        this.formation = formation;
    }


    public Formation createFormation(VehicleType type,Formation.Type formationType){

        select(type, formationType);

        return formation;
    }


    public CreateGroup select(VehicleType type, Formation.Type formationType) {

        double bottom = newInfo.getBottom(type);
        double left = newInfo.getLeft(type);
        double top = newInfo.getTop(type);
        double right = newInfo.getRight(type);


        switch (formationType) {
            case CAURUS:
                bottom = bottom - 27;
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
                right = right - 27;
                top = top + 27;
                break;
            case VERT_BOT:
                top = top + 27;
                break;
            case VERT_TOP:
                bottom = bottom -27;
                break;
            case HOR_LEFT:
                right = right - 27;
                break;
            case HOR_RIGHT:
                left = left + 27;
                break;
        }


        return select(top, right, bottom, left, type, formationType,commandCenter.getNextId());
    }

    public CreateGroup select(double top,
                              double right,
                              double bottom,
                              double left,
                              VehicleType type,
                              Formation.Type formationType,
                              int groupId
    ) {

        Vehicle[] vehicles = newInfo.streamVehicles(Info.Ownership.ALLY, type)
                .filter(vehicle -> {
                    return vehicle.getX() >= left &&
                            vehicle.getX() <= right &&
                            vehicle.getY() >= top &&
                            vehicle.getY() <= bottom;
                })
                .toArray(Vehicle[]::new);


        formation = new Formation(vehicles, formationType, groupId, type);
        CreateGroup createGroup = new CreateGroup(top, right, bottom, left,formation);
        next = createGroup;
        return createGroup;
    }
    public Command move(double x , double y){
        Mv mv = new Mv(x, y,formation,Mv.Type.MAP);
        next = mv;
        return mv;
    }

    public Command moveRelatively(double x , double y){
        Mv mv = new Mv(x, y,formation,Mv.Type.RELATIVE);
        next = mv;
        return mv;
    }

    public Command scale(double factor){
        Scale scale = new Scale(factor,formation);
        next = scale;
        return scale;
    }


    public Consumer<Move> getSelectMove(){
        return move -> {
          move.setAction(ActionType.CLEAR_AND_SELECT);
          move.setGroup(formation.getGroupId());
        };
    }


    public boolean isComplete() {
        return true;
    }

    public Command nextCommand() {
        return next;
    }

    public List<Consumer<Move>> getMoves(){
        return moves;
    }



    public static void setCommandCenter(CommandCenter commandCenter) {
        Command.commandCenter = commandCenter;
    }

    public static void setOldInfo(Info oldInfo) {
        Command.oldInfo = oldInfo;
    }

    public static void setNewInfo(Info newInfo) {
        Command.newInfo = newInfo;
    }
}
