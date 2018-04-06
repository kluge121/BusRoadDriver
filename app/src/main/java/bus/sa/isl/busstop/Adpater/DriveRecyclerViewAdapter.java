package bus.sa.isl.busstop.Adpater;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bus.sa.isl.busstop.Activity.DrivingActivity;
import bus.sa.isl.busstop.Activity.MainActivity;
import bus.sa.isl.busstop.Entity.DriveEntity;
import bus.sa.isl.busstop.Entity.LineEntity;
import bus.sa.isl.busstop.Entity.NoticeEntity;
import bus.sa.isl.busstop.Fragment.MainTab3;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.MainContext;

/**
 * Created by alstn on 2017-07-26.
 */

public class DriveRecyclerViewAdapter extends RecyclerView.Adapter<DriveRecyclerViewAdapter.ViewHolder> {

    private ArrayList<LineEntity> arrayList = new ArrayList<LineEntity>();
    Context mContext;

    public DriveRecyclerViewAdapter(ArrayList<LineEntity> arrayList, Context context) {
        this.arrayList = arrayList;
        this.mContext = context;
    }

    @Override
    public DriveRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_drive, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DriveRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.setmView(arrayList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayList.get(position).getIsDriving()) {
                    showStartDriveDialog(position);
                } else {

                    Toast.makeText(view.getContext(), "이미 운행중인 노선입니다.", Toast.LENGTH_SHORT).show();
                }

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
        final TextView itemState;
        final String s[] = {"운행가능", "운행중"};


        ViewHolder(View View) {
            super(View);
            mView = View;
            itemTitle = mView.findViewById(R.id.drive_title);
            itemWay = mView.findViewById(R.id.drive_way);
            itemStateCircle = mView.findViewById(R.id.drive_green);
            itemState = mView.findViewById(R.id.drive_state);
        }

        void setmView(LineEntity data) {
            itemTitle.setText(data.getStrLineTitle());
            itemWay.setText(data.getStrLineWay());
            if (data.getLineState() == 0) {
                this.itemStateCircle.setImageResource(R.drawable.emptygreen);
                itemState.setText("운행가능");
            } else {
                this.itemStateCircle.setImageResource(R.drawable.emptyyellow);
                itemState.setText("운행중");
            }


        }
    }


    private void showStartDriveDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("운행을 시작 하시 겠습니까???");
        builder.setPositiveButton("시작", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Intent intent = new Intent(mContext, DrivingActivity.class);
                intent.putExtra("lineNum", arrayList.get(position).getLineNnm());
                mContext.startActivity(intent);


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
