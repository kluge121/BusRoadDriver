package bus.sa.isl.busstop.Entity;

/**
 * Created by baeminsu on 2017. 9. 27..
 */

public class PreAckEntity {

    String name;
    String date;

    public PreAckEntity(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {

        return name;
    }

    public String getDate() {
        return date;
    }
}
