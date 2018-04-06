package bus.sa.isl.busstop.Adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bus.sa.isl.busstop.Entity.DrivingEntity;
import bus.sa.isl.busstop.R;

/**
 * Created by alstn on 2017-08-07.
 */

public class DrivingListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int FIRST_ITEM = 0;
    private final int MIDDLE_ITEM = 1;
    private final int LAST_ITEM = 2;
    private Context mContext;

    private ArrayList<DrivingEntity> arrayList = new ArrayList<DrivingEntity>();

    public ArrayList<DrivingEntity> getArrayList() {
        return arrayList;
    }

    public DrivingListRecyclerViewAdapter(ArrayList<DrivingEntity> list, Context context) {
        this.arrayList = list;
        this.mContext = context;
//        notifyDataSetChanged();
    }

    public void setNowLocationImage(int now, int last) {
        if (last != -1) {
            arrayList.get(last).setIsLocation(0);
        }
        arrayList.get(now).setIsLocation(1);
        notifyDataSetChanged();
        //Toast.makeText(mContext, "어댑터 업데이트 메소드 호출", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemViewType(int position) {
        switch (arrayList.get(position).getItemType()) {
            case FIRST_ITEM:
                return FIRST_ITEM;
            case MIDDLE_ITEM:
                return MIDDLE_ITEM;
            case LAST_ITEM:
                return LAST_ITEM;
            default:
        }

        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case FIRST_ITEM:
                v = inflater.inflate(R.layout.recyclerview_item_driving_first, parent, false);
                return new CustomViewHolder(v, FIRST_ITEM);
            case MIDDLE_ITEM:
                v = inflater.inflate(R.layout.recyclerview_item_driving_middle, parent, false);
                return new CustomViewHolder(v, MIDDLE_ITEM);
            case LAST_ITEM:
                v = inflater.inflate(R.layout.recyclerview_item_driving_last, parent, false);
                return new CustomViewHolder(v, LAST_ITEM);

        }
        Log.e("DrivingListRecycler", "onCreateViewHolder 실패");
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case FIRST_ITEM:
                CustomViewHolder firstViewHolder = (CustomViewHolder) holder;
                firstViewHolder.setView(arrayList.get(position));
                break;
            case MIDDLE_ITEM:
                CustomViewHolder middleViewHolder = (CustomViewHolder) holder;
                middleViewHolder.setView(arrayList.get(position));
                break;
            case LAST_ITEM:
                CustomViewHolder lastViewHolder = (CustomViewHolder) holder;
                lastViewHolder.setView(arrayList.get(position));
                break;
            default:
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView busStopNmae;
        ImageView reserverCircle;
        ImageView nowLocation;

        public CustomViewHolder(View itemView, int viewType) {
            super(itemView);
            switch (viewType) {
                case FIRST_ITEM:
                    busStopNmae = itemView.findViewById(R.id.driving_list_title_f);
                    reserverCircle = itemView.findViewById(R.id.driving_list_circle_f);
                    nowLocation = itemView.findViewById(R.id.driving_list_location_f);
                    break;
                case MIDDLE_ITEM:
                    busStopNmae = itemView.findViewById(R.id.driving_list_title_m);
                    reserverCircle = itemView.findViewById(R.id.driving_list_circle_m);
                    nowLocation = itemView.findViewById(R.id.driving_list_location_m);
                    break;
                case LAST_ITEM:
                    busStopNmae = itemView.findViewById(R.id.driving_list_title_l);
                    reserverCircle = itemView.findViewById(R.id.driving_list_circle_l);
                    nowLocation = itemView.findViewById(R.id.driving_list_location_l);
                    break;
            }
        }

        public void setView(DrivingEntity data) {
            busStopNmae.setText(data.getStrBusStopName());
            if (data.getIsLocation() == 1) {
                nowLocation.setVisibility(View.VISIBLE);
            } else {
                nowLocation.setVisibility(View.INVISIBLE);
            }
        }
    }


}
