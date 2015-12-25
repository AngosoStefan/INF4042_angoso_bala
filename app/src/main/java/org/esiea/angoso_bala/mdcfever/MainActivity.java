package org.esiea.angoso_bala.mdcfever;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Barre du haut */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Zone Marvel */

        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton1);

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CharSequence text = context.getString(R.string.m_choice);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);

                Intent intent = new Intent(context, MarvelActivity.class);
                startActivity(intent);
            }
        });

        /* Zone DC comics */

        ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);

        imageButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CharSequence text = context.getString(R.string.dc_choice);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);

                Intent intent = new Intent(context, DCActivity.class);
                startActivity(intent);
            }
        });

        /* Bouton about */

        Button b_about = (Button) findViewById(R.id.about);

        b_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("About");
                alertDialog.setMessage(context.getString(R.string.about_content));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


}
