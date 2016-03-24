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
import android.widget.TextView;

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
import jp.co.cyberagent.android.gpuimage.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDarkenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDifferenceBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDilationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDirectionalSobelEdgeDetectionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDissolveBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDivideBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExclusionBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFalseColorFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHalftoneFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHardLightBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageKuwaharaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLaplacianFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLevelsFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLightenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLinearBurnBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLookupFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLuminosityBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMixBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMultiplyBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageNonMaximumSuppressionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageNormalBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageOpacityFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageOverlayBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBDilationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageScreenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSmoothToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelThresholdFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSoftLightBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSourceOverBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSphereRefractionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSubtractBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSwirlFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageThresholdEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageTransformFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWeakPixelInclusionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceFilter;

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

        holder.filter_num.setText((position + 1) + "");
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

        filterList.add(new GPUImageGaussianBlurFilter(1.5f));   //   //磨砂效果
        filterList.add(new GPUImageBrightnessFilter(0.3f)); //亮度增强
        filterList.add(new GPUImageBulgeDistortionFilter(0.4f, 0.5f, new PointF(0.5f, 0.5f)));
        filterList.add(new GPUImageColorBalanceFilter());   //
        filterList.add(new GPUImageColorBlendFilter());     //

        filterList.add(new GPUImageColorInvertFilter());
        filterList.add(new GPUImageContrastFilter());
        filterList.add(new GPUImageDifferenceBlendFilter());
        filterList.add(new GPUImageDilationFilter());
        filterList.add(new GPUImageDirectionalSobelEdgeDetectionFilter());

        filterList.add(new GPUImageDissolveBlendFilter());
        filterList.add(new GPUImageEmbossFilter(2.0f));     //剧烈程度
        filterList.add(new GPUImageExposureFilter(1.3f));   //曝光
        filterList.add(new GPUImageFalseColorFilter());   //颜色丢失
        filterList.add(new GPUImageGammaFilter());   //

        filterList.add(new GPUImageGrayscaleFilter());   //
        filterList.add(new GPUImageHalftoneFilter());   //
        filterList.add(new GPUImageHazeFilter());   //
        filterList.add(new GPUImageHueBlendFilter());   //
        filterList.add(new GPUImageHueFilter());   //

        filterList.add(new GPUImageKuwaharaFilter());   //
        filterList.add(new GPUImageLaplacianFilter());   //
        filterList.add(new GPUImageMonochromeFilter());   //
        filterList.add(new GPUImageOpacityFilter(0.7f));   //
        filterList.add(new GPUImageOverlayBlendFilter());   //

        filterList.add(new GPUImagePosterizeFilter());   //
        filterList.add(new GPUImageRGBDilationFilter());   //
        filterList.add(new GPUImageSaturationBlendFilter());   //
        filterList.add(new GPUImageSepiaFilter());   //
        filterList.add(new GPUImageSketchFilter());     //

        filterList.add(new GPUImageSmoothToonFilter());   //
        filterList.add(new GPUImageSobelEdgeDetection());   //
        filterList.add(new GPUImageSobelThresholdFilter());   //
        filterList.add(new GPUImageSoftLightBlendFilter());   //
        filterList.add(new GPUImageThresholdEdgeDetection());   //

        filterList.add(new GPUImageToonFilter());   //
        filterList.add(new GPUImageVignetteFilter(new PointF(), new float[] {0.0f, 0.0f, 0.0f}, 0.2f, 1.3f));   //
        filterList.add(new GPUImageWeakPixelInclusionFilter());   //

        for (int i = 0; i < filterList.size(); i++) {
            executeChange(context, filterList.get(i), i);
        }
    }

    /**
     * 更改滤镜方法
     *
     * @param filter 滤镜类型
     */
    private static synchronized void executeChange(final Context context, GPUImageFilter filter, final int position) {
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
        final TextView filter_num;

        public FilterHolder(View itemView) {
            super(itemView);
            item_pic = (ImageView) itemView.findViewById(R.id.filter_img);
            filter_num = (TextView) itemView.findViewById(R.id.filter_num);
        }
    }
}
