package control;

import model.ActionType;
import model.VehicleType;
import model_custom.Info;

/**
 * Created by vorh on 11/29/17.
 */
public class Select extends Com{


    private double x;
    private double y;
    private VehicleType type;

    public Select(double x, double y, VehicleType type, Info oldInfo, Info newInfo) {
        super(oldInfo, newInfo);
        this.x = x;
        this.y = y;
        this.type = type;

        move = move -> {
          move.setAction(ActionType.CLEAR_AND_SELECT);
          move.setRight(x);
          move.setBottom(y);
          move.setVehicleType(type);
        };
    }

}
