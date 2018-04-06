package bus.sa.isl.busstop.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
 * Created by baeminsu on 2017. 10. 5..
 */

public class DriverSearchActivity extends Font {

    ImageButton searchBtn;
    Button requestBtn;
    EditText editAff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_search);
        editAff = (EditText) findViewById(R.id.edit_aff);
        searchBtn = (ImageButton) findViewById(R.id.aff_search);
        requestBtn = (Button) findViewById(R.id.request_btn);

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestBtn.setEnabled(false);
                new AsnycUpdate().execute(editAff.getText().toString());

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainContext.getContext(), FindDialogActivity.class);
                intent.putExtra("keyword", editAff.getText() + "");
                startActivityForResult(intent, 100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case 100:
                String tmp = data.getStringExtra("Affiliation");
                editAff.setText(tmp);
                Log.e("체크", tmp);
        }
    }

    class AsnycUpdate extends AsyncTask<String, Void, Void> {

        Response response;
        String aff;

        @Override
        protected Void doInBackground(String... strings) {
            aff = strings[0];
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", PropertyManager.getInstance().getID());
                jsonObject.put("aff", strings[0]);
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                Request request = new Request.Builder()
                        .post(body)
                        .url("http://52.79.108.145/busstop_server/driver_new_request.php")
                        .build();

                response = okHttpClient.newCall(request).execute();
            } catch (JSONException | IOException e) {
                Log.e("체크1", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            JSONObject jsonObject;

            try {
                String strResponse = response.body().string();
                jsonObject = new JSONObject(strResponse);
                String result = jsonObject.getString("msg");


                if (result.equals("성공")) {
                    Intent intent = new Intent(getApplicationContext(), DriverWaitActivity.class);
                    intent.putExtra("affiliation", aff);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainContext.getContext(), "일치하는 소속이 없습니다", Toast.LENGTH_SHORT).show();
                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e("체크2", e.toString());
            }
            requestBtn.setEnabled(true);

        }
    }

}

