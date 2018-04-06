package bus.sa.isl.busstop.TestSet;

import java.util.ArrayList;

import bus.sa.isl.busstop.Entity.MarkerEntity;

/**
 * Created by alstn on 2017-07-26.
 */

public class MarkerListSet {

    static ArrayList<MarkerEntity> a = new ArrayList<MarkerEntity>();

    public static ArrayList getMarkerTest() {
        a.clear();
        a.add(new MarkerEntity(36.354538, 127.354495, "갑천역"));
        a.add(new MarkerEntity(36.353722, 127.341588, "유성온천역"));
        a.add(new MarkerEntity(36.356603, 127.330678, "구암역"));
        a.add(new MarkerEntity(36.359076, 127.321504, "현충역"));
        a.add(new MarkerEntity(36.366776, 127.317879, "월드컵경기장역"));
        a.add(new MarkerEntity(36.374153, 127.317959, "노은역"));


        return a;

    }


}
