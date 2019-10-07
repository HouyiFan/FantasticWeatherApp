package com.fandev.fantasticweatherapp.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fandev.fantasticweatherapp.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class ForecastViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "ForecastViewPagerAdapte";

    private List<Fragment> fragments;
    private Context context;

    public ForecastViewPagerAdapter(Context context, @NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object item = super.instantiateItem(container, position);
        Log.d(TAG, "instantiateItem: " + position);
//        if (position == 0) {
//            Fragment fragment = (Fragment)super.instantiateItem(container, position);
//            View view = fragment.getView();
//            MaterialCardView cardView = view.findViewById(R.id.card_view);
//
//        }
//        ViewPager viewPager = container.findViewById(R.id.view_pager);
//        RelativeLayout relativeLayout = container.findViewById(R.id.forecast_daily_container);
//        MaterialCardView cardView = container.findViewById(R.id.card_view);
//        ShowcaseConfig config = new ShowcaseConfig();
//        config.setDelay(500); // half second between each showcase view
//        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence((Activity)context, "forecast_weather_showcase");
//        sequence.setConfig(config);
//        sequence.addSequenceItem(container.findViewById(R.id.view_pager),
//                "Here is the detailed weather info.", "GOT IT");
//        sequence.addSequenceItem(container.findViewById(R.id.forecast_hourly_recyclerview),
//                "You can scroll down to view the weather of each 3 hours of this day.", "GOT IT");
//        sequence.addSequenceItem(container.findViewById(R.id.view_pager),
//                "And you can swipe it to check another day's weather. Enjoy it!", "GOT IT");
//        sequence.start();
//        showIntro("Here is the detailed weather info of today",
//                container.findViewById(R.id.view_pager),
//                "daily_forecast_showcase");
//        showIntro("You can scroll down to view the weather of each 3 hours of this day.",
//                container.findViewById(R.id.forecast_hourly_recyclerview),
//                "forecast_hourly_recycler_view");
//        showIntro("And you can swipe it to check another day's weather. Enjoy it!",
//                container.findViewById(R.id.view_pager),
//                "view_pager_showcase");
        return item;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }


    private void showIntro(String text, View view, String showCaseID) {
        new MaterialShowcaseView.Builder((Activity)context)
                .setTarget(view)
                .setDismissText("GOT IT")
                .setContentText(text)
                .setDelay(1500) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(showCaseID) // provide a unique ID used to ensure it is only shown once
                .show();
    }
}
