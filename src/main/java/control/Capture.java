package control;

import model.Facility;
import model.Move;

import java.util.function.Consumer;

/**
 * Created by vorh on 12/2/17.
 */
public class Capture extends Command{

    private final Facility facility;
    protected Mv mv;

    public Capture( Facility facility) {
        this.facility = facility;

        double left = facility.getLeft();
        double top = facility.getTop();
//        mv = new Mv(left,top);
    }

    @Override
    public boolean isComplete() {
        return mv.isComplete() && facility.getOwnerPlayerId() == newInfo.getMe().getId();
    }


    @Override
    public Command nextCommand() {
//        Select select = new Select();
        return null;
    }

    @Override
    public Consumer<Move> getMove() {
        return mv.getMove();
    }
}
