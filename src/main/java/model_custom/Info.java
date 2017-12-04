package model_custom;

import model.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by vorh on 11/23/17.
 */
public class Info {

    private Player me;
    private World world;
    private Game game;
    private Move moveMain;
    private Map<Long, Vehicle> vehicleById;
    private Map<Long, Integer> updateTickByVehicleId;

    public void init(Game game, Player me, Move move, World world, Map<Long, Vehicle> vehicleById, Map<Long, Integer> updateTickByVehicleId) {
        this.me = me;
        this.world =  world;
        this.game = game;
        this.moveMain = move;
        this.vehicleById = vehicleById;
        this.updateTickByVehicleId = updateTickByVehicleId;
    }

    public double getX(int groupId){
        return streamVehicles(groupId).mapToDouble(Unit::getX).average().orElse(Double.NaN);
    }

    public double getY(int groupId){
        return streamVehicles(groupId).mapToDouble(Vehicle::getY).average().orElse(Double.NaN);
    }

    public double getX(VehicleType type) {
        return streamVehicles(Ownership.ALLY, type).mapToDouble(Unit::getX).average().orElse(Double.NaN);
    }

    public double getY(VehicleType type) {
        return streamVehicles(Ownership.ALLY, type).mapToDouble(Vehicle::getY).average().orElse(Double.NaN);
    }

    public boolean isMove(Vehicle[] vehicles){
        for (Vehicle vehicle : vehicles) {
            if (updateTickByVehicleId.containsKey(vehicle.getId())) {
                return true;
            }
        }
        return false;
    }

    public double getDistanceTo(double x , double y,VehicleType type){
        return streamVehicles(Ownership.ALLY,type).mapToDouble(vehicle -> vehicle.getDistanceTo(x, y)).average().orElse(Double.NaN);
    }

    public double getDistanceTo(double x , double y,Vehicle[] vehicles){
        return Arrays.stream(vehicles).mapToDouble(vehicle -> vehicle.getDistanceTo(x, y)).average().orElse(Double.NaN);
    }

    public Player getMe() {
        return me;
    }

    public World getWorld() {
        return world;
    }

    public Game getGame() {
        return game;
    }

    public Move getMoveMain() {
        return moveMain;
    }


    public Stream<Vehicle> streamVehicles(int groupId){
        Stream<Vehicle> stream = vehicleById.values().stream();

        return stream.filter(vehicle -> {
            for (int i : vehicle.getGroups()) {
                if (i==groupId){
                    return true;
                }
            }
            return false;
        });
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


    public double getBottom(VehicleType type){
        return streamVehicles(Info.Ownership.ALLY, type).max(Comparator.comparingDouble(Unit::getY)).get().getY();
    }

    public double getBottom(Vehicle[] vehicles){
        return Arrays.stream(vehicles).max(Comparator.comparingDouble(Unit::getY)).get().getY();
    }

    public double getLeft(VehicleType type) {
        return streamVehicles(Info.Ownership.ALLY, type).min(Comparator.comparingDouble(Unit::getX)).get().getX();
    }

    public double getLeft(Vehicle[] vehicles){
        return Arrays.stream(vehicles).min(Comparator.comparingDouble(Unit::getX)).get().getX();
    }

    public double getTop(VehicleType type) {
        return streamVehicles(Info.Ownership.ALLY, type).min(Comparator.comparingDouble(Unit::getY)).get().getY();
    }
    public double getTop(Vehicle[] vehicles){
        return Arrays.stream(vehicles).min(Comparator.comparingDouble(Unit::getY)).get().getY();
    }

    public double getRight(VehicleType type) {
        return streamVehicles(Info.Ownership.ALLY, type).max(Comparator.comparingDouble(Unit::getX)).get().getX();
    }

    public double getRight(Vehicle[] vehicles){
        return Arrays.stream(vehicles).max(Comparator.comparingDouble(Unit::getX)).get().getX();
    }



    public enum Ownership {
        ANY,

        ALLY,

        ENEMY
    }
}
