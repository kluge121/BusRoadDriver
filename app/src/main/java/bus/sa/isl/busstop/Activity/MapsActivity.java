package bus.sa.isl.busstop.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import bus.sa.isl.busstop.Adpater.MarkerListRecyclerViewAdpater;
import bus.sa.isl.busstop.Entity.MarkerEntity;
import bus.sa.isl.busstop.Fragment.MainTab3;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.ItemTouchHelperCallback;
import bus.sa.isl.busstop.Set.MainContext;
import bus.sa.isl.busstop.Set.PermissionUtil;
import bus.sa.isl.busstop.TestSet.MarkerListSet;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static bus.sa.isl.busstop.Activity.DrivingActivity.MAP_PERMISSION_REQUEST;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MarkerListRecyclerViewAdpater.OnStartDragListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    public static ArrayList<Marker> markerArrayList; // 지도상의 마커 저장 리스트
    private final long FINISH_INTERVAL_TIME = 2000;
    private final float DEFAULT_ZOOM = 15f;
    private DrawerLayout mDrawerLayout;
    private View mDrawerView;
    private ImageButton drawerBtn;
    private GoogleMap mMap;
    private Marker mNewMarker;
    private Button mRegister;
    View myCMarker;
    View busCMarker;
    TextView mTvMarker;
    ItemTouchHelper itemTouchHelper;
    private RecyclerView mRecyclerView;
    private MarkerListRecyclerViewAdpater mAdpater;
    ToggleButton toggleButton;
    ImageButton backBtn;
    private int cnt;
    LatLng firstAddress;
    Marker busMarekr;
    EditText mLineName;
    String affiliation;
    AsyncTask asyncTaskLine;
    AsyncTask asyncTaskStop;

    FusedLocationProviderClient mFusedLocationProviderClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        affiliation = getIntent().getStringExtra("affiliation");
        setWidget();


        //gpsTracker = new GPSTracker(MapsActivity.this);
        markerArrayList = new ArrayList<Marker>();
        // mAdpater = new MarkerListRecyclerViewAdpater(new ArrayList<MarkerEntity>(), this);
        mAdpater = new MarkerListRecyclerViewAdpater(new ArrayList<MarkerEntity>(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainContext.getContext()));
        mRecyclerView.setAdapter(mAdpater);

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(mAdpater));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        cnt = mAdpater.getItemCount();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드 올림


        drawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMapName();
                mDrawerLayout.openDrawer(mDrawerView);

            }
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
        createLocationRequest();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mAdpater.setMarkerClickListner(new MarkerListRecyclerViewAdpater.MarekrClickListner() {
            @Override
            public void clickSettingCamera(LatLng latLng) {
                CameraPosition cp = new CameraPosition.Builder().target((latLng)).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!(PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            getLocationPermission();
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onBackPressed() {
        long backPressedTime = 0;
        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - backPressedTime;

        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            updateMapName();
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed();
            } else {
                backPressedTime = currentTime;
                Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 종료 됩니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        setCustomMarkerView();


    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

    } // view를 bitmap으로 전환

    private void setCustomMarkerView() {
        myCMarker = LayoutInflater.from(this).inflate(R.layout.custom_marker, null);
        mTvMarker = myCMarker.findViewById(R.id.custom_marker_tv);
        busCMarker = LayoutInflater.from(this).inflate(R.layout.custom_marker2, null);
    }

    @Override
    public void onStartDrag(MarkerListRecyclerViewAdpater.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);

    }

    void setWidget() {
        mRecyclerView = findViewById(R.id.busstop_list_recyclerview);
        mRegister = findViewById(R.id.line_register);
        backBtn = findViewById(R.id.map_backkey);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerBtn = findViewById(R.id.drawer_open_btn);
        mDrawerView = findViewById(R.id.drawer_view);
        toggleButton = findViewById(R.id.togleBtn);
        mLineName = findViewById(R.id.busstop_list_name);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdpater.getArrayList().size() == 0) {
                    Toast.makeText(getApplicationContext(), "정류장을 하나 이상 생성해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    asyncTaskLine = new AsyncLineCreate().execute();
                }

            }
        });
    }

    @Override
    public void onMapClick(LatLng point) {  // 마커 추가

        String tmpName;

        if (toggleButton.isChecked()) {
            MarkerOptions markerOptions = new MarkerOptions();
            tmpName = "marker" + cnt++;
            mTvMarker.setText(tmpName);
            markerOptions.title("New Postion").position(point).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, myCMarker)));
            mNewMarker = mMap.addMarker(markerOptions);
            mAdpater.itemAdd(new MarkerEntity(point.latitude, point.longitude, tmpName));
            markerArrayList.add(mNewMarker);
        }


    }

    @Override
    public boolean onMarkerClick(Marker marker) {  // 마커 삭제
        if (toggleButton.isChecked()) {
            marker.hideInfoWindow();
            int position = mAdpater.findPosition(marker.getPosition());
            if (position == -1) return false;
            mAdpater.itemRemove(position);
            markerArrayList.remove(position);
            marker.remove();
            return true;
        }
        return true;
    }

    public void getMarker() {
        ArrayList<MarkerEntity> arrayList = mAdpater.getArrayList();

        for (int i = 0; i < arrayList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(arrayList.get(i).getMarkerName());
            mTvMarker.setText(arrayList.get(i).getMarkerName());
            markerOptions.position(arrayList.get(i).getLocation());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, myCMarker)));
            markerArrayList.add(mMap.addMarker(markerOptions));
        }
    }

    public void updateMapName() {
        ArrayList<MarkerEntity> arrayList = mAdpater.getArrayList();
        int count = arrayList.size();

        for (int i = 0; i < count; i++) {
            Marker tmpmarker = markerArrayList.remove(markerArrayList.size() - 1);
            tmpmarker.remove();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(arrayList.get(i).getLocation());
            markerOptions.title(arrayList.get(i).getMarkerName());
            mTvMarker.setText(arrayList.get(i).getMarkerName());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, myCMarker)));
            Marker tmpMarekr = mMap.addMarker(markerOptions);
            markerArrayList.add(0, tmpMarekr);

        }


    }


    private void createLocationCallback() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                firstAddress = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                mCurrentLocation = locationResult.getLastLocation();
                LatLng tmpLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                CameraPosition cp = new CameraPosition.Builder().target((firstAddress)).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
//                getMarker();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstAddress, DEFAULT_ZOOM));
                busMarekr = mMap.addMarker(new MarkerOptions().position(firstAddress).title("현재위치"));
                Log.e("업데이트체커", "체커");
                locationUpdateStop();

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

    private void getLocationPermission() {
        if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            getCurrentLocation();
        } else {
            String[] strRequestPermission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, strRequestPermission, MAP_PERMISSION_REQUEST);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MAP_PERMISSION_REQUEST) {
            if (PermissionUtil.verifyPermission(grantResults)) {
                getCurrentLocation();
            } else {
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

    void locationUpdateStop() {
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mFusedLocationProviderClient = null;
                        }
                    });
        }
    }

    private class AsyncLineCreate extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            JSONObject responseJson;
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            try {
                RequestBody body = RequestBody.create(JSON, lineAdapterHandling().toString());
                Request request = new Request.Builder()
                        .url("http://52.79.108.145/busstop_server/line_create.php")
                        .post(body)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String strResponse = response.body().string();
                    responseJson = new JSONObject(strResponse);
                    return responseJson.getInt("data");
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("체크", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.e("체크", integer + "");
            if (integer == -1) {
                Toast.makeText(getApplicationContext(), "노선이름이 중복되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            asyncTaskStop = new AsyncStopCreate().execute(integer);

        }
    }


    private class AsyncStopCreate extends AsyncTask<Integer, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... integers) {

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();
            try {
                RequestBody body = RequestBody.create(JSON, stopItemAdapterHandling(mAdpater.getArrayList(), integers[0]).toString());
                Request request = new Request.Builder()
                        .url("http://52.79.108.145/busstop_server/stop_create.php")
                        .post(body)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                if (response.body() != null) {
                    String strResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(strResponse);
                    return jsonObject.getString("result");

                }
            } catch (Exception e) {
                e.printStackTrace();

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                Toast.makeText(getApplicationContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
            } else if (!s.equals("fail")) {
                Toast.makeText(getApplicationContext(), "노선이 등록되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }


        }

    }


    JSONObject stopItemAdapterHandling(ArrayList<MarkerEntity> list, int lineNum) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("stop_line", lineNum);
        for (int i = 0; i < list.size(); i++) {
            JSONObject tmpObject = new JSONObject();
            tmpObject.put("stop_name", list.get(i).getMarkerName());
            tmpObject.put("stop_sort", i);
            tmpObject.put("stop_lat", list.get(i).getLatitude());
            tmpObject.put("stop_lon", list.get(i).getLongitude());
            jsonArray.put(tmpObject);
        }
        jsonObject.put("data", jsonArray);

        return jsonObject;
    }

    JSONObject lineAdapterHandling() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("line_name", mLineName.getText().toString());
        jsonObject.put("line_way", mAdpater.getArrayList().get(mAdpater.getItemCount() - 1).getMarkerName() + " 방향");
        jsonObject.put("line_bool", 0);
        jsonObject.put("line_nowlocation", "운행중 아님");
        jsonObject.put("line_affiliation", affiliation);

        return jsonObject;
    }


}
