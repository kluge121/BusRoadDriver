package bus.sa.isl.busstop.TestSet;

import java.util.ArrayList;

import bus.sa.isl.busstop.Entity.DrivingEntity;
import bus.sa.isl.busstop.Entity.LiningEntity;

/**
 * Created by alstn on 2017-08-07.
 */

public class LiningSet {

    private static final int FIRST_ITEM = 0;
    private static final int MIDDLE_ITEM = 1;
    private static final int LAST_ITEM = 2;
    static ArrayList<LiningEntity> a = new ArrayList<LiningEntity>();

    public static ArrayList getData() {
        a.clear();
        a.add(new LiningEntity("노은역", false, false, FIRST_ITEM));
        a.add(new LiningEntity("월드컵경기장역", false, false, MIDDLE_ITEM));
        a.add(new LiningEntity("현충원역", false, false, MIDDLE_ITEM));
        a.add(new LiningEntity("구암역", false, false, MIDDLE_ITEM));
        a.add(new LiningEntity("유성온천역", true, false, MIDDLE_ITEM));
        a.add(new LiningEntity("갑천", false, false, LAST_ITEM));


        return a;
    }
}
