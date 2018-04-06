package bus.sa.isl.busstop.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import bus.sa.isl.busstop.Entity.LoginEntity;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.Font;
import bus.sa.isl.busstop.Set.PropertyManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2017-07-25.
 */

public class LoginActivity extends Font {


    private EditText id;
    private EditText pass;
    private Button login;
    private TextView join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (EditText) findViewById(R.id.editText_id);
        pass = (EditText) findViewById(R.id.editText_pass);
        login = (Button) findViewById(R.id.button_login);
        join = (TextView) findViewById(R.id.join_signUp);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncLogin().execute(new LoginEntity(id.getText().toString(), pass.getText().toString()));
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

    }

    private class AsyncLogin extends AsyncTask<LoginEntity, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        JSONObject responseJson;
        LoginEntity tmpLoginEntity;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("로그인 중");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(LoginEntity... loginEntities) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            OkHttpClient okHttpClient = new OkHttpClient();

            try {
                tmpLoginEntity = loginEntities[0];
                jsonObject.put("id", tmpLoginEntity.getID());
                jsonObject.put("pass", tmpLoginEntity.getPASS());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("http://52.79.108.145/busstop_server/login.php")
                    .post(body)
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                if (response.body() != null) {
                    String strResponse = response.body().string();
                    responseJson = new JSONObject(strResponse);
                    return responseJson.getString("message");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (s == null) {
                Toast.makeText(getApplicationContext(), "네트워크 연결상태를 확인해주세요", Toast.LENGTH_LONG).show();
            } else if (s.equals("로그인 성공")) {

                try {

                    int wait = responseJson.getInt("wait");
                    PropertyManager propertyManager = PropertyManager.getInstance();
                    propertyManager.setAutoLogin(tmpLoginEntity.getID(), tmpLoginEntity.getPASS(), 1, responseJson.getInt("type"));
                    propertyManager.setAffiliation(responseJson.getString("affiliation"));
                    Intent intent;
                    if (wait == 1 || wait == 3) {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("affiliation", responseJson.getString("affiliation"));
                        startActivity(intent);

                    } else if (wait == 2) {
                        intent = new Intent(LoginActivity.this, DriverWaitActivity.class);
                        intent.putExtra("affiliation", responseJson.getString("affiliation"));
                        startActivity(intent);
                    } else if (wait == 4) {
                        intent = new Intent(LoginActivity.this, DriverSearchActivity.class);
                        startActivity(intent);
                    }
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            progressDialog.dismiss();
        }
    }

}
