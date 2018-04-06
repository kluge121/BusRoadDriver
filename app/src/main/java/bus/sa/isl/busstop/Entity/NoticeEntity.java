package bus.sa.isl.busstop.Entity;

/**
 * Created by alstn on 2017-07-26.
 */

public class NoticeEntity {
    private String strTitle;
    private String strDate;
    private String strCount;


    public NoticeEntity(String strTitle, String strDate, String strCount) {
        this.strTitle = strTitle;
        this.strDate = strDate;
        this.strCount = strCount;
    }

    public NoticeEntity(String strTitle, String strDate) {
        this.strTitle = strTitle;
        this.strDate = strDate;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public void setStrCount(String strCount) {
        this.strCount = strCount;
    }

    public String getStrTitle() {

        return strTitle;
    }

    public String getStrDate() {
        return strDate;
    }

    public String getStrCount() {
        return strCount;
    }
}
