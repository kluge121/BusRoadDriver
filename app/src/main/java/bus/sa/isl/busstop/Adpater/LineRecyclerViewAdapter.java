package bus.sa.isl.busstop.Adpater;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import bus.sa.isl.busstop.Activity.DrivingActivity;
import bus.sa.isl.busstop.Activity.LiningDetailListActivity;
import bus.sa.isl.busstop.Entity.LineEntity;
import bus.sa.isl.busstop.Fragment.MainTab2;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.MainContext;
import bus.sa.isl.busstop.Set.PropertyManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by alstn on 2017-07-26.
 */

public class LineRecyclerViewAdapter extends RecyclerView.Adapter<LineRecyclerViewAdapter.ViewHolder> {

    Context mContext;
    private PropertyManager propertyManager = PropertyManager.getInstance();

    private ArrayList<LineEntity> arrayList = new ArrayList<LineEntity>();

    public LineRecyclerViewAdapter(ArrayList<LineEntity> arrayList, Context context) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @Override
    public LineRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_line, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final LineRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.setmView(arrayList.get(position));


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LiningDetailListActivity.class);
                intent.putExtra("lineNum", arrayList.get(position).getLineNnm());
                view.getContext().startActivity(intent);
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (arrayList.get(position).getLineState() == 0) {
                    showDialog(holder.mView, holder.itemTitle.getText().toString());
                }
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
        final TextView itemWay;
        final ImageView itemStateCircle;
        final TextView itemLocation;

        ViewHolder(View View) {
            super(View);
            mView = View;
            itemTitle = mView.findViewById(R.id.line_title);
            itemWay = mView.findViewById(R.id.line_way);
            itemStateCircle = mView.findViewById(R.id.line_green);
            itemLocation = mView.findViewById(R.id.line_location);
        }

        void setmView(LineEntity date) {
            this.itemTitle.setText(date.getStrLineTitle());
            this.itemWay.setText(date.getStrLineWay());
            this.itemLocation.setText(date.getStrLineLocation());

            if (date.getLineState() == 0) {
                //0이면 운행중아님
                this.itemStateCircle.setImageResource(R.drawable.emptyred);
            } else {
                //1이면 운행중
                this.itemStateCircle.setImageResource(R.drawable.emptygreen);
            }


        }
    }


    private void showDialog(final View v, final String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("노선을 삭제하시겠습니까?");
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
                        .url("http://52.79.108.145/busstop_server/line_remove.php")
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
                arrayList = jsonItemHandling(jsonArray);
                notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }

    private ArrayList jsonItemHandling(JSONArray jsonArray) throws JSONException {

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
