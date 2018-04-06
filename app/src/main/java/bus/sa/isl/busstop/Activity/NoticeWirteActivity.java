package bus.sa.isl.busstop.Activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.Font;
import bus.sa.isl.busstop.Set.PropertyManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2017-07-28.
 */

public class NoticeWirteActivity extends Font implements View.OnClickListener {

    ImageButton backBtn;
    EditText editTitle;
    EditText editContent;
    Button registerBtn;
    String affiliation = PropertyManager.getInstance().getAffiliation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);
        setWidget();
    }


    void setWidget() {
        backBtn = (ImageButton) findViewById(R.id.notie_write_backkey);
        backBtn.setOnClickListener(this);
        editTitle = (EditText) findViewById(R.id.notice_write_title);
        editContent = (EditText) findViewById(R.id.content);
        registerBtn = (Button)findViewById(R.id.notice_register);
        registerBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notie_write_backkey:
                finish();
                break;
            case R.id.notice_register:
                new AsyncWrite().execute();
        }
    }


    class AsyncWrite extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(NoticeWirteActivity.this);
        String title;
        String content;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            title = editTitle.getText().toString();
            content = editContent.getText().toString();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("게시글 연결중");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONObject jsonBdoy = new JSONObject();
            OkHttpClient okHttpClient = new OkHttpClient();
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            try {
                jsonBdoy.put("notice_title",title);
                jsonBdoy.put("content",content);
                jsonBdoy.put("affiliation",affiliation);

                RequestBody body = RequestBody.create(JSON,jsonBdoy.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url("http://52.79.108.145/busstop_server/notice_register.php")
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                return strResponse;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Dd","dd");
            try {
                progressDialog.dismiss();
                JSONObject jsonObject = new JSONObject(s);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
