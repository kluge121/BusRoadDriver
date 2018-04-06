package bus.sa.isl.busstop.Fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bus.sa.isl.busstop.Adpater.LineRecyclerViewAdapter;
import bus.sa.isl.busstop.Entity.LineEntity;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.MainContext;
import bus.sa.isl.busstop.Set.PropertyManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alstn on 2017-07-24.
 */

public class MainTab2 extends Fragment {


    RecyclerView recyclerView;
    LineRecyclerViewAdapter mAdpater;
    PropertyManager propertyManager = PropertyManager.getInstance();
    String affiliation;
    SwipeRefreshLayout swipe;

    @Override
    public void onResume() {
        super.onResume();
        new AsyncGet().execute(affiliation);

    }

    public void notifyList() {
        mAdpater.notifyDataSetChanged();
    }
    public static MainTab2 newInstance() {
        return new MainTab2();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab2, container, false);
        affiliation = propertyManager.getAffiliation();
        recyclerView = v.findViewById(R.id.lineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainContext.getContext()));
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.tab2_swipe_layout);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncGet().execute(affiliation);
            }
        });
        new AsyncGet().execute(affiliation);

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
                mAdpater = new LineRecyclerViewAdapter(jsonItemHandling(jsonArray), getContext());
                recyclerView.setAdapter(mAdpater);
                mAdpater.notifyDataSetChanged();
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
