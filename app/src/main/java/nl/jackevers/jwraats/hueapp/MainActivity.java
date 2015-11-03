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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Do stuff on the restApi
        HueRestfull hueRest = HueRestfull.getInstance(this.getApplicationContext(), this);
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
                    adapter = new HueAdapter(MainActivity.this, R.layout.item_row, data);

                    //Creating the view
                    listView = (ListView) findViewById(R.id.listView1);
                    View header = (View) getLayoutInflater().inflate(R.layout.header_row, null);
                    listView.addHeaderView(header);
                    listView.setAdapter(adapter);

                    //http://stackoverflow.com/questions/17462372/android-java-lang-classcastexception-android-widget-linearlayout-cannot-be-cast
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(view != null && view.findViewById(R.id.txtTitle) != null){
                                String product = ((TextView) view.findViewById(R.id.txtTitle)).getText().toString();

                            }
                            System.out.println(position);
                        }
                    });
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }


}
