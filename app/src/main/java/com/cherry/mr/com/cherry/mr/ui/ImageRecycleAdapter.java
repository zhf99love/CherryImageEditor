package com.cherry.mr.com.cherry.mr.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cherry.mr.cherryimageeditor.CherryApp;
import com.cherry.mr.cherryimageeditor.ImageBean;
import com.cherry.mr.cherryimageeditor.R;
import com.cherry.mr.utils.FrescoImgUtils;
import com.cherry.mr.utils.Utils;
import com.facebook.drawee.view.DraweeView;

import java.util.List;

/**
 * Created by seapeak on 16/3/18.
 */
public class ImageRecycleAdapter extends RecyclerView.Adapter<ImageRecycleAdapter.ImageHolder> {

    List<ImageBean> listBean;
    int[] imageHeight;
    Context context;

    public ImageRecycleAdapter(Context context, List<ImageBean> listBean) {
        this.context = context;
        this.listBean = listBean;
        imageHeight = new int[500];
    }

    public void setData(List<ImageBean> listBean) {
        this.listBean = listBean;
        notifyDataSetChanged();
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.recycle_image_item, null);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, final int position) {
        if (imageHeight[position] == 0)
            imageHeight[position] = getHeight();

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.item_pic.getLayoutParams();
        lp.height = imageHeight[position];
        holder.item_pic.setLayoutParams(lp);
        FrescoImgUtils.displayRectImage("file://" + listBean.get(position).path, holder.item_pic);

        holder.item_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, ImageDetailActivity.class);
                in.putExtra("position", position);
                context.startActivity(in);
            }
        });
    }

    //随机产生高度
    private int getHeight() {
        int height = Utils.dpToPx((150 + (int) (Math.random() * 80)), context.getResources());
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
