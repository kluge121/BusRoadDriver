package bus.sa.isl.busstop.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import bus.sa.isl.busstop.Activity.AckDriverActivity;
import bus.sa.isl.busstop.Activity.DriverListActivity;
import bus.sa.isl.busstop.Activity.DriverSearchActivity;
import bus.sa.isl.busstop.Activity.LoginActivity;
import bus.sa.isl.busstop.Adpater.NoitceRecyclerViewAdapter;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.PropertyManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by alstn on 2017-07-24.
 */

public class MainTab4 extends Fragment implements View.OnClickListener {

    PropertyManager pm = PropertyManager.getInstance();
    CardView ackDrive;
    CardView affSec;
    CardView logout;
    CardView driverList;

    public static MainTab4 newInstance() {
        return new MainTab4();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab4, container, false);


        ackDrive = v.findViewById(R.id.master_driver_ack);
        affSec = v.findViewById(R.id.drive_aff_sec);
        logout = v.findViewById(R.id.logout);
        driverList = v.findViewById(R.id.driver_list);


        ackDrive.setOnClickListener(this);
        affSec.setOnClickListener(this);
        logout.setOnClickListener(this);
        driverList.setOnClickListener(this);

        if (pm.getType() == 1) {
            affSec.setVisibility(View.GONE);
        } else if (pm.getType() == 2) {
            ackDrive.setVisibility(View.GONE);
        }
        return v;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.master_driver_ack:
                Intent intent = new Intent(getContext(), AckDriverActivity.class);
                startActivity(intent);
                break;
            case R.id.drive_aff_sec:
                showDialog();
                break;
            case R.id.logout:
                Intent logout = new Intent(getContext(), LoginActivity.class);
                pm.setLogout();
                startActivity(logout);
                getActivity().finish();
                break;
            case R.id.driver_list:
                Intent list = new Intent(getContext(), DriverListActivity.class);
                startActivity(list);
                break;


        }
    }

    class AsyncSec extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("id", PropertyManager.getInstance().getID());
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url("http://52.79.108.145/busstop_server/dirver_sec.php")
                        .post(body)
                        .build();

                Response response = okHttpClient.newCall(request).execute();

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(getContext(), DriverSearchActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("소속을 탈퇴하시겠습니까?");
        builder.setPositiveButton("탈퇴", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                new AsyncSec().execute();


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
}
