package bus.sa.isl.busstop.Adpater;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Iterator;

import bus.sa.isl.busstop.Activity.MapsActivity;
import bus.sa.isl.busstop.Entity.MarkerEntity;
import bus.sa.isl.busstop.Set.ItemTouchHelperListener;
import bus.sa.isl.busstop.R;


/**
 * Created by alstn on 2017-07-31.
 */


public class MarkerListRecyclerViewAdpater extends RecyclerView.Adapter<MarkerListRecyclerViewAdpater.ViewHolder> implements ItemTouchHelperListener {

    private ArrayList<MarkerEntity> arrayList = new ArrayList();
    private OnStartDragListener onStartDragListener;

    private MarekrClickListner marekrClickListner;

    public interface MarekrClickListner {
        void clickSettingCamera(LatLng latLng);
    }

    public void setMarkerClickListner(MarekrClickListner marekrClickListner) {
        this.marekrClickListner = marekrClickListner;
    }


    public void setArrayList(ArrayList<MarkerEntity> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<MarkerEntity> getArrayList() {
        return arrayList;
    }


    public interface OnStartDragListener {
        void onStartDrag(ViewHolder viewHolder);
    }

    public void itemAdd(MarkerEntity data) {
        arrayList.add(data);
        notifyDataSetChanged();
    }

    public void itemRemove(int position) {
        arrayList.remove(position);
        notifyDataSetChanged();
    }

    public int findPosition(LatLng point) {
        int cnt = 0;
        while (cnt < arrayList.size()) {
            if (arrayList.get(cnt).getLatitude() == point.latitude && arrayList.get(cnt).getLongitude() == point.longitude) {
                return cnt;
            }
            cnt++;
        }
        return -1;
    }

    public MarkerListRecyclerViewAdpater(ArrayList<MarkerEntity> markerEntityArrayList, OnStartDragListener startDragListener) {
        this.arrayList = markerEntityArrayList;
        this.onStartDragListener = startDragListener;
    }

    @Override
    public MarkerListRecyclerViewAdpater.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_busstop_list, parent, false);
        return new ViewHolder(v, new CustomEditTextListener());
    } // 이미지화, 메모리에 올리는 작업


    @Override
    public void onBindViewHolder(final MarkerListRecyclerViewAdpater.ViewHolder holder, final int position) {
        holder.markerHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });


        holder.customEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.markerName.setText(arrayList.get(holder.getAdapterPosition()).getMarkerName());
        holder.markerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marekrClickListner.clickSettingCamera(arrayList.get(position).getLocation());
            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override // darg
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < 0 || fromPosition >= arrayList.size() || toPosition < 0 || toPosition >= arrayList.size()) {
            return false;
        }

        MarkerEntity fromItem = arrayList.get(fromPosition);
        arrayList.remove(fromPosition);
        arrayList.add(toPosition, fromItem);
        notifyItemMoved(fromPosition, toPosition);
        mapActivityListUpdate(fromPosition,toPosition);


        return true;
    }

    private void mapActivityListUpdate(int fromPosition, int toPosition){
        Marker fromMarker = MapsActivity.markerArrayList.get(fromPosition);
        MapsActivity.markerArrayList.remove(fromPosition);
        MapsActivity.markerArrayList.add(toPosition,fromMarker);
        Log.e("체크",fromPosition+" : "+ toPosition);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final EditText markerName;
        final TextView markerNumber;
        final ImageView markerHandle;

        CustomEditTextListener customEditTextListener;

        ViewHolder(View View, CustomEditTextListener customEditTextListener) {
            super(View);
            mView = View;
            markerName = mView.findViewById(R.id.markerName);
            markerNumber = mView.findViewById(R.id.markerNumber);
            markerHandle = mView.findViewById(R.id.markerHandle);


            this.customEditTextListener = customEditTextListener;
            markerName.addTextChangedListener(customEditTextListener);
        }

        void setView(MarkerEntity data) {
            // 리스너로 대체

        }

        String getMarkerName() {
            return markerName.getText().toString();
        }

    }

    private class CustomEditTextListener implements TextWatcher {
        private int position;

        void updatePosition(int position) {
            this.position = position;
        }

        @Override

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            arrayList.get(position).setMarkerName(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


}
