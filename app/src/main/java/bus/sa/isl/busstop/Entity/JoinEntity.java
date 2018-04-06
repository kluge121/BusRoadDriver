package bus.sa.isl.busstop.Entity;

/**
 * Created by alstn on 2017-09-06.
 */

public class JoinEntity {
    String id;
    String pass;
    String affiliation;
    int type;

    public JoinEntity(String id, String pass, String affiliation, int type) {
        this.id = id;
        this.pass = pass;
        this.affiliation = affiliation;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getPass() {
        return pass;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public int getType() {
        return type;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }



    public void setType(int type) {
        this.type = type;
    }
}
