package com.cherry.mr.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import com.cherry.mr.cherryimageeditor.R;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.disk.NoOpDiskTrimmableRegistry;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;


/**
 * Created by seapeak on 15/12/28.
 * Fresco的图片加载工具类
 */
public class FrescoImgUtils {

    private static Context context;

    //分配的可用内存
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    //使用的缓存数量
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE * 3 / 8;

    //小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    private static final int MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = 20 * ByteConstants.MB;

    //小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    private static final int MAX_SMALL_DISK_LOW_CACHE_SIZE = 60 * ByteConstants.MB;

    //默认图极低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_VERYLOW_SIZE = 30 * ByteConstants.MB;

    //默认图低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_LOW_SIZE = 60 * ByteConstants.MB;

    //默认图磁盘缓存的最大值
    private static final int MAX_DISK_CACHE_SIZE = 100 * ByteConstants.MB;

    //小图所放路径的文件夹名
    private static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "ImagePipelineCacheSmall";

    //默认图所放路径的文件夹名
    private static final String IMAGE_PIPELINE_CACHE_DIR = "ImagePipelineCacheDefault";

    public static ImagePipelineConfig getDefaultImagePipelineConfig(Context context) {

        //内存配置
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE,// 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,// 内存缓存中图片的最大数量。
                MAX_MEMORY_CACHE_SIZE,// 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,// 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE);// 内存缓存中单个图片的最大大小。

        //修改内存图片缓存数量，空间策略（这个方式有点恶心）
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };

        //小图片的磁盘配置
        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig.newBuilder(context).setBaseDirectoryPath(context.getApplicationContext().getCacheDir())//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)//文件夹名
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_SMALL_DISK_LOW_CACHE_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_SMALL_DISK_VERYLOW_CACHE_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .setDiskTrimmableRegistry(NoOpDiskTrimmableRegistry.getInstance())
                .build();

        //默认图片的磁盘配置
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context).setBaseDirectoryPath(Environment.getExternalStorageDirectory().getAbsoluteFile())//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)//文件夹名
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .setDiskTrimmableRegistry(NoOpDiskTrimmableRegistry.getInstance())
                .build();

        //渐进式加载
        ProgressiveJpegConfig pjpegConfig = new ProgressiveJpegConfig() {
            @Override
            public int getNextScanNumberToDecode(int scanNumber) {
                return scanNumber + 2;
            }

            public QualityInfo getQualityInfo(int scanNumber) {
                boolean isGoodEnough = (scanNumber >= 5);
                return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
            }
        };


        //缓存图片配置
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setMemoryTrimmableRegistry(NoOpMemoryTrimmableRegistry.getInstance())
                .setDecodeFileDescriptorEnabled(true)
                .setProgressiveJpegConfig(pjpegConfig)
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true);


        // 就是这段代码，用于清理缓存
        NoOpMemoryTrimmableRegistry.getInstance().registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                        ) {
                    ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                }
            }
        });

        return configBuilder.build();
    }

    @SuppressLint("NewApi")
    public static void Init(Context con) {
        context = con;
        Fresco.initialize(con, getDefaultImagePipelineConfig(con));
    }

    public static void clearCache() {
        // 就是这段代码，用于清理缓存
        NoOpMemoryTrimmableRegistry.getInstance().registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                        ) {
                    ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                }
            }
        });
    }


    /**
     * 显示头像的方法
     *
     * @param uid        用户ID
     * @param draweeView 图片draweeView
     * @param needChange 是否需要拼装数据
     */
    public static void displayAvatar(long uid, DraweeView draweeView, boolean needChange) {
        String url = getavatarurl(uid);
        if (needChange) {
            int x = (int) (Math.random() * 100);
            url = url + "?" + x + "";
        }
        displayAvatarUrl(url, draweeView);
    }

    public static void displayAvatar(long uid, DraweeView draweeView) {
        displayAvatar(uid, draweeView, false);
    }

    public static void displayAvatarUrl(String url, DraweeView draweeView) {

        if (url == null) {
            url = "";
        }

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setRoundingParams(new RoundingParams().setRoundAsCircle(true))
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.avatar_default), ScalingUtils.ScaleType.FIT_CENTER)
                .setFailureImage(context.getResources().getDrawable(R.drawable.avatar_default), ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(320, 320))
                .setImageType(ImageRequest.ImageType.SMALL)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        if (!draweeView.hasHierarchy())
            draweeView.setHierarchy(hierarchy);
        draweeView.setController(controller);
    }

    /**
     * 加载边角的
     * @param url
     * @param draweeView
     */
    public static void displayCornerImage(String url, DraweeView draweeView) {

        if (url == null) {
            url = "";
        }

        float corner = (float) Utils.dpToPx(8, context.getResources());

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setRoundingParams(new RoundingParams().setCornersRadii(corner, corner, corner, corner))
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.avatar_default), ScalingUtils.ScaleType.FIT_CENTER)
                .setFailureImage(context.getResources().getDrawable(R.drawable.avatar_default), ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(350, 350))
                .setImageType(ImageRequest.ImageType.SMALL)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        if (!draweeView.hasHierarchy())
            draweeView.setHierarchy(hierarchy);
        draweeView.setController(controller);
    }

    /**
     * 加载矩形图片时使用
     *
     * @param url
     * @param draweeView
     */
    public static void displayRectImage(String url, DraweeView draweeView) {

        if (url == null) {
            url = "";
        }

        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.default_img), ScalingUtils.ScaleType.FIT_CENTER)
                .setFailureImage(context.getResources().getDrawable(R.drawable.default_img), ScalingUtils.ScaleType.FIT_CENTER)
                .setPressedStateOverlay(new ColorDrawable(0x44000000))
                .build();

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(400, 400))
                .setAutoRotateEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setImageType(ImageRequest.ImageType.DEFAULT)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .build();
        if (!draweeView.hasHierarchy())
            draweeView.setHierarchy(hierarchy);
        draweeView.setController(controller);
    }

    /**
     * 加载矩形大图图片时使用
     *
     * @param url
     * @param draweeView
     */
    public static void displayBigRectImage(String url, DraweeView draweeView, int width, int height, boolean isFitXY) {

        if (url == null) {
            url = "";
        }

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.pic_default), ScalingUtils.ScaleType.FIT_CENTER)
                .setFailureImage(context.getResources().getDrawable(R.drawable.pic_default), ScalingUtils.ScaleType.FIT_CENTER)
                .setPressedStateOverlay(new ColorDrawable(0x44000000))
                .build();

        if (isFitXY) hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setAutoRotateEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(true)
                .setImageType(ImageRequest.ImageType.DEFAULT)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .build();

        if (!draweeView.hasHierarchy())
            draweeView.setHierarchy(hierarchy);
        draweeView.setController(controller);
    }

    public static void displayBigRectImage(String url, DraweeView draweeView) {
        displayBigRectImage(url, draweeView, 500, 500, false);
    }

    public static void displayBigRectImage(String url, DraweeView draweeView, int width, int height){
        displayBigRectImage(url, draweeView, width, height, false);
    }

    public static String getavatarurl(long uid) {
        return "http://icons.xici.net/u" + uid + "/files/photo_m.pic";
    }

    public static void displayTempImage(String url, DraweeView draweeView) {

        if (url == null) {
            url = "";
        }

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.default_img), ScalingUtils.ScaleType.FIT_CENTER)
                .setFailureImage(context.getResources().getDrawable(R.drawable.default_img), ScalingUtils.ScaleType.FIT_CENTER)
                .setPressedStateOverlay(new ColorDrawable(0x44000000))
                .build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(150, 150))
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setImageType(ImageRequest.ImageType.SMALL)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();

        if (!draweeView.hasHierarchy())
            draweeView.setHierarchy(hierarchy);
        draweeView.setController(controller);
    }
}
