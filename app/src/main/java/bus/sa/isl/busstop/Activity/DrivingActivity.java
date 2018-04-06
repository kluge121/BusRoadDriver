package bus.sa.isl.busstop.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bus.sa.isl.busstop.Adpater.DrivingListRecyclerViewAdapter;
import bus.sa.isl.busstop.Adpater.LiningListRecyclerViewAdapter;
import bus.sa.isl.busstop.DrivingService;
import bus.sa.isl.busstop.Entity.DrivingEntity;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.GPSTracker;
import bus.sa.isl.busstop.Set.PermissionUtil;
import bus.sa.isl.busstop.Set.PropertyManager;
import bus.sa.isl.busstop.Set.RecyclerViewDecoration;
import bus.sa.isl.busstop.TestSet.DrivingSet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alstn on 2017-08-07.
 */

public class DrivingActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    DrivingListRecyclerViewAdapter mAdapter;
    ImageButton backBtn;
    private Messenger mRemote;
    final int MSG_SERVICE_NUM = 3;
    final static int MAP_PERMISSION_REQUEST = 100;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mRemote = new Messenger(service);
            if (mRemote != null) {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = new Messenger(new RemoteHandler());

                try {
                    mRemote.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mRemote = null;

        }
    };
    int lastNum;
    int lineNum;
    TextView Title;
    ArrayList<DrivingEntity> arrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        Intent intent = getIntent();
        lineNum = intent.getIntExtra("lineNum", -1);
        lastNum = -1;
        new AsyncGet().execute(lineNum);
        setWidget();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewDecoration(-4));


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!(PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            getLocationPermission();
        }

        testSendMessage();
    }

    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            drivingStopBack();
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "뒤로 버튼을 한 번 더 누르시면 운행이 종료 됩니다.", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onDestroy() {
        // unbindService(mConnection);
        super.onDestroy();
    }

    // 통신테스트 메소드
    public void testSendMessage() {
        if (mRemote != null) {
            Message msg = new Message();
            msg.what = 1;
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    void setWidget() {
        Title = (TextView) findViewById(R.id.driving_title);
        recyclerView = (RecyclerView) findViewById(R.id.drivingRecyclerView);
        backBtn = (ImageButton) findViewById(R.id.driving_backkey);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.driving_backkey:
                drivingStopBack();
                break;
            default:
        }
    }


    private void getLocationPermission() {
        if (!PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                !PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            String[] strRequestPermission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, strRequestPermission, MAP_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MAP_PERMISSION_REQUEST) {
            if (!PermissionUtil.verifyPermission(grantResults)) {
                showRequestAgainDialog();
            }

        }
    }

    private void showRequestAgainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("위치 정보 제공 권한이 없을시 앱이 정상 작동하지 않습니다.");
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Toast.makeText(getApplicationContext(), "앱 기능을 사용 할 수 없습니다. ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.create();
        builder.show();
    }

    private void drivingStopBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말 운행을 종료하시겠습니까?");
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                try {
                    unbindService(mConnection);
                    finish();
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
            }
        });
        builder.create();
        builder.show();
    }

    public class RemoteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    Log.e("통신보안 서비스 > 액티비티(0)", "액티비티 - 서비스 핸들러 연결완료 //  " + msg.obj.toString());
                    break;

                case MSG_SERVICE_NUM:
                    Log.e("통신보안 서비스 > 액티비티(MSG)", "현재 위치 업데이트" + msg.what + " " + lastNum);
                    mAdapter.setNowLocationImage((Integer) msg.obj, lastNum);
                    lastNum = (Integer) msg.obj;

                    break;
                default:
                    Log.e("통신보안 서비스 > 액티비티(d)", "Default 상황 발생");
                    break;
            }
        }
    }


    private class AsyncGet extends AsyncTask<Integer, Void, JSONObject> {

        ProgressDialog progressDialog = new ProgressDialog(DrivingActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("목록 로딩중");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Integer... integers) {

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://52.79.108.145/busstop_server/stop_info_get.php?linename=" + lineNum)
                    .get()
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                return new JSONObject(strResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                Title.setText(PropertyManager.getInstance().getAffiliation() + " - " + jsonObject.getString("line"));
                arrayList = jsonItemHandling(jsonArray);

                mAdapter = new DrivingListRecyclerViewAdapter(arrayList, getApplicationContext());
                recyclerView.setAdapter(mAdapter);
                Intent serviceIntent = new Intent(getApplicationContext(), DrivingService.class);
                serviceIntent.putExtra("list", mAdapter.getArrayList());
                serviceIntent.putExtra("lineNum", lineNum);
                bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }

    }

    ArrayList jsonItemHandling(JSONArray jsonArray) throws JSONException {

        final int FIRST_ITEM = 0;
        final int MIDDLE_ITEM = 1;
        final int LAST_ITEM = 2;

        ArrayList<DrivingEntity> arrayList = new ArrayList<DrivingEntity>();
        JSONObject tmpObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            tmpObject = jsonArray.getJSONObject(i);

            String name = tmpObject.getString("6");
            double lat = tmpObject.getDouble("1");
            double log = tmpObject.getDouble("2");
            int location = tmpObject.getInt("3");
            int reserve = tmpObject.getInt("4");
            int type;

            if (i == 0) {
                type = FIRST_ITEM;
            } else if (i == jsonArray.length() - 1) {
                type = LAST_ITEM;
            } else {
                type = MIDDLE_ITEM;
            }
            arrayList.add(new DrivingEntity(name, location, reserve, type, lat, log));
        }

        return arrayList;


    }
}
