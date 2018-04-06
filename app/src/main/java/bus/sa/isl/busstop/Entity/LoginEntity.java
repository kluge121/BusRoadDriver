package bus.sa.isl.busstop.Entity;

/**
 * Created by alstn on 2017-09-07.
 */

public class LoginEntity {

    String ID;
    String PASS;


    public LoginEntity(String ID, String PASS) {
        this.ID = ID;
        this.PASS = PASS;

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setPASS(String PASS) {
        this.PASS = PASS;
    }

    public String getID() {
        return ID;
    }

    public String getPASS() {
        return PASS;
    }
}
