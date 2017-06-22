package shay.example.com.clotheslinemonitor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<String> {

    String[] weatherInfo;
    long[] weatherTime;
    int[] imageRating; // R.drawable clothesitems
    int[] rainAlert;
    Context mContext;


    public MyAdapter(Context context, String[] weatherInfo, int[] weatherImage, int[] rainAlert, long[] weatherTime) {
        super(context, R.layout.listview_item);
        this.weatherInfo = weatherInfo;
        this.weatherTime = weatherTime;
        this.imageRating = weatherImage;
        this.rainAlert = rainAlert;
        this.mContext = context;
    }

    @Override
    public int getCount() { // returns the length of display list
        return UI_DataHolder.getDisplayHours();
    }


    @NonNull

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listViewHolder = new ViewHolder();


        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.listview_item, parent, false);
            listViewHolder.textTime = (TextView) convertView.findViewById(R.id.textViewTime);
            listViewHolder.imageViewClothes = (ImageView) convertView.findViewById(R.id.imageView);
            listViewHolder.textDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }
        String displayTime = Converter.convertTime(weatherTime[position]);
        listViewHolder.textTime.setText(displayTime);
        listViewHolder.imageViewClothes.setImageResource(imageRating[position]);
        listViewHolder.imageViewClothes.setBackgroundResource(rainAlert[position]);
        listViewHolder.textDescription.setText(weatherInfo[position]);
        return convertView;
    }


    static class ViewHolder {
        TextView textTime;
        ImageView imageViewClothes;
        TextView textDescription;
    }
}
