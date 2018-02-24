package solutions.farsight.pixabayimagesearcher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by Stephen on 2/23/2018.
 */

public class ImageResultRecyclerAdapter extends RecyclerView.Adapter<ImageResultRecyclerAdapter .ViewHolder> {

    private List<ImageResult> items;
    private int itemLayout;

    public ImageResultRecyclerAdapter(List<ImageResult> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        ImageResult item = items.get(position);
        Context context = holder.image.getContext();
        Picasso.with(context).load(item.url).into(holder.image);

        holder.itemView.setTag(item);
        holder.itemView.setMinimumWidth(item.width);
        holder.itemView.setMinimumHeight(item.height);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}