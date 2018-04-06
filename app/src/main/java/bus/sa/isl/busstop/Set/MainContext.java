package bus.sa.isl.busstop.Set;

import android.app.Application;
import android.content.Context;

import com.tsengvn.typekit.Typekit;

/**
 * Created by alstn on 2017-07-24.
 */

public class MainContext extends Application {

    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }


    public static Context getContext() {
        return mContext;
    }

}
