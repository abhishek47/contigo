package com.contigo.app;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.contigo.app.adapters.HistoryAdapter;
import com.contigo.app.database.CGContactsDbHelper;
import com.contigo.app.database.HistoryController;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.ArrayList;

import static com.contigo.app.Utility.STATE_RECV;

public class HistoryActivity extends AppCompatActivity {

    ArrayList<History> history;
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        changeStatusBarColor();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);


        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        history = HistoryController.getAllHistory(db);

        historyAdapter = new HistoryAdapter(this, history);

        ListView contactsList = (ListView) findViewById(R.id.contacts_list);

        contactsList.setAdapter(historyAdapter);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String toOpen = history.get(position).getState();
                if(toOpen.equals(STATE_RECV)){
                    String Contactid = history.get(position).getContactId();
                    Intent profileIntent = new Intent(HistoryActivity.this, ProfileActivity.class);
                    profileIntent.putExtra("contact_id", Contactid);
                    startActivity(profileIntent);
                } else {
                    Toast.makeText(HistoryActivity.this, "Profile not found!", Toast.LENGTH_LONG).show();
                }


            }
        });



        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;
            case R.id.action_clear:
                new MaterialStyledDialog.Builder(HistoryActivity.this)
                        .setTitle("Clear All History?")
                        .setDescription(Html.fromHtml("Confirm to clear all receive history.This won't delete the actual contacts from the phone."))
                        .setHeaderColor(R.color.dialog_header_orange)
                        .setIcon(R.drawable.failed)
                        .setNegativeText("CANCEL")
                        .setPositiveText("DELETE")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

                                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                                HistoryController.deleteAllHistorys(db);
                                historyAdapter.clear();
                                historyAdapter.notifyDataSetChanged();

                                db.close();

                            }
                        })
                        .withDialogAnimation(true)
                        .show();


        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
