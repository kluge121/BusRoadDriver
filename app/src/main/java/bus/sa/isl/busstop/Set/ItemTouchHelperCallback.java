package bus.sa.isl.busstop.Set;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by alstn on 2017-07-31.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    ItemTouchHelperListener listener;

    public ItemTouchHelperCallback(ItemTouchHelperListener listener) {
        this.listener = listener;
    }

    // 각 view에서 어떤 user action이 가능한지 정의
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;


        return makeMovementFlags(dragFlags, 0);
    }


    // user가 item을 drag할 때, ItemTouchHelper가 onMove()를 호출
    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        return listener.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
    }

    // user가 item을 swipe할 때, ItemTouchHelper가 onSwiped()를 호출
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //스와이프 안씀
    }
}


