package bus.sa.isl.busstop.Adpater;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import bus.sa.isl.busstop.Activity.NoticeDetailActivity;
import bus.sa.isl.busstop.Entity.LineEntity;
import bus.sa.isl.busstop.Entity.NoticeEntity;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.MainContext;
import bus.sa.isl.busstop.Set.PropertyManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by alstn on 2017-07-26.
 */

public class NoitceRecyclerViewAdapter extends RecyclerView.Adapter<NoitceRecyclerViewAdapter.ViewHolder> {

    Context mContext;
    PropertyManager propertyManager = PropertyManager.getInstance();

    private ArrayList<NoticeEntity> arrayList = new ArrayList<NoticeEntity>();

    public NoitceRecyclerViewAdapter(ArrayList<NoticeEntity> arrayList, Context context) {
        this.arrayList = arrayList;
        this.mContext = context;
    }

    @Override
    public NoitceRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_notice, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NoitceRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.setmView(arrayList.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NoticeDetailActivity.class);
                intent.putExtra("name", arrayList.get(position).getStrTitle());
                mContext.startActivity(intent);
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog(holder.mView, holder.itemTitle.getText().toString());

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        final TextView itemTitle;
        final TextView itemDate;
//        final TextView itemCount;

        ViewHolder(View View) {
            super(View);
            mView = View;
            itemTitle = mView.findViewById(R.id.notice_title);
            itemDate = mView.findViewById(R.id.notice_date);
//            itemCount = mView.findViewById(R.id.notice_count);
        }

        void setmView(NoticeEntity date) {
            this.itemTitle.setText(date.getStrTitle());
            this.itemDate.setText(date.getStrDate());
//            this.itemCount.setText("조회수 : "+date.getStrCount()+"회");

        }
    }


    public class AsyncGet extends AsyncTask<String, Void, JSONObject> {

        JSONObject responseJson;
        ProgressDialog progressDialog = new ProgressDialog(mContext);

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
                    .url("http://52.79.108.145/busstop_server/notice_info_get.php?affiliation=" + affiliation)
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
                JSONArray jsonArray = jsonObject.getJSONArray("msg");
                arrayList = jsonItemHandling(jsonArray);
                notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }

    private void showDialog(final View v, final String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("게시물을 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                new AsyncRemovet().execute(str);

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

    private class AsyncRemovet extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("삭제중");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("title", strings[0]);
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url("http://52.79.108.145/busstop_server/notice_remove.php")
                        .post(body)
                        .build();

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
                if (jsonObject.getString("result").equals("success")) {
                    Toast.makeText(MainContext.getContext(), "삭제가 완료되었습니다", Toast.LENGTH_SHORT).show();

                    new AsyncGet().execute(propertyManager.getAffiliation());
                } else {
                    Toast.makeText(MainContext.getContext(), "요류가 발생하였습니다. 다시시도하여 주십시오", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            new AsyncGet().execute(PropertyManager.getInstance().getAffiliation());
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
