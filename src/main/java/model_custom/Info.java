package model_custom;

import model.*;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by vorh on 11/23/17.
 */
public class Info {

    private Player me;
    public World world;
    private Game game;
    private Move moveMain;
    private Map<Long, Vehicle> vehicleById;


    private Player oldMe;
    private World oldworld;
    private Game oldGame;
    private Move oldMoveMain;
    private Map<Long, Vehicle> oldVehicleById;




    public void init(Game game, Player me, Move move, World world,Map<Long,Vehicle> vehicleById) {
        oldMe = me;
        oldworld = world;
        oldGame = game;
        oldMoveMain = moveMain;
        oldVehicleById= this.vehicleById;

        this.me = me;
        this.world =  world;
        this.game = game;
        this.moveMain = move;
        this.vehicleById = vehicleById;
    }

    public double getX(VehicleType type) {
        return streamVehicles(Ownership.ALLY, type).mapToDouble(Vehicle::getX).average().orElse(Double.NaN);
    }

    public double getY(VehicleType type) {
        return streamVehicles(Ownership.ALLY, type).mapToDouble(Vehicle::getY).average().orElse(Double.NaN);
    }


    public Stream<Vehicle> streamVehicles(Ownership ownership, VehicleType vehicleType) {
        Stream<Vehicle> stream = vehicleById.values().stream();

        switch (ownership) {
            case ALLY:
                stream = stream.filter(vehicle -> vehicle.getPlayerId() == me.getId());
                break;
            case ENEMY:
                stream = stream.filter(vehicle -> vehicle.getPlayerId() != me.getId());
                break;
            default:
        }

        if (vehicleType != null) {
            stream = stream.filter(vehicle -> vehicle.getType() == vehicleType);
        }

        return stream;
    }

    public Stream<Vehicle> streamVehicles(Ownership ownership) {
        return streamVehicles(ownership, null);
    }

    public Stream<Vehicle> streamVehicles() {
        return streamVehicles(Ownership.ANY);
    }

    public enum Ownership {
        ANY,

        ALLY,

        ENEMY
    }
}
