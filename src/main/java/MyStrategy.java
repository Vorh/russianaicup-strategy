import control.ChainCommand;
import control.Command;
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


    private Random random;

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
            createFormation();
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

        oldInfo.init(this.game, this.me, this.move, this.world, vehicleById);
        Command.setOldInfo(oldInfo);

        this.me = me;
        this.world = world;
        this.game = game;
        this.move = move;



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

        newInfo.init(game, me, move, world, vehicleById);
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


    private void createFormation() {


        Facility[] facilities = newInfo.getWorld().getFacilities();

        for (Facility facility : facilities) {
            System.out.println("X " + facility.getLeft() + " Y " + facility.getTop());
        }

        command().select(VehicleType.ARRV, Formation.Type.MERIDIEM)
                .scale(0.5)
                .move(250,250);

//                .capture(facilities[0])
//                .move();
//        command().select(VehicleType.ARRV, Formation.Type.MERIDIANAM)
//                .move();
//
//        command().select(VehicleType.ARRV, Formation.Type.EUROBOREUS)
//                .move();
//        command().select(VehicleType.ARRV, Formation.Type.CAURUS)
//                .move();


//        command().select(VehicleType.FIGHTER, Formation.Type.MERIDIANAM)
//                .move(300,100);
//        command().select(VehicleType.FIGHTER, Formation.Type.EUROBOREUS)
//                .move(150,400);




    }


    public Command command() {
        ChainCommand chainCommand = new ChainCommand();
        commandMap.put(commandMap.size() + 1, chainCommand);
        return chainCommand.createCommand();
    }


    private void move() {
        commandMap.entrySet().removeIf(entry -> {
            ChainCommand chain = entry.getValue();

            while (chain.isNext()){
                Consumer<Move> execute = chain.execute();

                if (execute == null) return true;
                System.out.println(execute.getClass());
                delayedMoves.add(execute);
            }

            return chain.isComplete();
        });
    }


}
