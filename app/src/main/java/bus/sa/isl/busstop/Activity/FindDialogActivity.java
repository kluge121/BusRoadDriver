package bus.sa.isl.busstop.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.Font;
import bus.sa.isl.busstop.Set.MainContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by baeminsu on 2017. 9. 25..
 */

public class FindDialogActivity extends Font {

    Adapter mAdapter;
    RecyclerView recyclerView;
    String keyword;
    TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_find_dialog);

//        this.setFinishOnTouchOutside(false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_find);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainContext.getContext()));
        keyword = getIntent().getStringExtra("keyword");
        title = (TextView) findViewById(R.id.find_title);
        title.setText("\'" + keyword + "\' 검색결과");
        new AsnycGet().execute();


    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHoler> {
        ArrayList<String> arrayList = new ArrayList<String>();

        public Adapter(ArrayList<String> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_find_affiliation, parent, false);
            return new ViewHoler(v);
        }

        @Override
        public void onBindViewHolder(final ViewHoler holder, int position) {
            holder.setView(arrayList.get(position));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    Log.e("체크", holder.tv.getText() + "");
                    intent.putExtra("Affiliation", holder.tv.getText());
                    setResult(100, intent);
                    finish();
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        class ViewHoler extends RecyclerView.ViewHolder {

            View view;
            TextView tv;

            ViewHoler(View itemView) {
                super(itemView);
                view = itemView;
                tv = view.findViewById(R.id.find_affiliation);
            }

            void setView(String a) {
                if (a != null) {
                    tv.setText(a);
                }
            }

        }

    }

    private class AsnycGet extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... voids) {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .get()
                    .url("http://52.79.108.145/busstop_server/find_affiliation.php?affiliation=" + keyword)
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                if (strResponse.equals("NULL")) {
                    return null;
                } else {
                    return new JSONArray(strResponse.toString());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            try {
                if (jsonArray != null) {

                    mAdapter = new Adapter(jsonArrayHandling(jsonArray));
                    recyclerView.setAdapter(mAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    ArrayList jsonArrayHandling(JSONArray jsonArray) throws JSONException {

        ArrayList tmp = new ArrayList();
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                tmp.add(jsonArray.getJSONObject(i).getString("0"));
            }
        } else {
            return null;
        }

        return tmp;
    }


}
