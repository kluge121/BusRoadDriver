package bus.sa.isl.busstop.Entity;

/**
 * Created by alstn on 2017-07-26.
 */

public class LineEntity {
    private String strLineTitle;
    private String strLineWay;
    private int lineState;
    private String strLineLocation;
    private int lineNnm;

    public LineEntity(String strLineTitle, String strLineWay, int bLineState, String strLineLocation, int lineNnm) {
        this.lineNnm = lineNnm;
        this.strLineTitle = strLineTitle;
        this.strLineWay = strLineWay;
        this.lineState = bLineState;
        this.strLineLocation = strLineLocation;
    }


    public String getStrLineTitle() {
        return strLineTitle;

    }

    public String getStrLineWay() {
        return strLineWay;
    }

    public void setLineNnm(int lineNnm) {
        this.lineNnm = lineNnm;
    }

    public int getLineNnm() {

        return lineNnm;
    }

    public int getLineState() {
        return lineState;
    }

    public String getStrLineLocation() {
        return strLineLocation;
    }

    public boolean getIsDriving() {
        return lineState == 0;


    }

}
