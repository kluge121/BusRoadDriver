package bus.sa.isl.busstop.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bus.sa.isl.busstop.Adpater.DriveRecyclerViewAdapter;
import bus.sa.isl.busstop.Adpater.LineRecyclerViewAdapter;
import bus.sa.isl.busstop.Entity.LineEntity;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.MainContext;
import bus.sa.isl.busstop.Set.PropertyManager;
import bus.sa.isl.busstop.TestSet.DriveSet;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alstn on 2017-07-24.
 */

public class MainTab3 extends Fragment {

    RecyclerView recyclerView;
    DriveRecyclerViewAdapter mAdapter;
    SwipeRefreshLayout swipe;

    public static MainTab3 newInstance() {
        return new MainTab3();
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab3, container, false);
        recyclerView = v.findViewById(R.id.driveRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainContext.getContext()));
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.tab3_swipe_layout);
        recyclerView.setAdapter(mAdapter);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MainTab3.AsyncGet().execute(PropertyManager.getInstance().getAffiliation());
            }
        });
        new MainTab3.AsyncGet().execute(PropertyManager.getInstance().getAffiliation());
        return v;
    }


    public class AsyncGet extends AsyncTask<String, Void, JSONObject> {

        JSONObject responseJson;
        ProgressDialog progressDialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("목록 로딩중");
            progressDialog.show();

        }


        @Override
        protected JSONObject doInBackground(String... strings) {

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            OkHttpClient okHttpClient = new OkHttpClient();

            String affiliation = strings[0];
            Request request = new Request.Builder()
                    .url("http://52.79.108.145/busstop_server/line_info_get.php?affiliation=" + affiliation)
                    .get()
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();

                responseJson = new JSONObject(strResponse);
                return responseJson;

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
                mAdapter = new DriveRecyclerViewAdapter(jsonItemHandling(jsonArray), getContext());
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                if (swipe.isRefreshing()) {
                    swipe.setRefreshing(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }

    ArrayList jsonItemHandling(JSONArray jsonArray) throws JSONException {

        ArrayList<LineEntity> arrayList = new ArrayList<LineEntity>();
        JSONObject tmpObject;

        for (int i = 0; i < jsonArray.length(); i++) {

            tmpObject = jsonArray.getJSONObject(i);
            int linenum = tmpObject.getInt("0");
            String name = tmpObject.getString("1");
            String way = tmpObject.getString("2");
            int drive = tmpObject.getInt("3");
            String location = tmpObject.getString("4");

            arrayList.add(new LineEntity(name, way, drive, location, linenum));
        }
        return arrayList;
    }

}
