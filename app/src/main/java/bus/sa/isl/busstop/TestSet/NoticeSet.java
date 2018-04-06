package bus.sa.isl.busstop.TestSet;

import java.util.ArrayList;

import bus.sa.isl.busstop.Entity.NoticeEntity;

/**
 * Created by alstn on 2017-07-26.
 */

public class NoticeSet {

    static ArrayList<NoticeEntity> a = new ArrayList<NoticeEntity>();

    public static ArrayList getData() {
        a.clear();
        a.add(new NoticeEntity("안녕하세요1", "2017-05-11", "31"));
        a.add(new NoticeEntity("안녕하세요2 글자수테스트중", "2017-05-12", "32"));
        a.add(new NoticeEntity("안녕하세요3", "2017-08-13", "33"));
        a.add(new NoticeEntity("안녕하세요5", "2017-08-15", "35"));
        a.add(new NoticeEntity("안녕하세요6", "2017-08-16", "36"));
        a.add(new NoticeEntity("안녕하세요7", "2017-08-17", "37"));
        a.add(new NoticeEntity("안녕하세요8", "2017-08-18", "38"));
        return a;
    }
}
