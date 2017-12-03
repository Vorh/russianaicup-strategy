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

    private void setStartTargetHelicopter(){

        Formation fighterMeridiem = command().select(VehicleType.HELICOPTER, Formation.Type.MERIDIEM)
                .createFormation(commandCenter.getNextId());
        Formation fighterCaurus = command().select(VehicleType.HELICOPTER, Formation.Type.CAURUS)
                .createFormation(commandCenter.getNextId());
        Formation fighterEuroboreus = command().select(VehicleType.HELICOPTER, Formation.Type.EUROBOREUS)
                .createFormation(commandCenter.getNextId());
        Formation fighterMeridianam = command().select(VehicleType.HELICOPTER, Formation.Type.MERIDIANAM)
                .createFormation(commandCenter.getNextId());

        commandCenter.add(fighterCaurus);
        commandCenter.add(fighterEuroboreus);
        commandCenter.add(fighterMeridianam);
        commandCenter.add(fighterMeridiem);

        command().select(fighterEuroboreus).scale(0.1);
        command().select(fighterMeridiem).scale(0.1);
        command().select(fighterMeridianam).scale(0.1);
        command().select(fighterCaurus).scale(0.1);

        int ranger = 100;
        if (commandCenter.pathFreeAir(CommandCenter.Direction.SOUTH, VehicleType.HELICOPTER)) {
            command().select(fighterMeridiem).move( 166,250);
//            command().select(fighterEuroboreus).moveRelatively(0,ranger);
//            command().select(fighterMeridiem).moveRelatively(0,ranger);
//            command().select(fighterCaurus).moveRelatively(0,ranger);
        }else {
//            command().select(fighterMeridianam).moveRelatively(ranger,0);
//            command().select(fighterEuroboreus).moveRelatively(ranger,0);
//            command().select(fighterMeridiem).moveRelatively(ranger,0);
//            command().select(fighterCaurus).moveRelatively(ranger,0);
        }
    }

    private void createFormation() {

        commandCenter = new CommandCenter(oldInfo,newInfo);

        setStartTargetHelicopter();

    }


    public Command command() {
        ChainCommand chainCommand = new ChainCommand();
        commandMap.put(commandMap.size() + 1, chainCommand);
        return chainCommand.createCommand();
    }


    private void move() {

        commandMap.entrySet().removeIf(entry -> {
            ChainCommand chain = entry.getValue();

//            System.out.println(chain.getInfo());

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
