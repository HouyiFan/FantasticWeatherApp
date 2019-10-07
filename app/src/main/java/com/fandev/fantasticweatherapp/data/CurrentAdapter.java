package com.fandev.fantasticweatherapp.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fandev.fantasticweatherapp.ForecastActivity;
import com.fandev.fantasticweatherapp.R;
import com.fandev.fantasticweatherapp.model.Current;
import com.fandev.fantasticweatherapp.util.Prefs;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class CurrentAdapter extends RecyclerView.Adapter<CurrentAdapter.CurrentHolder> implements ItemTouchHelperAdapter {
    private static final String TAG = "CurrentAdapter";

    private Context context;
    private ArrayList<Current> currents;

    public CurrentAdapter(Context context, ArrayList<Current> currents) {
        this.context = context;
        this.currents = currents;
    }

    public static class CurrentHolder extends RecyclerView.ViewHolder {
        private TextView cityName, curTemp, curDate, curDescription;
        private ImageView curWeatherIcon;
        private View view;

        public CurrentHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            cityName = itemView.findViewById(R.id.current_city_name);
            curTemp = itemView.findViewById(R.id.current_temp);
//            curDate = itemView.findViewById(R.id.current_date);
            curDescription = itemView.findViewById(R.id.current_weather_description);
//            curWeatherIcon = itemView.findViewById(R.id.current_weather_icon);
        }

        public void setDetails(Current currentWeather, Context context) {
            cityName.setText(currentWeather.getCityName());
            curTemp.setText(currentWeather.getCurTemp());
//            curDate.setText(currentWeather.getDate());
            curDescription.setText(currentWeather.getDescription());
//            String currentWeatherIconUri = "http://openweathermap.org/img/wn/" + currentWeather.getIcon()
//                    + ".png";
//            Glide.with(view).load(currentWeatherIconUri).
//                    apply(new RequestOptions().override(100, 50)).into(curWeatherIcon);
        }
    }

    @NonNull
    @Override
    public CurrentHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.current_item, parent, false);
        final CurrentHolder viewHolder = new CurrentHolder(view);
        showIntro("Now you can see its basic weather info.\n\n" +
                        "Press the card to show its upcoming five days weather detailed info.\n\n"
                        + "And you can swipe it to delete or drag it to change the order of these cards.",
                    view,
                "recycler_view_item_showcase");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Current currentWeather = currents.get(position);
                Log.d(TAG, "onClick: " + currentWeather.getCityName());
                Intent intent = new Intent(context, ForecastActivity.class);
                intent.putExtra("cityList", currents);
                intent.putExtra("selectedCityName", currentWeather.getCityName());
                intent.putExtra("selectedPhotoRef", currentWeather.getPhotoRef());
                Log.d(TAG, "photoRef: " + currentWeather.getPhotoRef());
                if(context instanceof Activity) {
                    context.startActivity(intent);
                }
                ((Activity)context).finish();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentHolder holder, int position) {
        Current current = currents.get(position);
        holder.setDetails(current, context);
    }

    @Override
    public int getItemCount() {
        return currents.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(currents, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(currents, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        Prefs prefs = new Prefs(context);
        prefs.setCityList(currents);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        currents.remove(position);
        Prefs prefs = new Prefs(context);
        prefs.setCityList(currents);
        notifyItemRemoved(position);
    }

    private void showIntro(String text, View view, String showCaseID) {
        new MaterialShowcaseView.Builder((Activity)context)
                .setTarget(view)
                .setContentText(text)
                .setDismissOnTouch(true)
                .setDismissOnTargetTouch(true)
                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(showCaseID) // provide a unique ID used to ensure it is only shown once
                .show();
    }
}
