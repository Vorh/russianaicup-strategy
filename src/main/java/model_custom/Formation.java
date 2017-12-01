package model_custom;

import model.Vehicle;
import model.VehicleType;

/**
 * Created by vorh on 12/1/17.
 */
public class Formation {



    private Vehicle[] vehicles;
    private Type type;
    private int groupId;
    private VehicleType vehicleType;


    public Formation(Vehicle[] vehicles, Type type, int groupId, VehicleType vehicleType) {
        this.vehicles = vehicles;
        this.type = type;
        this.groupId = groupId;
        this.vehicleType = vehicleType;
    }

    public enum Type {
        CAURUS,
        EUROBOREUS,
        MERIDIEM,
        MERIDIANAM,

    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Vehicle[] getVehicles() {
        return vehicles;
    }

    public Type getType() {
        return type;
    }

    public int getGroupId() {
        return groupId;
    }
}
