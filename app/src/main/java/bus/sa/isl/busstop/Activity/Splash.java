package bus.sa.isl.busstop.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import bus.sa.isl.busstop.Entity.LoginEntity;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.Font;
import bus.sa.isl.busstop.Set.PropertyManager;
import bus.sa.isl.busstop.TestActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by alstn on 2017-09-07.
 */

public class Splash extends Activity {

    Handler handler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final PropertyManager propertyManager = PropertyManager.getInstance();


        final boolean login = propertyManager.getAutoLogin();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (login) {
                    LoginEntity loginEntity = new LoginEntity(propertyManager.getID(), propertyManager.getPass());
                    new AsyncLogin().execute(loginEntity);
                } else {
                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    class AsyncLogin extends AsyncTask<LoginEntity, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(Splash.this);

        LoginEntity tmpLoginEntity;
        JSONObject responseJson;

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
                String strResponse = response.body().string();
                responseJson = new JSONObject(strResponse);
                return responseJson.getString("message");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && s.equals("로그인 성공")) {
                PropertyManager propertyManager = PropertyManager.getInstance();
                try {
                    propertyManager.setAutoLogin(tmpLoginEntity.getID(), tmpLoginEntity.getPASS(), 1, responseJson.getInt("type"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    int wait = responseJson.getInt("wait");
                    propertyManager.setAffiliation(responseJson.getString("affiliation"));
                    Intent intent;

                    if (wait == 1 || wait == 3) {
                        intent = new Intent(Splash.this, MainActivity.class);
                        intent.putExtra("affiliation", responseJson.getString("affiliation"));
                        startActivity(intent);
                    } else if (wait == 2) {
                        intent = new Intent(Splash.this, DriverWaitActivity.class);
                        intent.putExtra("affiliation", responseJson.getString("affiliation"));
                        startActivity(intent);
                    } else if (wait == 4) {
                        intent = new Intent(Splash.this, DriverSearchActivity.class);
                        startActivity(intent);
                    }
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Intent intent = new Intent(Splash.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            progressDialog.dismiss();
        }
    }
}
