package bus.sa.isl.busstop.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bus.sa.isl.busstop.Adpater.LiningListRecyclerViewAdapter;
import bus.sa.isl.busstop.Entity.DrivingEntity;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.Font;
import bus.sa.isl.busstop.Set.PropertyManager;
import bus.sa.isl.busstop.Set.RecyclerViewDecoration;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alstn on 2017-08-19.
 */

public class LiningDetailListActivity extends Font implements View.OnClickListener {

    RecyclerView recyclerView;
    LiningListRecyclerViewAdapter mAdapter;
    ImageButton backBtn;
    Button editBtn;
    Button mapViewBtn;
    Integer lineNum;
    ArrayList stopArray;
    TextView Title;
    String lineName;
    ImageButton refresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lining_detail);
        Intent intent = getIntent();
        lineNum = intent.getIntExtra("lineNum", -1);
        setWidget();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewDecoration(-4));

        new AsyncGet().execute(lineNum);


    }

    void setWidget() {

        Title = (TextView) findViewById(R.id.lining_detail_title);
        recyclerView = (RecyclerView) findViewById(R.id.liningRecyclerView);
        mapViewBtn = (Button) findViewById(R.id.btnMap);
        backBtn = (ImageButton) findViewById(R.id.lining_backkey);
        editBtn = (Button) findViewById(R.id.lining_line_edit);
        refresh = (ImageButton) findViewById(R.id.lining_detail_list_refresh);
        editBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        mapViewBtn.setOnClickListener(this);
        refresh.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lining_backkey:
                finish();
                break;
            case R.id.lining_line_edit:
                intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.btnMap:
                intent = new Intent(getApplicationContext(), LiningDetailMapActivity.class);
                intent.putExtra("list", stopArray);
                intent.putExtra("lineNum", lineNum);
                intent.putExtra("lineName", lineName);
                startActivity(intent);
                break;
            case R.id.lining_detail_list_refresh:
                new AsyncGet().execute(lineNum);
                break;


        }

    }


    private class AsyncGet extends AsyncTask<Integer, Void, JSONObject> {

        ProgressDialog progressDialog = new ProgressDialog(LiningDetailListActivity.this);

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
                lineName = jsonObject.getString("line");
                Title.setText(PropertyManager.getInstance().getAffiliation() + " - " + jsonObject.getString("line"));
                stopArray = jsonItemHandling(jsonArray);
                mAdapter = new LiningListRecyclerViewAdapter(stopArray);
                recyclerView.setAdapter(mAdapter);
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
