package com.cherry.mr.cherryimageeditor;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cherry.mr.utils.FrescoImgUtils;
import com.cherry.mr.utils.Utils;
import com.facebook.drawee.view.DraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seapeak on 16/3/18.
 */
public class ImageRecycleAdapter extends RecyclerView.Adapter<ImageRecycleAdapter.ImageHolder> {

    List<ImageBean> listBean;
    Context context;

    public ImageRecycleAdapter(Context context, List<ImageBean> listBean) {
        this.context = context;
        this.listBean = listBean;
    }

    public void setData(List<ImageBean> listBean) {
        this.listBean = new ArrayList<>();
        notifyDataSetChanged();
        this.listBean = listBean;
        notifyItemRangeInserted(0, listBean.size());
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.recycle_image_item, null);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, final int position) {
        if (listBean.get(position).imageHeight == 0)
            listBean.get(position).imageHeight = getHeight();

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.item_pic.getLayoutParams();
        lp.height = listBean.get(position).imageHeight;
        holder.item_pic.setLayoutParams(lp);
        FrescoImgUtils.displayBigRectImage("file://" + listBean.get(position).path, holder.item_pic);

        holder.item_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, MainActivity.class);
                in.putExtra("pic", listBean.get(position).path);
                CherryApp.imageCache = holder.item_pic.getTopLevelDrawable();
                context.startActivity(in, ActivityOptions.makeSceneTransitionAnimation((Activity) context, ((Activity) context).findViewById(R.id.fab), "shareName").toBundle());
            }
        });
    }

    //随机产生高度
    private int getHeight() {
        int height = Utils.dpToPx(100 + (int) (Math.random() * 100), context.getResources());
        return height;
    }

    @Override
    public int getItemCount() {
        return listBean.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder{

        final DraweeView item_pic;

        public ImageHolder(View itemView) {
            super(itemView);
            item_pic = (DraweeView) itemView.findViewById(R.id.item_pic);
        }
    }
}
