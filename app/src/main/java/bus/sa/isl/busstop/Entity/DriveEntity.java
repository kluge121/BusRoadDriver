package bus.sa.isl.busstop.Entity;

/**
 * Created by alstn on 2017-07-26.
 */

public class DriveEntity {
    private String strDriveTitle;
    private String strDriveWay;
    private boolean bDriveState;


    public String getStrDriveTitle() {
        return strDriveTitle;
    }

    public String getStrDriveWay() {
        return strDriveWay;
    }

    public boolean isbDriveState() {
        return bDriveState;
    }

    public void setStrDriveTitle(String strDriveTitle) {
        this.strDriveTitle = strDriveTitle;
    }

    public void setStrDriveWay(String strDriveWay) {
        this.strDriveWay = strDriveWay;
    }

    public void setbDriveState(boolean bDriveState) {
        this.bDriveState = bDriveState;
    }

    public DriveEntity(String strDriveTitle, String strDriveWay, boolean bDriveState) {

        this.strDriveTitle = strDriveTitle;
        this.strDriveWay = strDriveWay;
        this.bDriveState = bDriveState;
    }
}
