package com.cherry.mr.com.cherry.mr.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cherry.mr.cherryimageeditor.R;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage3x3ConvolutionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImage3x3TextureSamplingFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageAddBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageAlphaBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBilateralFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBoxBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBulgeDistortionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCGAColorspaceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageChromaKeyBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBalanceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBurnBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorDodgeBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorMatrixFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;

/**
 * Created by seapeak on 16/3/23.
 */
public abstract class FilterRecycleAdapter extends RecyclerView.Adapter<FilterRecycleAdapter.FilterHolder> {

    private static List<GPUImageFilter> filterList;
    private static List<Bitmap> bitmapList;
    private Context context;

    public FilterRecycleAdapter(Context context) {
        this.context = context;
    }

    abstract void onFilterClick(GPUImageFilter filter);

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_filter_item, parent, false);
        return new FilterHolder(view);
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, final int position) {

        if (bitmapList.get(position) != null) {
            holder.item_pic.setImageBitmap(bitmapList.get(position));
        }

        holder.item_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilterClick(filterList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public static void initFilter(Context context){
        filterList = new ArrayList<>();
        bitmapList = new ArrayList<>();

//        filterList.add(new GPUImage3x3ConvolutionFilter());
//        filterList.add(new GPUImage3x3TextureSamplingFilter());
//        filterList.add(new GPUImageAddBlendFilter());
//        filterList.add(new GPUImageAlphaBlendFilter());
//        filterList.add(new GPUImageBilateralFilter());
        filterList.add(new GPUImageBoxBlurFilter(1.5f));
        filterList.add(new GPUImageBrightnessFilter(0.3f));
        filterList.add(new GPUImageBulgeDistortionFilter(0.4f, 0.5f, new PointF(0.5f, 0.5f)));
        filterList.add(new GPUImageCGAColorspaceFilter());
        filterList.add(new GPUImageChromaKeyBlendFilter());
        filterList.add(new GPUImageColorBalanceFilter());
        filterList.add(new GPUImageColorBlendFilter());
//        filterList.add(new GPUImageColorBurnBlendFilter());
//        filterList.add(new GPUImageColorDodgeBlendFilter());
        filterList.add(new GPUImageColorInvertFilter());
//        filterList.add(new GPUImageColorMatrixFilter());
        filterList.add(new GPUImageContrastFilter());

        for (int i = 0; i < filterList.size(); i++) {
            executeChange(context, filterList.get(i), i);
        }
    }

    /**
     * 更改滤镜方法
     *
     * @param filter 滤镜类型
     */
    private static void executeChange(final Context context, GPUImageFilter filter, final int position) {
        bitmapList.add(position, null);
        AsyncTask<GPUImageFilter, Void, Bitmap> filterTask = new AsyncTask<GPUImageFilter, Void, Bitmap>() {

            GPUImageFilter filter;

            @Override
            protected Bitmap doInBackground(GPUImageFilter... params) {
                try {
                    filter = params[0];

                    Bitmap  bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.show_choice);
                    // 使用GPUImage处理图像
                    GPUImage gpuImage = new GPUImage(context);
                    gpuImage.setImage(bitmap);
                    gpuImage.setFilter(filter);
                    Bitmap getBitmap = gpuImage.getBitmapWithFilterApplied();
                    return getBitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                bitmapList.remove(position);
                bitmapList.add(position, bitmap);
            }
        };
        filterTask.execute(filter);
    }


    public class FilterHolder extends RecyclerView.ViewHolder {

        final ImageView item_pic;

        public FilterHolder(View itemView) {
            super(itemView);
            item_pic = (ImageView) itemView.findViewById(R.id.filter_img);
        }
    }
}
