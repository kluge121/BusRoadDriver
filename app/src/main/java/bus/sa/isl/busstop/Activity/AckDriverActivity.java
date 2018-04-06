package bus.sa.isl.busstop.Activity;

import android.media.Image;
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
import java.util.List;

import bus.sa.isl.busstop.Entity.PreAckEntity;
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
 * Created by baeminsu on 2017. 9. 27..
 */

public class AckDriverActivity extends Font {

    RecyclerView recyclerView;
    Adapter mAdapter;

    // 생각해보니 리스너 굳이 필요하지 않넹 ㅎ

    interface CompleteListener {
        void complete(int position);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ack);

        recyclerView = (RecyclerView) findViewById(R.id.ack_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainContext.getContext()));
        new AsyncGet().execute();
    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements CompleteListener {


        ArrayList arraList = new ArrayList<PreAckEntity>();

        public Adapter(ArrayList arraList) {
            this.arraList = arraList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MainContext.getContext()).inflate(R.layout.recyclerview_ack_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            holder.setView((PreAckEntity) arraList.get(position));
            holder.checkMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Integer boxInt = position;
                    new AsyncAck().execute(boxInt.toString(), holder.tv.getText().toString());
                }
            });


            holder.xMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Integer boxInt = position;
                    new AsyncReject().execute(boxInt.toString(), holder.tv.getText().toString());
                }
            });


        }

        @Override
        public int getItemCount() {
            return arraList.size();
        }

        @Override
        public void complete(int position) {
            arraList.remove(position);
            notifyDataSetChanged();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv;
            ImageButton checkMark;
            ImageButton xMark;

            public ViewHolder(View view) {
                super(view);
                tv = view.findViewById(R.id.driverId);
                checkMark = view.findViewById(R.id.check);
                xMark = view.findViewById(R.id.xmark);
            }

            void setView(PreAckEntity data) {
                tv.setText(data.getName());
            }
        }

    }

    class AsyncGet extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... voids) {

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .get()
                    .url("http://52.79.108.145/busstop_server/drive_wait_list_get.php?affiliation=" + PropertyManager.getInstance().getAffiliation())
                    .build();


            try {
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                return new JSONArray(strResponse);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            try {
                mAdapter = new Adapter(jsonArrayHandling(jsonArray));
                recyclerView.setAdapter(mAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class AsyncAck extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", strings[1]);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url("http://52.79.108.145/busstop_server/ack_driver.php")
                        .post(body)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return Integer.parseInt(strings[0]);
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            mAdapter.complete(s);

        }
    }

    class AsyncReject extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", strings[1]);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url("http://52.79.108.145/busstop_server/reject_driver.php")
                        .post(body)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


            return Integer.parseInt(strings[0]);
        }


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mAdapter.complete(integer);
        }


    }


    ArrayList jsonArrayHandling(JSONArray jsonArray) throws JSONException {

        List tmp = new ArrayList<PreAckEntity>();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            tmp.add(new PreAckEntity(jsonObject.getString("0"), jsonObject.getString("1")));
        }

        return (ArrayList) tmp;
    }


}
