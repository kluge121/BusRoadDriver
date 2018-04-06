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
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import bus.sa.isl.busstop.Entity.DrivingEntity;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.PermissionUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static bus.sa.isl.busstop.Activity.DrivingActivity.MAP_PERMISSION_REQUEST;

public class LiningDetailMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    LatLng firstAddress;
    LatLng busAddress;
    ImageButton backBtn;
    ImageButton refreshBtn;
    Marker myMarker;
    Marker busMarker;
    View myCMarker;
    View busCMarker;
    int cpFlag = 0;
    TextView mTvMarker;
    ArrayList<DrivingEntity> markerArrayList;
    private final float DEFAULT_ZOOM = 15f;
    int lineNum;
    String lineName;
    TextView Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_detail_map);
        busAddress = new LatLng(0, 0);
        firstAddress = new LatLng(0, 0);
        lineName = getIntent().getStringExtra("lineName");
        Title = (TextView) findViewById(R.id.lining_detail_map_toolbar_title);
        Title.setText(lineName);
        lineNum = getIntent().getIntExtra("lineNum", -1);
        backBtn = findViewById(R.id.lining_detail_map_backkey);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        refreshBtn = findViewById(R.id.lining_detail_map_refresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AsyncPointGet().execute();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerArrayList = (ArrayList<DrivingEntity>) getIntent().getSerializableExtra("list");


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
    protected void onDestroy() {
        myMarker.remove();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setCustomMarkerView();
        getMarker();

        myMarker = mMap.addMarker(new MarkerOptions().position(firstAddress).title("현재위치"));
        busMarker = mMap.addMarker(new MarkerOptions().position(busAddress).title("버스위치"));
    }


    private void createLocationCallback() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                firstAddress = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                mCurrentLocation = locationResult.getLastLocation();
                LatLng tmpLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                if (cpFlag == 0) {
                    CameraPosition cp = new CameraPosition.Builder().target((firstAddress)).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstAddress, DEFAULT_ZOOM));
                    cpFlag = 1;
                }

                myMarker.setPosition(firstAddress);

            }
        };

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2);
        mLocationRequest.setFastestInterval(2);
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

    private void setCustomMarkerView() {
        myCMarker = LayoutInflater.from(this).inflate(R.layout.custom_marker, null);
        mTvMarker = myCMarker.findViewById(R.id.custom_marker_tv);
        busCMarker = LayoutInflater.from(this).inflate(R.layout.custom_marker, null);
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

    public void getMarker() {
        for (int i = 0; i < markerArrayList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(markerArrayList.get(i).getStrBusStopName());
            mTvMarker.setText(markerArrayList.get(i).getStrBusStopName().toString());
            markerOptions.position(markerArrayList.get(i).makeLatLng());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, myCMarker)));
            mMap.addMarker(markerOptions);
        }
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

    }

    class AsyncPointGet extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("http://52.79.108.145/busstop_server/line_point_get.php?lineNum=" + lineNum)
                    .get()
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                return new JSONObject(strResponse);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                JSONArray jsonArray = jsonObject.getJSONArray("msg");
                busAddress = new LatLng(jsonArray.getJSONObject(0).getDouble("0"), jsonArray.getJSONObject(0).getDouble("1"));
                Log.e("체크", jsonArray.getJSONObject(0).getDouble("0") + " " + jsonArray.getJSONObject(0).getDouble("1") + "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            busMarker.setPosition(busAddress);

        }
    }

}

