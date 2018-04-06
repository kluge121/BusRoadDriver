package bus.sa.isl.busstop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import bus.sa.isl.busstop.Activity.DrivingActivity;
import bus.sa.isl.busstop.Activity.JoinActivity;
import bus.sa.isl.busstop.Activity.LoginActivity;
import bus.sa.isl.busstop.Activity.MainActivity;
import bus.sa.isl.busstop.Activity.MapsActivity;
import bus.sa.isl.busstop.Activity.NoticeDetailActivity;
import bus.sa.isl.busstop.Activity.NoticeWirteActivity;
import bus.sa.isl.busstop.Set.Font;

/**
 * Created by alstn on 2017-08-15.
 */

public class TestActivity extends Font implements View.OnClickListener {


    Button main;
    Button map;
    Button login;
    Button join;
    Button driving;
    Button noticeWrite;
    Button noticeDetail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        main  = (Button)findViewById(R.id.btnMain);
        map = (Button)findViewById(R.id.btnMap);
        login = (Button)findViewById(R.id.btnLogin);
        join = (Button)findViewById(R.id.btnJoin);
        driving = (Button)findViewById(R.id.btnDriving);
        noticeWrite = (Button)findViewById(R.id.btnNotice);
        noticeDetail=(Button)findViewById(R.id.btnNoitceDetail);

        main.setOnClickListener(this);
        map.setOnClickListener(this);
        login.setOnClickListener(this);
        join.setOnClickListener(this);
        driving.setOnClickListener(this);
        noticeWrite.setOnClickListener(this);
        noticeDetail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnMain:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btnMap:
                intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.btnLogin:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btnJoin:
                intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
                break;
            case R.id.btnDriving:
                intent = new Intent(getApplicationContext(), DrivingActivity.class);
                startActivity(intent);
                break;
            case R.id.btnNotice:
                intent = new Intent(getApplicationContext(), NoticeWirteActivity.class);
                startActivity(intent);
                break;
            case R.id.btnNoitceDetail:
                intent = new Intent(getApplicationContext(), NoticeDetailActivity.class);
                startActivity(intent);
                break;
        }
    }
}
