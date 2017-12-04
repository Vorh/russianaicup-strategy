package control;

import model.VehicleType;
import model_custom.Formation;
import model_custom.Info;
import model_custom.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vorh on 12/3/17.
 */
public class CommandCenter {

    private final Info oldInfo;
    private final Info newInfo;
    List<Formation> formations;
    private int groupIdCounter = 1;

    private VehicleType startPositions[][] = new VehicleType[3][3];
    private VehicleType centerStartPosition;

    public CommandCenter(Info oldInfo, Info newInfo) {
        this.oldInfo = oldInfo;
        this.newInfo = newInfo;
        formations = new ArrayList<>();


        for (VehicleType vehicleType : VehicleType.values()) {
            identifyPosition(vehicleType);
        }

        displayStartPositions();
    }

    private void displayStartPositions() {
        for (int i = 0; i < startPositions.length; i++) {
            System.out.println();
            for (int j = 0; j < startPositions[i].length; j++) {

                VehicleType vehicleType = startPositions[j][i];
                if (vehicleType == null) {
                    System.out.print(" |  NULL | ");
                } else {
                    System.out.print(" | " + vehicleType.name() + " | ");
                }
            }
        }
        System.out.println();
    }

    private void identifyPosition(VehicleType vehicleType) {

        double left = newInfo.getLeft(vehicleType);
        double top = newInfo.getTop(vehicleType);


        if (left == 166.0) {
            if (top == 18.0) {
                startPositions[2][0] = vehicleType;
            } else if (top == 92.0) {
                startPositions[2][1] = vehicleType;
            } else {
                startPositions[2][2] = vehicleType;
            }
        } else if (left == 92.0) {
            if (top == 18.0) {
                startPositions[1][0] = vehicleType;
            } else if (top == 92.0) {
                startPositions[1][1] = vehicleType;
            } else {
                startPositions[1][2] = vehicleType;
            }
        } else {
            if (top == 18.0) {
                startPositions[0][0] = vehicleType;
            } else if (top == 92.0) {
                startPositions[0][1] = vehicleType;
            } else {
                startPositions[0][2] = vehicleType;
            }
        }

    }

    public Pair<Integer, Integer> getStartPosition(VehicleType type) {

        for (int i = 0; i < startPositions.length; i++) {
            for (int j = 0; j < startPositions[i].length; j++) {
                if (startPositions[i][j] == type) {
                    return new Pair<>(j, i);
                }
            }
        }

        return null;

    }

    public boolean pathFreeAir(Direction direction, VehicleType type) {

        Pair<Integer, Integer> position = getStartPosition(type);
        boolean isClear = true;

        switch (direction) {
            case SOUTH:
                for (int i= position.value; i < startPositions.length; i++) {
                    isClear = checkPath(type,startPositions[position.value][i]);
                }
                break;
            case EAST:
                for (int i = position.key; i < startPositions[position.key].length; i++) {
                    isClear = checkPath(type,startPositions[position.key][i]);
                }
                break;
        }


        return isClear;
    }

    private boolean checkPath(VehicleType type, VehicleType tempType){
        if (type == VehicleType.HELICOPTER && tempType == VehicleType.FIGHTER) {
            return false;
        } else if (type == VehicleType.FIGHTER && tempType == VehicleType.HELICOPTER) {
            return false;
        }else {
            return true;
        }
    }

    public boolean isFreeStartPosition(int x , int y) {
        return startPositions[x][y] == null;
    }

    public List<VehicleType> getRemotenessArmy() {
        List<VehicleType> army = new ArrayList<>();

        for (int i = startPositions.length-1; i > -1; i--) {
            for (int j = startPositions[i].length-1; j > -1; j--) {
                VehicleType type = startPositions[i][j];

                if (type != null &&
                    type != VehicleType.FIGHTER &&
                    type != VehicleType.HELICOPTER){

                    army.add(type);
                }
            }
        }
        return army;
    }

    public VehicleType[][] getStartPositions() {
        return startPositions;
    }

    public enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    public void add(Formation formation) {
        formations.add(formation);
    }

    public int getNextId() {
        return groupIdCounter++;
    }


}
