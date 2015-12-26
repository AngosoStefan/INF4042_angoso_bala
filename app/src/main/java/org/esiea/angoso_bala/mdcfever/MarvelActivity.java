package org.esiea.angoso_bala.mdcfever;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class MarvelActivity extends AppCompatActivity {

    final Context context = this;
    public static final String HEROES_UPDATE = "com.octip.cours.inf4042_11.HEROES_UPDATE";
    public static final String TAG = "GetHeroesServices";
    private RecyclerView rv_heroes;

    public String comics;

    /* Getters et setters du recyclerview */

    public RecyclerView getRv_heroes() {
        return rv_heroes;
    }

    public void setRv_heroes(RecyclerView rv_heroes) {
        this.rv_heroes = rv_heroes;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* On lance le téléchargement */

        GetHeroesServices.startActionHeroes(context);

        setContentView(R.layout.activity_second);

        /* Barre de titre */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Gestion de la réception */

        IntentFilter intentFilter = new IntentFilter(HEROES_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new HeroesUpdate(), intentFilter);

        /* Gestion du recyclerview */

        rv_heroes = (RecyclerView) findViewById(R.id.rv_heroes);
        rv_heroes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        /* On crée un JSONArray à partir du fichier */

        JSONArray heroes = getHeroesFromFile();

        HeroesAdapter ba = new HeroesAdapter(heroes);
        rv_heroes.setAdapter(ba);
    }

    public JSONArray getHeroesFromFile() {
        String json = null;
        try {
            InputStream is = new FileInputStream(getCacheDir() + "/" + "heroes.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(json);
            JSONArray results = obj.getJSONArray("results");

            /* Nous filtrons les résultats non pas depuis le site, mais en JAVA : on ne garde que Marvel */

            JSONArray results_filtered = getCharactersBy("Marvel", results);

            return results_filtered;

        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }

    }

    /* Méthode de filtrage par publisher */

    public JSONArray getCharactersBy(String str_publisher, JSONArray results) {
        JSONArray array_filtered = new JSONArray();

        int i, j = 0;
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

    public class HeroesUpdate extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setTicker(context.getString(R.string.notif_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(context.getString(R.string.notif_title))
                    .setContentText(context.getString(R.string.notif_content))
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

            HeroesAdapter ba = (HeroesAdapter) getRv_heroes().getAdapter();
            ba.setNewHeroes();
        }
    }

    private class HeroesAdapter extends RecyclerView.Adapter<HeroesAdapter.HeroesHolder> {

        private JSONArray heroes;

        public HeroesAdapter(JSONArray heroes) {
            this.heroes = heroes;
        }

        @Override
        public HeroesAdapter.HeroesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(parent.getContext());

            View v = li.inflate(R.layout.rv_heroes_element, parent, false);

            HeroesHolder bh = new HeroesHolder(v);

            return bh;
        }

        @Override
        public void onBindViewHolder(HeroesAdapter.HeroesHolder holder, int position) {
            try {
                String str_name;
                String str_imageURL;
                JSONObject jo = heroes.getJSONObject(position);

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
            return heroes.length();
        }

        public void setNewHeroes() {
            heroes = getHeroesFromFile();
            notifyDataSetChanged();
        }


        public class HeroesHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public ImageView picture;


            public HeroesHolder(View v) {
                super(v);

                name = (TextView) v.findViewById(R.id.rv_heroes_element_name);
                picture = (ImageView) v.findViewById(R.id.picture);
            }

        }

    }


}