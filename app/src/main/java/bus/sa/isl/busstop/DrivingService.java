package bus.sa.isl.busstop;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import bus.sa.isl.busstop.Entity.DrivingEntity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by alstn on 2017-08-23.
 */

public class DrivingService extends Service {


    private Messenger mRemote;
    FusedLocationProviderClient mFusedLocationProviderClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    LatLng tmpLatLng;
    ArrayList<DrivingEntity> List;
    int busStopIndex;
    final int MSG_SERVICE_NUM = 3;
    Thread drivingThread;
    boolean threadFlag = false;
    boolean startFlag = true;
    int lineNum;


    // 서비스 핸들러러
    private class RemoteHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // Register activity hander
                    mRemote = (Messenger) msg.obj;
                    remoteSendMessage("서비스에서 액티비티 핸들러 등록 완료 응답");
//                    Log.e("통신보안s1", "액티비티 > 서비스 핸들러 전달");
                    break;
                default:
                    Log.e("통신보안 액티비티 > 서비스(d)", "Default 상황 발생");
                    break;
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        getCurrentLocation();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        List = (ArrayList<DrivingEntity>) intent.getSerializableExtra("list");
        lineNum = intent.getIntExtra("lineNum", -1);
        return new Messenger(new RemoteHandler()).getBinder();
    }

    private void createLocationCallback() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                //Toast.makeText(getApplicationContext(), "로케이션체크중", Toast.LENGTH_SHORT).show();
                //new AsyncLineUpdate().execute(lineNum);
                if (startFlag) {


                    startThread();
                    drivingThread.start();
                    startFlag = !startFlag;
                }
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressWarnings("MissingPermission")
    private void getCurrentLocation() {
        createLocationCallback();
        createLocationRequest();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadFlag = false;
        locationUpdateStop();
        new AsnycLineStop().execute();
        Toast.makeText(getApplicationContext(), "서비스 스레드 종료", Toast.LENGTH_SHORT).show();

    }


    void startThread() {

        busStopIndex = 0;
        threadFlag = true;
        drivingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                new AsyncLineUpdate().execute(lineNum);
                while (threadFlag) {
                    tmpLatLng = new LatLng(List.get(busStopIndex).getLatitude(), List.get(busStopIndex).getLongitude());

                    while (threadFlag) {

//                        Log.e("통신보안3 위도1", tmpLatLng.latitude - 0.0003 + "");
//                        Log.e("통신보안3 경도1", tmpLatLng.longitude - 0.0003 + "");
//                        Log.e("통신보안3 위도2", tmpLatLng.latitude + 0.0003 + "");
//                        Log.e("통신보안3 경도2", tmpLatLng.longitude + 0.0003 + "");
                        for (int i = busStopIndex; i <= busStopIndex; i++) {
                            tmpLatLng = new LatLng(List.get(i).getLatitude(), List.get(i).getLongitude());
                            if (mCurrentLocation.getLatitude() > tmpLatLng.latitude - 0.0003 &&
                                    mCurrentLocation.getLatitude() < tmpLatLng.latitude + 0.0003 &&
                                    mCurrentLocation.getLongitude() > tmpLatLng.longitude - 0.0003 &&
                                    mCurrentLocation.getLongitude() < tmpLatLng.longitude + 0.0003
                                    ) {
//                                Log.e("통신보안", "정류장 업데이트 : " + busStopIndex);

                                remoteSendMessage(busStopIndex);
                                new AsyncLineUpdate().execute(lineNum);
                                new AsnycStopUpdate().execute();
                                busStopIndex++;
                                Log.e("체크", busStopIndex + " 번");
                                break;
                            }
//                            new AsyncLineUpdate().execute(lineNum);
//                            new AsnycStopUpdate().execute();
                        }

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (List.size() == busStopIndex) threadFlag = !threadFlag;
                }
            }
        });

    }

    // Send message to activity
    public void remoteSendMessage(int num) {
        if (mRemote != null) {
            Message msg = new Message();
            msg.what = MSG_SERVICE_NUM;
            msg.obj = num;
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e("통신보안", "오류발생");
            }
        }
    }

    public void remoteSendMessage(String str) {
        if (mRemote != null) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = str;
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    void locationUpdateStop() {
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            mFusedLocationProviderClient = null;
        }
    }

    private class AsyncLineUpdate extends AsyncTask<Integer, Void, String> {


        @Override
        protected String doInBackground(Integer... integers) {
            Log.e("체크내부", "1");
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();


            JSONObject jsonObject = new JSONObject();
            try {
                if (busStopIndex == 0) busStopIndex = 1;
                jsonObject.put("line_location", List.get(--busStopIndex).getStrBusStopName());
                jsonObject.put("line_bool", 1);
                jsonObject.put("lineNum", lineNum);
                jsonObject.put("lat", mCurrentLocation.getLatitude());
                jsonObject.put("lon", mCurrentLocation.getLongitude());

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url("http://52.79.108.145/busstop_server/line_drive_update.php")
                        .post(body)
                        .build();
                Log.e("체크", "중간");
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                return strResponse;

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                Log.e("체크익셉션", "ㅁ");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("체크포스트", s);
        }
    }

    private class AsnycLineStop extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... integers) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lineNum", lineNum);
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url("http://52.79.108.145/busstop_server/line_stop.php")
                        .post(body)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();


                return strResponse;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

    private class AsnycStopUpdate extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... integers) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            OkHttpClient okHttpClient = new OkHttpClient();
            try {
                jsonObject.put("num", lineNum);
                jsonObject.put("sort", busStopIndex);
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url("http://52.79.108.145/busstop_server/stop_drive_update.php")
                        .post(body)
                        .build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();


            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
