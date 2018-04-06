package bus.sa.isl.busstop.Set;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by alstn on 2017-09-07.
 */

public class PropertyManager {

    private static PropertyManager instance;

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    SharedPreferences mSp;
    SharedPreferences.Editor mEditor;

    private PropertyManager() {
        mSp = PreferenceManager.getDefaultSharedPreferences(MainContext.getContext());
        mEditor = mSp.edit();
    }

    private static final String AUTO_ID = "id";
    private static final String AUTO_PASS = "pass";
    private static final String AFFILIATION = "affiliation";
    private static final String TYPE = "type";




    public void setAutoLogin(String ID, String PASS, int value, int type) {
        mEditor.putInt("first", value);
        mEditor.putString(AUTO_ID, ID);
        mEditor.putString(AUTO_PASS, PASS);
        mEditor.putInt(TYPE, type);
        mEditor.commit();
    }

    public void setAffiliation(String affiliation) {
        mEditor.putString(AFFILIATION, affiliation);
        mEditor.commit();
    }


    public boolean getAutoLogin() {
//        Log.e("체크", mSp.getInt("first", 0) + "");
        if (mSp.getInt("first", 0) == 1) { // first값이 1 일때 오토로그인 , 0이면 로그인해야댑니당 헤헤
            return true;
        } else {
            return false;
        }

    }

    public int getType() {
        return mSp.getInt(TYPE, 0);
    }

    public String getID() {
        return mSp.getString(AUTO_ID, "");
    }

    public String getPass() {
        return mSp.getString(AUTO_PASS, "");
    }

    public String getAffiliation()

    {
        return mSp.getString(AFFILIATION, "");
    }

    public void setLogout(){
        mEditor.putInt("first", 0);
        mEditor.putString(AUTO_ID, "");
        mEditor.putString(AUTO_PASS, "");
        mEditor.putInt(TYPE, 0);
        mEditor.commit();
    }


}
