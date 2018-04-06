package bus.sa.isl.busstop.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.Font;
import bus.sa.isl.busstop.Set.MainContext;
import bus.sa.isl.busstop.Set.PropertyManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by baeminsu on 2017. 10. 10..
 */

public class DriverListActivity extends Font {


    Adapter mAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_driver_list);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_driver_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainContext.getContext()));

        new AsyncGet().execute();


    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        ArrayList arrayList;

        Adapter(ArrayList arrayList) {
            this.arrayList = arrayList;
        }


        public void complete(int position) {
            arrayList.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.recyclerview_item_dirver_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.setView((String) arrayList.get(position));
            holder.xbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AsyncWdw().execute(holder.tv.getText().toString(),new Integer(position).toString());

                }
            });

        }


        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv;
            ImageButton xbtn;

            public ViewHolder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.list_driver);
                xbtn = itemView.findViewById(R.id.driver_withdrawa);
            }

            public void setView(String s) {
                tv.setText(s);
            }
        }
    }


    class AsyncGet extends AsyncTask<Void, Void, Void> {

        JSONArray jsonArray = null;

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://52.79.108.145/busstop_server/driver_list_get.php?affiliation=" +
                            PropertyManager.getInstance().getAffiliation())
                    .get()
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                jsonArray = new JSONArray(strResponse);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                mAdapter = new Adapter(jsonHandling(jsonArray));
                recyclerView.setAdapter(mAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    class AsyncWdw extends AsyncTask<String, Void, Void> {

        int position;
        JSONArray jsonArray = null;

        @Override
        protected Void doInBackground(String... strings) {
            position = Integer.parseInt(strings[1]);
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id",strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON,jsonObject.toString());
            Request request = new Request.Builder()
                    .url("http://52.79.108.145/busstop_server/driver_withdrawal.php")
                    .post(body)
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                jsonArray = new JSONArray(strResponse);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.complete(position);


        }


    }


    ArrayList jsonHandling(JSONArray jsonArray) throws JSONException {

        ArrayList<String> arrayList = new ArrayList();
        JSONObject tmp;
        for (int i = 0; i < jsonArray.length(); i++) {
            tmp = jsonArray.getJSONObject(i);
            Log.e("체크", tmp.getString("0"));
            arrayList.add(tmp.getString("0"));
        }

        return arrayList;

    }


}
