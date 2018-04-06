package bus.sa.isl.busstop.Set;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by alstn on 2017-08-10.
 */

public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {

    private final int divHeight;

    public RecyclerViewDecoration(int divHeight) {
        this.divHeight = divHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = divHeight;
        outRect.bottom = divHeight;
    }
}
