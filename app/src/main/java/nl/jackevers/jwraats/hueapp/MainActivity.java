package nl.jackevers.jwraats.hueapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private HueAdapter adapter;
    private ArrayList<HueLight> data = new ArrayList<HueLight>();
    private HueRestfull hueRest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Do stuff on the restApi
        hueRest = HueRestfull.getInstance(this.getApplicationContext(), this);
        hueRest.discoverBridge();
    }

    public void changeData(ArrayList<HueLight> newData){
        this.data.clear();
        this.data.addAll(newData);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    //Creating adapter
                    adapter = new HueAdapter(MainActivity.this, R.layout.item_row, data, hueRest);

                    //Creating the view
                    listView = (ListView) findViewById(R.id.listView1);
                    View header = (View) getLayoutInflater().inflate(R.layout.header_row, null);
                    //listView.addHeaderView(header);
                    listView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }


}
