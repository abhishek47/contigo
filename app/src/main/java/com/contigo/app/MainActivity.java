package com.contigo.app;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.bottomsheet.BottomSheet;
import com.contigo.app.database.CGContactsDbHelper;
import com.contigo.app.database.ContactsController;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.contigo.app.Utility.REQUEST_LOCATION_SERVICES;
import static com.contigo.app.Utility.checkAndRequestPermissions;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int CONTACT_PICKER_RESULT = 300;
    Contact profileContact;
    NavigationView navigationView;

    @BindView(R.id.profile_image)
    ImageView mProfileImage;

    @BindView(R.id.profile_name)
    TextView mProfileName;

    @BindView(R.id.home_email)
    TextView mEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        boolean toStart = getProfileContact();

        if(toStart) {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            setDefaultPreferenceValues();

            setHotspotName();

            // populateAccountsPreference();

            initDrawer(toolbar);

            ensureNeededPermissions();


            setProfileInfo();

            checkSettingsPermission();

        }

    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void ensureNeededPermissions() {
        // This will ask for the camera permission AND the contacts permission on the same slide.
        // Ensure your slide talks about both so as not to confuse the user.
        if (Build.VERSION.SDK_INT < 23) {
            //Do not need to check the permission
        } else {
            if (checkAndRequestPermissions(this)) {
                //If you have already permitted the permission

            }


        }
    }

    private void checkSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(MainActivity.this)) {
                // Do stuff here
            }
            else {
                new MaterialStyledDialog.Builder(MainActivity.this)
                        .setTitle("Permission Required!")
                        .setDescription(Html.fromHtml("As per the Marshmallow and further versions of android user need to " +
                                                      "grant permission to the app to modify system settings.In further screen toggle the permission to on." +
                                                      "Even if it is on turn it off and then turn on again."))
                        .setHeaderColor(R.color.dialog_header)
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .setPositiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .withDialogAnimation(true)
                        .setCancelable(false)
                        .show();

            }
        }
    }

    private void setDefaultPreferenceValues() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
    }

    private boolean getProfileContact() {
        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        profileContact = ContactsController.getProfileContact(db);

        db.close();

        PrefManager prefManager = new PrefManager(this);

        if (prefManager.isFirstTimeLaunch() || profileContact == null) {
            launchSplashScreen();
            return false;

        }

        return true;
    }

    private void launchSplashScreen() {
        startActivity(new Intent(MainActivity.this, SplashScreenActivity.class));
        finish();
    }

    private void setHotspotName() {
        if(getDefaultSharedPreferences(getApplicationContext()).getString("hotspot_name", "ContiGo").equals("ContiGo")) {
            SharedPreferences sharedPref = getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("hotspot_name", profileContact.getName());
            editor.apply();

        }
    }


    private void setProfileInfo() {



        mProfileName.setText(profileContact.getName());
        mEmail.setText(profileContact.getHomeEmail());

        View headerLayout = navigationView.getHeaderView(0); // 0-index header

        TextView name = (TextView) headerLayout.findViewById(R.id.profile_name);
        name.setText(profileContact.getName());

        TextView homeEmail = (TextView) headerLayout.findViewById(R.id.home_email);
        homeEmail.setText(profileContact.getHomeEmail());

        ImageView profileImage = (ImageView) headerLayout.findViewById(R.id.profile_image);

        if (profileContact.getProfilePic() != null) {
            File profilePic = new File(profileContact.getProfilePic());

        if (profilePic.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());

            profileImage.setImageBitmap(myBitmap);

            mProfileImage.setImageBitmap(myBitmap);

        }

        }




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProfileInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_history) {
            startActivity(new Intent(this, HistoryActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_share) {
            // Create fragment and give it an argument specifying the article it should show

        } else if (id == R.id.nav_profile) {

            Intent profileIntent = new Intent(this, ProfileActivity.class);
            startActivity(profileIntent);




        } else if (id == R.id.nav_settings) {

            startActivity(new Intent(this, SettingsActivity.class));


        } else if (id == R.id.nav_search) {

            startActivity(new Intent(this, HistoryActivity.class));

        } else if (id == R.id.nav_like) {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

        } else if (id == R.id.nav_about) {

            startActivity(new Intent(this, AboutActivity.class));

        }

        // and add the transaction to the back stack so the user can navigate back



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.bSend)
    public  void sendClick(View view)
    {
       /* WifiStatus wifiStatus = new WifiStatus(this);
        if(!wifiStatus.istWifiEnabled())
        {
            wifiStatus.setWifiEnabled();
            launchRingDialog();


        } else {*/
        // Start loction service
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {

            Utility.displayPromptForEnablingGPS(MainActivity.this);


        } else {
            openAndroidBottomMenu();
        }


        // }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_LOCATION_SERVICES:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Intent myIntent = new Intent(MainActivity.this, SenderActivity.class);
                        MainActivity.this.startActivity(myIntent);
                        break;
                    case Activity.RESULT_CANCELED:

                        break;
                }
                break;
            case  CONTACT_PICKER_RESULT :

                if(resultCode == Activity.RESULT_OK && data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {
                    Contact selectedContact = new Contact();

                    // process contacts
                    List<com.onegravity.contactpicker.contact.Contact> contacts = (List<com.onegravity.contactpicker.contact.Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
                    if(!contacts.isEmpty()) {
                        com.onegravity.contactpicker.contact.Contact contact = contacts.get(0);

                        selectedContact.setContactId(String.valueOf(contact.getId()));
                        selectedContact.setProfilePic(null);
                        if (contact.getPhotoUri() != null) {
                            selectedContact.setProfilePic(contact.getPhotoUri().getEncodedPath());
                        }


                        selectedContact.setName(contact.getDisplayName());
                        selectedContact.setHomeAddress(contact.getAddress(ContactsContract.CommonDataKinds.Phone.TYPE_HOME));
                        if(selectedContact.getHomeAddress() == null)
                        {
                            selectedContact.setHomeAddress("");
                        }
                        selectedContact.setWorkAddress(contact.getAddress(ContactsContract.CommonDataKinds.Phone.TYPE_WORK));
                        if(selectedContact.getWorkAddress() == null)
                        {
                            selectedContact.setWorkAddress("");
                        }
                        selectedContact.setHomePhone(contact.getPhone(ContactsContract.CommonDataKinds.Phone.TYPE_HOME));
                        if(selectedContact.getHomePhone() == null)
                        {
                            selectedContact.setHomePhone("");
                        }
                        selectedContact.setWorkPhone(contact.getPhone(ContactsContract.CommonDataKinds.Phone.TYPE_WORK));
                        if(selectedContact.getWorkPhone() == null)
                        {
                            selectedContact.setWorkPhone("");
                        }
                        selectedContact.setHomeEmail(contact.getEmail(ContactsContract.CommonDataKinds.Phone.TYPE_HOME));
                        if(selectedContact.getHomeEmail() == null)
                        {
                            selectedContact.setHomeEmail("");
                        }
                        selectedContact.setWorkEmail(contact.getEmail(ContactsContract.CommonDataKinds.Phone.TYPE_WORK));
                        if(selectedContact.getWorkEmail() == null)
                        {
                            selectedContact.setWorkEmail("");
                        }
                        selectedContact.setCompanyName("");
                        selectedContact.setWorkAs("");
                        selectedContact.setIsPrimary(0);


                        Intent myIntent = new Intent(MainActivity.this, SenderActivity.class);
                        myIntent.putExtra("contact", selectedContact);
                        MainActivity.this.startActivity(myIntent);
                    }

                }

                break;
        }
    }

    @OnClick(R.id.bSendios)
    public  void sendIosClick(View view)
    {
        //startActivity(new Intent(MainActivity.this, SenderActivity.class));
       // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Toast.makeText(this, "Feature Coming Soon", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.bReceive)
    public  void receiveClick(View view)
    {
        startActivity(new Intent(MainActivity.this, ReceiverActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @OnClick(R.id.select_contact)
    public void selectContact(View view)
    {

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {

            Utility.displayPromptForEnablingGPS(MainActivity.this);


        } else {
            Intent intent = new Intent(this, ContactPickerActivity.class)
                    .putExtra(ContactPickerActivity.EXTRA_THEME, R.style.Theme_Dark)
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                    .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, false)
                    .putExtra(ContactPickerActivity.EXTRA_SELECT_CONTACTS_LIMIT, 1)
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
            startActivityForResult(intent, CONTACT_PICKER_RESULT);
        }

    }

    public static ArrayList<String> getGoogleAccounts(Context context) {

        AccountManager manager = AccountManager.get(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Account[] accounts = manager.getAccountsByType("com.google");
        ArrayList<String> possibleEmails = new ArrayList<>();

        for (Account account : accounts) {

            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
            Log.i("DGEN ACCOUNT","CALENDAR LIST ACCOUNT/"+account.name);
        }

       return possibleEmails;
    }


   public void openAndroidBottomMenu() {

        new BottomSheet.Builder(this).title("Sharing Preferences").sheet(R.menu.bottom_menu).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.menu_profile:
                        // TODO when help menu/button is clicked
                        Intent myIntent = new Intent(MainActivity.this, SenderActivity.class);
                        MainActivity.this.startActivity(myIntent);
                        break;
                    case R.id.menu_contact:
                        // TODO when call menu/button is clicked
                        Intent intent = new Intent(MainActivity.this, ContactPickerActivity.class)
                                .putExtra(ContactPickerActivity.EXTRA_THEME, R.style.Theme_Dark)
                                .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                                .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, false)
                                .putExtra(ContactPickerActivity.EXTRA_SELECT_CONTACTS_LIMIT, 1)
                                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
                        startActivityForResult(intent, CONTACT_PICKER_RESULT);
                        break;

                }
            }
        }).show();
    }
}
