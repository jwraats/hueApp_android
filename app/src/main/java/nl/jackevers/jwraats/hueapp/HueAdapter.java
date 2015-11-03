package nl.jackevers.jwraats.hueapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jwraats on 03/11/15.
 * http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
 */
public class HueAdapter extends ArrayAdapter<HueLight> {

    Context context;
    int layoutResourceId;
    private ArrayList<HueLight> data = new ArrayList<HueLight>();
    private HueRestfull hueRest;

    public void setData(ArrayList<HueLight> data){
        this.data = data;
    }

    public HueAdapter(Context context, int layoutResourceId, ArrayList<HueLight> data, HueRestfull hueRest) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.hueRest = hueRest;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final HueLightHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new HueLightHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.switchOn = (Switch)row.findViewById(R.id.switchOn);

            row.setTag(holder);
        }
        else
        {
            holder = (HueLightHolder)row.getTag();
        }

        HueLight hueLight = data.get(position);
        holder.hueLight = hueLight;
        holder.txtTitle.setText(hueLight.lightName);
        holder.switchOn.setChecked(hueLight.switchLightOn);

        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.hueLight.hue != null){
                    Random rand = new Random();
                    // nextInt is normally exclusive of the top value,
                    // so add 1 to make it inclusive
                    int max = 65535;
                    int min = 0;
                    int randomNum = rand.nextInt((max - min) + 1) + min;
                    holder.hueLight.hue = (double)randomNum;
                    holder.hueLight.sat = (double)255;
                    holder.hueLight.brightness = (double)255;
                }else {
                    if(holder.hueLight.brightness == 255){
                        holder.hueLight.brightness = (double)50;
                    }else{
                        holder.hueLight.brightness = (double)255;
                    }
                }


                hueRest.updateLight(holder.hueLight);
                hueRest.getLights();
            }
        });

        //http://stackoverflow.com/questions/17462372/android-java-lang-classcastexception-android-widget-linearlayout-cannot-be-cast
        holder.switchOn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((Switch) v).isChecked()) {
                    holder.hueLight.switchLightOn = true;
                }
                else{
                    holder.hueLight.switchLightOn = false;
                }
                hueRest.updateLight(holder.hueLight);
                hueRest.getLights();

            }
        });
        return row;
    }

    static class HueLightHolder
    {
        HueLight hueLight;
        TextView txtTitle;
        Switch switchOn;
    }
}
