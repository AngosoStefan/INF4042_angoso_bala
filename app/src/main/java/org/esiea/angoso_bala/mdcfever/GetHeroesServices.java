package org.esiea.angoso_bala.mdcfever;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GetHeroesServices extends IntentService {
    public static final String TAG = "GetHeroesServices";

    private static final String ACTION_GET_ALL_HEROES = "org.esiea.angoso_bala.mdcfever.action.heroes";

    public GetHeroesServices() {
        super("GetHeroesServices");
    }

    public static void startActionHeroes(Context context) {
        Intent intent = new Intent(context, GetHeroesServices.class);
        intent.setAction(ACTION_GET_ALL_HEROES);
        context.startService(intent);
    }

    public static void startActionBaz(Context context) {
        Intent intent = new Intent(context, GetHeroesServices.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_ALL_HEROES.equals(action)) {
                handleActionHeroes();
            }
        }
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Méthode de récupération des données de l'API rest*/

    private void handleActionHeroes() {
        Log.d(TAG, "Thread:" + Thread.currentThread().getName());
        URL url = null;
        try {
            url = new URL("http://www.comicvine.com/api/characters/?api_key=5432a36b9f8863afe084d12ae75ae89e3cf00383&field_list=name,publisher,image&format=json&offset=670");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                copyInputStreamToFile(conn.getInputStream(), new File(getCacheDir(), "heroes.json"));
                Log.d(TAG, "DOWNLOAD FINISHED FOR JSON HEROES !");
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MarvelActivity.HEROES_UPDATE));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}