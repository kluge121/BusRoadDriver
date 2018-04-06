package bus.sa.isl.busstop.Entity;

/**
 * Created by alstn on 2017-08-19.
 */

public class LiningEntity {

    private String strStopName;
    private boolean bLocationCheck;
    private boolean bReserveChcek;
    private int type;


    public LiningEntity(String strStopName, boolean bLocationCheck, boolean bReserveChcek, int itemtype) {

        this.strStopName = strStopName;
        this.bLocationCheck = bLocationCheck;
        this.bReserveChcek = bReserveChcek;
        this.type = itemtype;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {

        return type;
    }


    public String getStrStopName() {
        return strStopName;
    }

    public boolean isbLocationCheck() {
        return bLocationCheck;
    }

    public boolean isbReserveChcek() {
        return bReserveChcek;
    }

    public void setStrStopName(String strStopName) {
        this.strStopName = strStopName;
    }

    public void setbLocationCheck(boolean bLocationCheck) {
        this.bLocationCheck = bLocationCheck;
    }

    public void setbReserveChcek(boolean bReserveChcek) {
        this.bReserveChcek = bReserveChcek;
    }
}
