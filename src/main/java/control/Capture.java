package control;

import model.Facility;
import model.Move;
import model_custom.Formation;
import model_custom.Info;

import java.util.function.Consumer;

/**
 * Created by vorh on 12/2/17.
 */
public class Capture extends Command{

    private final Facility facility;
    protected final Mv mv;

    public Capture( Formation formation, Facility facility) {
        this.facility = facility;

        double left = facility.getLeft();
        double top = facility.getTop();
        mv = new Mv(left,top,formation);
    }

    @Override
    public boolean isComplete() {
        return mv.isComplete() && facility.getOwnerPlayerId() == newInfo.getMe().getId();
    }


    @Override
    public Command nextCommand() {
        Select select = new Select();
    }

    @Override
    public Consumer<Move> getMove() {
        return mv.getMove();
    }
}
