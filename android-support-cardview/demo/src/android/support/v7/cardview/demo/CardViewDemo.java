package android.support.v7.cardview.demo;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.util.Log;

import java.util.ArrayList;

public class CardViewDemo extends Activity {

    private final static String TAG = CardViewDemo.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_card_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
