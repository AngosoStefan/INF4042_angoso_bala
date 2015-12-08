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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class SecondActivity extends AppCompatActivity {

    final Context context = this;
    public static final String BIERS_UPDATE = "com.octip.cours.inf4042_11.BIERS_UPDATE";
    public static final String TAG = "GetBiersServices";
    private RecyclerView rv_biere;

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

        setContentView(R.layout.activity_second);
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

    public JSONArray getBiersFromFile() {
        try {
            InputStream is = new FileInputStream(getCacheDir() + "/" + "bieres.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            return new JSONArray(new String(buffer, "UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }

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
                JSONObject jo = bieres.getJSONObject(position);
                str_name = jo.getString("name");

                holder.name.setText(str_name);

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

            public BierHolder(View v) {
                super(v);

                name = (TextView) v.findViewById(R.id.rv_bier_element_name);
            }

        }

    }


}