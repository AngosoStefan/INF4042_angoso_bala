package org.esiea.angoso_bala.mdcfever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ThirdActivity extends AppCompatActivity {

    final Context context = this;
    public static final String BIERS_UPDATE = "com.octip.cours.inf4042_11.BIERS_UPDATE";
    public static final String TAG = "GetBiersServices";
    private RecyclerView rv_biere;

    public String comics;

    public RecyclerView getRv_biere() {
        return rv_biere;
    }

    public void setRv_biere(RecyclerView rv_biere) {
        this.rv_biere = rv_biere;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GetBiersServices.startActionBiers(context);

        setContentView(R.layout.activity_third);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IntentFilter intentFilter = new IntentFilter(BIERS_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BierUpdate(), intentFilter);


        rv_biere = (RecyclerView) findViewById(R.id.rv_biere);
        rv_biere.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        JSONArray bieres = getBiersFromFile();

        BiersAdapter ba = new BiersAdapter(bieres);
        rv_biere.setAdapter(ba);
    }




    /*@Override
    public void onClick(View v) {
        // define the button that invoked the listener by id
        switch (v.getId()) {
            case R.id.b_dc:
                // ОК button
                Toast.makeText(SecondActivity.this,
                        "Yes!", Toast.LENGTH_SHORT).show();
                comics = "DC Comics";
                break;
            case R.id.b_marvel:
                // Cancel button
                Toast.makeText(SecondActivity.this,
                        "No!", Toast.LENGTH_SHORT).show();
                comics = "Marvel";
                break;
        }
    }*/






    public JSONArray getBiersFromFile() {
        String json = null;
        try {
            InputStream is = new FileInputStream(getCacheDir() + "/" + "heroes.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(json);
            JSONArray results = obj.getJSONArray("results");



            JSONArray results_filtered = getCharactersBy("DC Comics", results);

            return results_filtered;

        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }

    }

    public JSONArray getCharactersBy(String str_publisher, JSONArray results) {
        JSONArray array_filtered = new JSONArray();

        int i,j=0;
        for (i = 0; i < 100; i++) {
            try {

                JSONObject element = results.getJSONObject(i);
                JSONObject publisher = element.getJSONObject("publisher");
                JSONObject image = element.getJSONObject("image");

                if (publisher.getString("name").equals(str_publisher)) {
                    JSONObject element_filtered = new JSONObject();
                    element_filtered.put("name", element.getString("name"));
                    element_filtered.put("publisher", publisher);
                    element_filtered.put("image", image);
                    element_filtered.put("small_url", image.getString("small_url"));
                    array_filtered.put(element_filtered);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return array_filtered;
    }

    public class BierUpdate extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            CharSequence text = "File downloaded !";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);

            BiersAdapter ba = (BiersAdapter) getRv_biere().getAdapter();
            ba.setNewBieres();
        }
    }


    private class BiersAdapter extends RecyclerView.Adapter<BiersAdapter.BierHolder> {

        private JSONArray bieres;

        public BiersAdapter(JSONArray bieres) {
            this.bieres = bieres;
        }

        @Override
        public BiersAdapter.BierHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(parent.getContext());

            View v = li.inflate(R.layout.rv_bier_element, parent, false);

            BierHolder bh = new BierHolder(v);



            return bh;
        }

        @Override
        public void onBindViewHolder(BiersAdapter.BierHolder holder, int position) {
            try {
                String str_name;
                String str_imageURL;
                JSONObject jo = bieres.getJSONObject(position);

                str_name = jo.getString("name");

                JSONObject image = jo.getJSONObject("image");
                str_imageURL = image.getString("small_url");

                holder.name.setText(str_name);
                Picasso.with(context).load(str_imageURL).into(holder.picture);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return bieres.length();
        }

        public void setNewBieres() {
            bieres = getBiersFromFile();
            notifyDataSetChanged();
        }


        public class BierHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public ImageView picture;


            public BierHolder(View v) {
                super(v);

                name = (TextView) v.findViewById(R.id.rv_bier_element_name);
                picture = (ImageView) v.findViewById(R.id.picture);
            }

        }

    }


}