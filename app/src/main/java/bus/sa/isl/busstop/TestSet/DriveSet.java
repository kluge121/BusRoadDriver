package bus.sa.isl.busstop.TestSet;

import java.util.ArrayList;

import bus.sa.isl.busstop.Entity.DriveEntity;


/**
 * Created by alstn on 2017-07-26.
 */

public class DriveSet {

    static ArrayList<DriveEntity> a = new ArrayList<DriveEntity>();

    public static ArrayList getData(){
        a.clear();
        a.add(new DriveEntity("1호선 가오동노선","한밭대학교-가오동",true));
        a.add(new DriveEntity("2호선 신탄진노선","한밭대학교-신탄진",false));
        a.add(new DriveEntity("3호선 노은동노선","한밭대학교-노은동",true));
        a.add(new DriveEntity("4호선 서대전노선","한밭대학교-서대전",false));


        return a;
    }
}
