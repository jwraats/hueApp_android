package nl.jackevers.jwraats.hueapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jwraats on 03/11/15.
 * http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
 */
public class HueAdapter extends ArrayAdapter<HueLight> {

    Context context;
    int layoutResourceId;
    private ArrayList<HueLight> data = new ArrayList<HueLight>();

    public void setData(ArrayList<HueLight> data){
        this.data = data;
    }

    public HueAdapter(Context context, int layoutResourceId, ArrayList<HueLight> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        HueLightHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new HueLightHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (HueLightHolder)row.getTag();
        }

        HueLight hueLight = data.get(position);
        holder.txtTitle.setText(hueLight.lightName);

        return row;
    }

    static class HueLightHolder
    {
        TextView txtTitle;
    }
}
