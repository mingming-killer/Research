package com.gmail.killer.mingming.themeresearch;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class UILDemo extends Activity {

    private final static String TAG = UILDemo.class.getSimpleName();

    //"http://s.heyemoji.com/themes/
    private final static String THEME_LIST[] = {
        "preview_blueflatex.png",
        "preview_blueflat.png",
        "preview_chinawriting.png",
        "preview_chrismasblue.png",
        "preview_chrismasredex.png",
        "preview_chrismasred.png",
        "preview_colorful.png",
        "preview_daycloud.png",
        "preview_dreamblue.png",
        "preview_dreamcloud.png",
        "preview_gingerbread.png",
        "preview_happynewyearred.png",
        "preview_ics.png",
        "preview_inkpainting.png",
        "preview_kiwi.png",
        "preview_lace.png",
        "preview_leafdrop.png",
        "preview_letterpaper.png",
        "preview_lollipopdark.png",
        "preview_lollipoplight.png",
        "preview_loverbird.png",
        "preview_lovergift.png",
        "preview_loverpink.png",
        "preview_loverpinkround.png",
        "preview_newcolorful.png",
        "preview_newmacaronpink.png",
        "preview_newpeachpink.png",
        "preview_newyearred2.png",
        "preview_nightskycyan.png",
        "preview_nightstar.png",
        "preview_olive.png",
        "preview_peachcolor.png",
        "preview_purpleheart.png",
        "preview_purple.png",
        "preview_rainbow.png",
        "preview_roundblack.png",
        "preview_sap.png",
        "preview_simplewhite.png",
        "preview_trans_dreamredblue.png",
        "preview_trans_electricblue.png",
        "preview_trans_macarons_purple.png",
        "preview_trans_nightsky.png",
        "preview_trans_oilpaint1.png",
        "preview_trans_peacock_blue.png",
        "preview_trans_rustred.png",
        "preview_tuhao.png",
        "preview_tuya.png",
        "preview_valentineday.png",
        "preview_youdreamcolor.png",
    };

    class ThemeInfo {
        public String mPreviewSUrl;
        public String mPkgName;
        public String mName;
    }

    private LayoutInflater mLayoutInflater;
    private int mColNum;
    private int mItemWidth;
    private int mItemHeight;

    private ArrayList<ThemeInfo> mThemeInfos;
    private ListAdapter mListAdapter;
    private GridView mListView;

    class ListAdapter extends BaseAdapter {

        public ListAdapter() {
            super();
        }

        @Override
        public int getCount() {
            int count = mThemeInfos.size() / mColNum;
            if (0 != mThemeInfos.size() % mColNum) {
                count += 1;
            }
            return count;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            if (position >= 0 && position < mThemeInfos.size()) {
                return mThemeInfos.get(position);
            }
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            boolean isFirst = false;
            ImageView previewView = null;
            if (null == convertView) {
                convertView = mLayoutInflater.inflate(R.layout.uil_list_item, null);
                isFirst = true;
            }

            ThemeInfo data = null;
            if (position >= 0 && position < mThemeInfos.size()) {
                data = mThemeInfos.get(position);
            }
            if (null == data || null == data.mPreviewSUrl) {
                Log.w(TAG, "pos: " + position + ", url is null, igore !!");
                return convertView;
            }

            // TODO: user view holder
            previewView = (ImageView) convertView;
            ViewGroup.LayoutParams params = previewView.getLayoutParams();
            if (null == params) {
                params = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            } else {
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = mItemHeight;
            }
            previewView.setLayoutParams(params);

            if (!isFirst) {
                ImageLoader.getInstance().cancelDisplayTask(previewView);
            }
            ImageLoader.getInstance().displayImage(data.mPreviewSUrl, previewView);

            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uil);

        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().destroy();
    }

    private void initData() {
        configImageLoader();

        ThemeInfo info = null;
        mThemeInfos = new ArrayList<ThemeInfo>();
        for (String url : THEME_LIST) {
            info = new ThemeInfo();
            info.mPreviewSUrl = "http://s.heyemoji.com/themes/" + url;
            mThemeInfos.add(info);
        }

        mListAdapter = new ListAdapter();

        Resources res = getResources();
        mColNum = res.getInteger(R.integer.uil_list_col_num);
        mItemWidth = (int) res.getDimension(R.dimen.uil_list_item_width);
        mItemHeight = (int) res.getDimension(R.dimen.uil_list_item_height);
    }

    private void initView() {
        mLayoutInflater = LayoutInflater.from(this);
        mListView = (GridView) findViewById(R.id.uil_list_view);
        mListView.setAdapter(mListAdapter);
    }

    private void configImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
            .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
            .showImageOnFail(R.drawable.ic_error) // resource or drawable
            .resetViewBeforeLoading(false)  // default
            .delayBeforeLoading(1000)
            .cacheInMemory(true) // default
            .cacheOnDisk(true) // default
            .considerExifParams(true) // default
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
            //.decodingOptions(...)
            .displayer(new FadeInBitmapDisplayer(600))
            .handler(new Handler()) // default
            .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
            .memoryCacheExtraOptions(720, 1280) // default = device screen dimensions
            .diskCacheExtraOptions(720, 1280, null)
            //.denyCacheImageMultipleSizesInMemory()
            .memoryCache(new LruMemoryCache(4 * 1024 * 1024))
            .memoryCacheSize(4 * 1024 * 1024)
            .memoryCacheSizePercentage(25) // 25% memory as cache
            .diskCache(new UnlimitedDiscCache(new File("/mnt/sdcard/uil-test/")))
            .diskCacheSize(250 * 1024 * 1024)
            .diskCacheFileCount(500)
            .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
            .imageDownloader(new BaseImageDownloader(this)) // default
            .imageDecoder(new BaseImageDecoder(false)) // default
            //.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
            .defaultDisplayImageOptions(options)
            //.writeDebugLogs()
            .build();

        ImageLoader.getInstance().init(config);
    }

}
