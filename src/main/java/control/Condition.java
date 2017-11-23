package control;

import model_custom.Info;

/**
 * Created by vorh on 11/23/17.
 */
@FunctionalInterface
public interface Condition {


    boolean check(Info oldInfo, Info newInfo);

}
