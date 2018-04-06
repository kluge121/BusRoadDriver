package bus.sa.isl.busstop.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import bus.sa.isl.busstop.Entity.JoinEntity;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.MainContext;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by alstn on 2017-08-07.
 */

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView masterC;
    ImageView driverC;
    TextView signUpBtn;
    ImageButton searchBtn;
    EditText id;
    EditText pass;
    EditText affiliation;
    int iType;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case 100:
                String tmp = data.getStringExtra("Affiliation");
                affiliation.setText(tmp);
                Log.e("체크", tmp);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint);
        setWidget();
        masterC.setImageResource(R.drawable.choice2);
        driverC.setImageResource(R.drawable.choice);
        searchBtn.setVisibility(View.INVISIBLE);
        iType = 1;


    }

    void setWidget() {
        masterC = (ImageView) findViewById(R.id.master_choice);
        driverC = (ImageView) findViewById(R.id.driver_choice);
        signUpBtn = (TextView) findViewById(R.id.join_signUp);
        id = (EditText) findViewById(R.id.join_id);
        pass = (EditText) findViewById(R.id.join_pass);
        affiliation = (EditText) findViewById(R.id.join_aff);
        searchBtn = (ImageButton) findViewById(R.id.searchbtn);
        searchBtn.setOnClickListener(this);
        masterC.setOnClickListener(this);
        driverC.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.master_choice:
                masterC.setImageResource(R.drawable.choice2);
                driverC.setImageResource(R.drawable.choice);
                searchBtn.setVisibility(View.INVISIBLE);
                affiliation.setText("");
                affiliation.setHint("생성할 소속명을 입력");
                iType = 1;
                break;
            case R.id.driver_choice:
                masterC.setImageResource(R.drawable.choice);
                driverC.setImageResource(R.drawable.choice2);
                searchBtn.setVisibility(View.VISIBLE);
                affiliation.setText("");
                affiliation.setHint("검색할 소속명을 입력");
                iType = 2;
                break;
            case R.id.join_signUp:
                JoinEntity entity = new JoinEntity(id.getText().toString(), pass.getText().toString(), affiliation.getText().toString(), iType);
                new AsyncJoin().execute(entity);
                break;
            case R.id.searchbtn:
                if (iType == 2) {
                    Intent intent = new Intent(MainContext.getContext(), FindDialogActivity.class);
                    intent.putExtra("keyword", affiliation.getText() + "");
                    startActivityForResult(intent, 100);
                }

            default:
        }


    }

    private class AsyncJoin extends AsyncTask<JoinEntity, Void, String> {


        ProgressDialog progressDialog = new ProgressDialog(JoinActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("회원가입 중");
            progressDialog.show();
        }


        @Override
        protected String doInBackground(JoinEntity... joinEntities) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


            JoinEntity entity = joinEntities[0];
            JSONObject jsonObject = new JSONObject();
            OkHttpClient okHttpClient = new OkHttpClient();

            try {
                jsonObject.put("id", entity.getId());
                jsonObject.put("pass", entity.getPass());
                jsonObject.put("aff", entity.getAffiliation());
                jsonObject.put("type", entity.getType());

            } catch (JSONException e) {
                e.printStackTrace();
            }


            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("http://52.79.108.145/busstop_server/join.php")
                    .post(body)
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String strResponse = response.body().string();
                JSONObject responseJson = new JSONObject(strResponse);
                return responseJson.getString("message");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            if (s.equals("가입 성공")) {
                finish();
            }

        }
    }


}
