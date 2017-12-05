import control.ChainCommand;
import control.Command;
import control.CommandCenter;
import model.*;
import model_custom.Formation;
import model_custom.Info;

import java.util.*;
import java.util.function.Consumer;

public final class MyStrategy implements Strategy {


    private static final Map<VehicleType, VehicleType[]> preferredTargetTypesByVehicleType;

    static {
        preferredTargetTypesByVehicleType = new EnumMap<>(VehicleType.class);

        preferredTargetTypesByVehicleType.put(VehicleType.FIGHTER, new VehicleType[]{
                VehicleType.HELICOPTER, VehicleType.FIGHTER
        });

        preferredTargetTypesByVehicleType.put(VehicleType.HELICOPTER, new VehicleType[]{
                VehicleType.TANK, VehicleType.ARRV, VehicleType.HELICOPTER, VehicleType.IFV, VehicleType.FIGHTER
        });

        preferredTargetTypesByVehicleType.put(VehicleType.IFV, new VehicleType[]{
                VehicleType.HELICOPTER, VehicleType.ARRV, VehicleType.IFV, VehicleType.FIGHTER, VehicleType.TANK
        });

        preferredTargetTypesByVehicleType.put(VehicleType.TANK, new VehicleType[]{
                VehicleType.IFV, VehicleType.ARRV, VehicleType.TANK, VehicleType.FIGHTER, VehicleType.HELICOPTER
        });
    }


    public Random random;

    private TerrainType[][] terrainTypeByCellXY;
    private WeatherType[][] weatherTypeByCellXY;

    private Player me;
    private World world;
    private Game game;
    private Move move;
    private boolean isCreateFormation;

    private final Map<Long, Vehicle> vehicleById = new HashMap<>();
    private final Map<Long, Integer> updateTickByVehicleId = new HashMap<>();
    private final Queue<Consumer<Move>> delayedMoves = new ArrayDeque<>();
    private final Map<Integer, ChainCommand> commandMap = new HashMap<>();
    protected final Info oldInfo;
    protected final Info newInfo;
    private int ticks;
    protected CommandCenter commandCenter;
    protected int lenght;
    protected Formation center;
    protected Formation left;
    protected Formation right;
    protected boolean isLeft;
    protected boolean isCenter;
    protected boolean isRight;

    public MyStrategy() {
        oldInfo = new Info();
        newInfo = new Info();
        ticks = 20_000;


    }


    @Override
    public void move(Player me, World world, Game game, Move move) {
        initializeStrategy(world, game);
        initializeTick(me, world, game, move);


        if (!isCreateFormation) {
            createCommandCenter();
            isCreateFormation = true;
        }


        if (me.getRemainingActionCooldownTicks() > 0) {
            return;
        }

        if (executeDelayedMove()) {
            return;
        }

        move();

        executeDelayedMove();

        ticks--;
    }


    /**
     * Инциализируем стратегию.
     * <p>
     * Для этих целей обычно можно использовать конструктор, однако в данном случае мы хотим инициализировать генератор
     * случайных чисел значением, полученным от симулятора игры.
     */
    private void initializeStrategy(World world, Game game) {
        if (random == null) {
            random = new Random(game.getRandomSeed());

            terrainTypeByCellXY = world.getTerrainByCellXY();
            weatherTypeByCellXY = world.getWeatherByCellXY();
        }
    }

    /**
     * Сохраняем все входные данные в полях класса для упрощения доступа к ним, а также актуализируем сведения о каждой
     * технике и времени последнего изменения её состояния.
     */
    private void initializeTick(Player me, World world, Game game, Move move) {

        oldInfo.init(this.game, this.me, this.move, this.world, vehicleById, updateTickByVehicleId);
        Command.setOldInfo(oldInfo);

        this.me = me;
        this.world = world;
        this.game = game;
        this.move = move;


        updateTickByVehicleId.clear();
        for (Vehicle vehicle : world.getNewVehicles()) {
            vehicleById.put(vehicle.getId(), vehicle);
            updateTickByVehicleId.put(vehicle.getId(), world.getTickIndex());
        }

        for (VehicleUpdate vehicleUpdate : world.getVehicleUpdates()) {
            long vehicleId = vehicleUpdate.getId();

            if (vehicleUpdate.getDurability() == 0) {
                vehicleById.remove(vehicleId);
                updateTickByVehicleId.remove(vehicleId);
            } else {
                vehicleById.put(vehicleId, new Vehicle(vehicleById.get(vehicleId), vehicleUpdate));
                updateTickByVehicleId.put(vehicleId, world.getTickIndex());
            }
        }

        newInfo.init(game, me, move, world, vehicleById, updateTickByVehicleId);
        Command.setNewInfo(newInfo);
    }

    /**
     * Достаём отложенное действие из очереди и выполняем его.
     *
     * @return Возвращает {@code true}, если и только если отложенное действие было найдено и выполнено.
     */
    private boolean executeDelayedMove() {
        Consumer<Move> delayedMove = delayedMoves.poll();
        if (delayedMove == null) {
            return false;
        }

        delayedMove.accept(move);
        return true;
    }

//    private void setStartTargetHelicopter() {
//
//        Formation fighterMeridiem = command().select(VehicleType.HELICOPTER, Formation.Type.MERIDIEM)
//                .createFormation();
//        Formation fighterCaurus = command().select(VehicleType.HELICOPTER, Formation.Type.CAURUS)
//                .createFormation();
//        Formation fighterEuroboreus = command().select(VehicleType.HELICOPTER, Formation.Type.EUROBOREUS)
//                .createFormation();
//        Formation fighterMeridianam = command().select(VehicleType.HELICOPTER, Formation.Type.MERIDIANAM)
//                .createFormation();
//
//        commandCenter.add(fighterCaurus);
//        commandCenter.add(fighterEuroboreus);
//        commandCenter.add(fighterMeridianam);
//        commandCenter.add(fighterMeridiem);
//
//        command().select(fighterEuroboreus).scale(0.1);
//        command().select(fighterMeridiem).scale(0.1);
//        command().select(fighterMeridianam).scale(0.1);
//        command().select(fighterCaurus).scale(0.1);
//
//        int targetX = 0;
//        int targetY = 0;
//
//        Pair<Integer, Integer> position = commandCenter.getStartPosition(VehicleType.HELICOPTER);
//        if (commandCenter.pathFreeAir(CommandCenter.Direction.SOUTH, VehicleType.HELICOPTER)) {
//            targetY = 200 - (position.key * 50);
//        } else {
//            targetX = 200 - (position.value * 50);
//        }
//        command().select(fighterMeridianam).moveRelatively(targetX, targetY);
//        command().select(fighterEuroboreus).moveRelatively(targetX, targetY);
//        command().select(fighterMeridiem).moveRelatively(targetX, targetY);
//        command().select(fighterCaurus).moveRelatively(targetX, targetY);
//    }
//
//    private void setStartTargetFighter() {
//
//        Formation fighterMeridiem = command().select(VehicleType.FIGHTER, Formation.Type.MERIDIEM)
//                .createFormation();
//        Formation fighterCaurus = command().select(VehicleType.FIGHTER, Formation.Type.CAURUS)
//                .createFormation();
//        Formation fighterEuroboreus = command().select(VehicleType.FIGHTER, Formation.Type.EUROBOREUS)
//                .createFormation();
//        Formation fighterMeridianam = command().select(VehicleType.FIGHTER, Formation.Type.MERIDIANAM)
//                .createFormation();
//
//        commandCenter.add(fighterCaurus);
//        commandCenter.add(fighterEuroboreus);
//        commandCenter.add(fighterMeridianam);
//        commandCenter.add(fighterMeridiem);
//
//        command().select(fighterEuroboreus).scale(0.1);
//        command().select(fighterMeridiem).scale(0.1);
//        command().select(fighterMeridianam).scale(0.1);
//        command().select(fighterCaurus).scale(0.1);
//
//        int targetX = 0;
//        int targetY = 0;
//
//        Pair<Integer, Integer> position = commandCenter.getStartPosition(VehicleType.FIGHTER);
//        if (commandCenter.pathFreeAir(CommandCenter.Direction.SOUTH, VehicleType.FIGHTER)) {
//            targetY = 200 - (position.key * 50);
//        } else {
//            targetX = 200 - (position.value * 50);
//        }
//        command().select(fighterMeridianam).moveRelatively(targetX, targetY);
//        command().select(fighterEuroboreus).moveRelatively(targetX, targetY);
//        command().select(fighterMeridiem).moveRelatively(targetX, targetY);
//        command().select(fighterCaurus).moveRelatively(targetX, targetY);
//    }


    private void createCommandCenter() {

        commandCenter = new CommandCenter(oldInfo, newInfo);
        Command.setCommandCenter(commandCenter);

//        setStartTargetHelicopter();
//        setStartTargetFighter();
        setStartTargetGroundForces();

    }


    public void setStartTargetGroundForces() {


        VehicleType[][] startPositions = commandCenter.getStartPositions();

        isLeft = false;
        isCenter = false;
        isRight = false;


        lenght = 74;


        secondRowLeft(startPositions);
        secondRowCenter(startPositions);
        secondRowRight(startPositions);

        firstRowCenter(startPositions);
        firstRowLeft(startPositions);
        firstRowRight(startPositions);



        thirdRowLeft(startPositions);
        thirdRowLeft(startPositions);
        thirdRowRight(startPositions);
    }

    private void thirdRowRight(VehicleType[][] startPositions) {

        VehicleType type;
        if (startPositions[2][2]!=null){

            if (startPositions[1][2]==null && isRight ){

                isRight = false;
                type = startPositions[2][2];
                right = command().createFormation(type, Formation.Type.FULL);
                command(right).moveRelatively(0,-lenght);
            }else if (startPositions[1][1]==null){

                if (startPositions[2][1]!=null){
                    isLeft = false;
                    thirdRowCenter(startPositions);

                    type = startPositions[2][2];
                    left = command().createFormation(type, Formation.Type.FULL);
                    command(left).moveRelatively(-lenght*2,0)
                            .moveRelatively(0,-lenght);
                }else {
                    isCenter =false;
                    type = startPositions[2][2];
                    center = command().createFormation(type, Formation.Type.FULL);
                    command(center).moveRelatively(-lenght,0)
                            .moveRelatively(0,-lenght);
                }
            }else {
                isLeft = false;
                type = startPositions[2][2];
                left = command().createFormation(type, Formation.Type.FULL);
                command(left).moveRelatively(-lenght*2,0)
                        .moveRelatively(0,-lenght);
            }

        }
    }

    private void thirdRowLeft(VehicleType[][] startPositions) {

        VehicleType type;


        if (startPositions[2][0] != null) {

            if (startPositions[1][0] == null && isLeft) {
                type = startPositions[2][0];
                isLeft = false;
                left = command().createFormation(type, Formation.Type.FULL);
                command(left).moveRelatively(0, -lenght);
            } else if (startPositions[1][1] == null) {

                if (startPositions[2][1] != null) {
                    isRight = false;
                    thirdRowCenter(startPositions);

                    type = startPositions[2][0];
                    right = command().createFormation(type, Formation.Type.FULL);
                    command(right).moveRelatively(lenght * 2, 0)
                            .moveRelatively(0, -lenght);
                } else {
                    isCenter= false;
                    type = startPositions[2][0];
                    center = command().createFormation(type, Formation.Type.FULL);
                    command(center).moveRelatively(lenght, 0)
                            .moveRelatively(0, -lenght);
                }

            } else {
                isRight =false;
                type = startPositions[2][0];
                right = command().createFormation(type, Formation.Type.FULL);
                command(right).moveRelatively(lenght * 2, 0)
                        .moveRelatively(0, -lenght);
            }


        }
    }

    private void thirdRowCenter(VehicleType[][] startPositions) {

        VehicleType type;
        if (startPositions[2][1]!=null){
            if (startPositions[1][1]==null && isCenter){
                isCenter =false;
                type = startPositions[2][1];

                center = command().createFormation(type, Formation.Type.FULL);
                command(center).moveRelatively(0,-lenght);
            }else if (startPositions[1][0]==null){

                if (startPositions[2][0]!=null){
                    isRight =false;
                    firstRowLeft(startPositions);

                    type = startPositions[2][1];
                    right= command().createFormation(type, Formation.Type.FULL);
                    command(right).moveRelatively(lenght,0)
                            .moveRelatively(0,-lenght);
                }else {
                    isLeft =false;
                    type = startPositions[2][1];
                    left = command().createFormation(type, Formation.Type.FULL);
                    command(left).moveRelatively(0,-lenght);
                }
            }else {
                isRight= false;
                type = startPositions[2][1];
                right = command().createFormation(type, Formation.Type.FULL);
                command(right).moveRelatively(lenght,0)
                        .moveRelatively(0,-lenght);
            }

        }

    }

    private void secondRowRight(VehicleType[][] startPosition) {
        VehicleType type;
        if (startPosition[1][2] != null) {
            isRight = false;
            type = startPosition[1][2];
            right = command().createFormation(type, Formation.Type.FULL);
        }
    }

    private void secondRowCenter(VehicleType[][] startPosition) {
        VehicleType type;
        if (startPosition[1][1] != null) {

            isCenter=false;
            type = startPosition[1][1];
            center = command().createFormation(type, Formation.Type.FULL);
        }
    }

    private void secondRowLeft(VehicleType[][] startPosition) {
        VehicleType type;
        if (startPosition[1][0] != null) {
            isLeft = false;
            type = startPosition[1][0];
            left = command().createFormation(type, Formation.Type.FULL);
        }
    }

    private void firstRowRight(VehicleType[][] startPositions) {
        VehicleType type;
        Formation right;
        Formation center;
        if (startPositions[0][2] != null) {

            if (startPositions[1][2] == null) {
                type = startPositions[0][2];
                isRight = false;
                right = command().createFormation(type, Formation.Type.FULL);
                command(right).moveRelatively(0, lenght);
            } else if (startPositions[1][1] == null) {

                if (startPositions[0][1] != null) {
                    firstRowCenter(startPositions);
                } else {

                    isCenter=false;
                    type = startPositions[0][2];
                    center = command().createFormation(type, Formation.Type.FULL);
                    command(center).moveRelatively(-lenght, 0)
                            .moveRelatively(0, lenght);
                }

            } else {
                isRight = false;
                type = startPositions[0][2];
                right = command().createFormation(type, Formation.Type.FULL);
                command(right).moveRelatively(-lenght * 2, 0)
                        .moveRelatively(0, lenght);
            }
        }
    }

    private void firstRowLeft(VehicleType[][] startPositions) {
        VehicleType type;
        Formation left;
        Formation right;
        if (startPositions[0][0] != null) {
            if (startPositions[1][0] == null) {

                isLeft = false;
                type = startPositions[0][0];
                left = command().createFormation(type, Formation.Type.FULL);
                command(left).moveRelatively(0, lenght);
                System.out.println("Left to y");
            } else if (startPositions[1][1] == null) {

                if (startPositions[0][1] != null) {
                    isRight = false;
                    firstRowCenter(startPositions);

                    type = startPositions[0][0];
                    right = command().createFormation(type, Formation.Type.FULL);
                    command(right).moveRelatively(lenght * 2, 0)
                            .moveRelatively(0, lenght);
                } else {

                    isRight =true;
                    type = startPositions[0][0];
                    right = command().createFormation(type, Formation.Type.FULL);
                    command(right).moveRelatively(lenght, 0)
                            .moveRelatively(0, lenght);
                }

            }


        }
    }

    private void firstRowCenter(VehicleType[][] startPositions) {
        VehicleType type;
        Formation center;
        Formation left;
        Formation right;
        if (startPositions[0][1] != null) {
            if (startPositions[1][1] == null && isCenter) {
                isCenter =false;
                type = startPositions[0][1];
                center = command().createFormation(type, Formation.Type.FULL);
                command(center).moveRelatively(0, lenght);
            } else if (startPositions[1][0] == null) {

                if (startPositions[0][0] == null) {
                    isLeft =true;

                    type = startPositions[0][1];

                    left = command().createFormation(type, Formation.Type.FULL);
                    command(left).moveRelatively(-lenght, 0)
                            .moveRelatively(0, lenght);
                } else {
                    isRight = false;
                    firstRowLeft(startPositions);

                    type = startPositions[0][1];
                    right = command().createFormation(type, Formation.Type.FULL);
                    command(right).moveRelatively(lenght, 0)
                            .moveRelatively(0, lenght);
                }
            } else {
                isRight =true;
                type = startPositions[0][1];
                right = command().createFormation(type, Formation.Type.FULL);
                command(right).moveRelatively(lenght, 0)
                        .moveRelatively(0, lenght);
            }
        }
    }


    public Command command() {
        return command(null);
    }

    public Command command(Formation formation) {
        ChainCommand chainCommand = new ChainCommand();
        commandMap.put(commandMap.size() + 1, chainCommand);
        return chainCommand.createCommand(formation);
    }


    private void move() {

        commandMap.entrySet().removeIf(entry -> {

            ChainCommand chain = entry.getValue();

            while (chain.isNext()) {
                List<Consumer<Move>> execute = chain.execute();

                if (execute == null) return true;
                execute.forEach(moveConsumer -> {
                    System.out.println(moveConsumer.getClass());
                    delayedMoves.add(moveConsumer);
                });
            }

            return chain.isComplete();

        });
    }


}
