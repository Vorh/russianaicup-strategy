package model_custom;

import model.Vehicle;

/**
 * Created by vorh on 12/1/17.
 */
public class Formation {



    private Vehicle[] vehicles;
    private Type type;


    public Formation(Vehicle[] vehicles, Type type) {
        this.vehicles = vehicles;
        this.type = type;
    }

    public enum Type {
        CAURUS,
        EUROBOREUS,
        MERIDIEM,
        MERIDIANAM,

    }
}
