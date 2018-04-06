package bus.sa.isl.busstop.Activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.Font;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alstn on 2017-08-27.
 */

public class NoticeDetailActivity extends Font implements View.OnClickListener {
    ImageButton backKey;
    Button editContents;
    TextView title;
    TextView date;
    TextView contents;
    String titleName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        setWidget();
        titleName = getIntent().getStringExtra("name");
        Log.e("체크",titleName);
        new AsyncGet().execute(titleName);
    }

    void setWidget() {
        backKey = (ImageButton) findViewById(R.id.notice_detail_backkey);
        editContents = (Button) findViewById(R.id.notice_detail_edit);
        title = (TextView) findViewById(R.id.notice_detail_title);
        date = (TextView) findViewById(R.id.notice_detail_date);
        contents = (TextView) findViewById(R.id.notice_detail_contents);

        backKey.setOnClickListener(this);
        editContents.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.notice_detail_backkey:
                finish();
                break;
            case R.id.notice_detail_edit:
                break;

        }

    }

    class AsyncGet extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progressDialog = new ProgressDialog(NoticeDetailActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("게시글 연결중");
            progressDialog.show();
        }


        @Override
        protected JSONObject doInBackground(String... strings) {

            OkHttpClient okHttpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://52.79.108.145/busstop_server/notice_detail_get.php?title=" + titleName)
                    .get()
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                JSONObject responseJson = new JSONObject(strResponse);
                return responseJson;

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
                JSONObject tmpJsonObject = jsonArray.getJSONObject(0);
                title.setText(tmpJsonObject.getString("0"));
                date.setText(tmpJsonObject.getString("2"));
                contents.setText(tmpJsonObject.getString("3"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }
}
