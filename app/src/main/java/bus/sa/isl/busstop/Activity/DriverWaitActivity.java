package bus.sa.isl.busstop.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
 * Created by baeminsu on 2017. 9. 28..
 */

public class DriverWaitActivity extends Font {


    TextView tv;
    Button cancleBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_wait_activity);

        tv = (TextView) findViewById(R.id.wait_tv);
        cancleBtn = (Button) findViewById(R.id.join_cancle);

        String affiliation = getIntent().getStringExtra("affiliation");
        tv.setText(affiliation + " 소속 가입 대기중입니다.");

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncCancle().execute();

            }
        });
    }

    class AsyncCancle extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();




            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(getApplicationContext(), DriverSearchActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
