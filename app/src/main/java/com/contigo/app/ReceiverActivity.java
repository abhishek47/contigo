package com.contigo.app;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.contigo.app.database.CGContactsDbHelper;
import com.contigo.app.database.ContactsController;
import com.contigo.app.database.HistoryController;
import com.contigo.app.utils.CapturePhotoUtils;
import com.contigo.app.wifiutils.WifiStatus;
import com.contigo.app.wifiutils.wifiHotSpots;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.karuppiah7890.fileshare.FileReceiver;

import static com.contigo.app.Utility.STATE_RECV;

public class ReceiverActivity extends AppCompatActivity {

    FileReceiver fileReceiver;
    TextView tvCode;

    @BindView(R.id.activity_receiver)
    RelativeLayout layout;

    @BindView(R.id.content)
    RippleBackground rippleBackground;

    @BindView(R.id.ssid)
    TextView ssid;

    @BindView(R.id.profile_image)
    ImageView profileImage;

    Contact recievedContact;


    private SerialBitmap serialProfilePic;
    private SerialBitmap serialVC;
    Bitmap profilePic, visitingCard;

    private Utility mUtility;

    Contact profileContact;

    String userAccount;

    private boolean doSyncContacts;


    wifiHotSpots wifiHotspots;
    WifiStatus wifiStatus;

    public static Uri phoneUri = ContactsContract.Data.CONTENT_URI;

    ProgressDialog sendingProgress;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FileReceiver.CODE:
                    break;

                case FileReceiver.LISTENING:
                    Toast.makeText(ReceiverActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
                    break;

                case FileReceiver.CONNECTED:
                    //Toast.makeText(ReceiverActivity.this,"Connected!",Toast.LENGTH_SHORT).show();

                    break;

                case FileReceiver.RECEIVING_FILE:
                    //Toast.makeText(ReceiverActivity.this,"Receiving File!",Toast.LENGTH_SHORT).show();
                    sendingProgress = ProgressDialog.show(ReceiverActivity.this, "Please Wait", Html.fromHtml("Receiving <b>" + "Contact" + "</b>..."), true);
                    sendingProgress.setCancelable(false);
                    break;

                case FileReceiver.FILE_RECEIVED:

                    File file = (File) msg.obj;
                    // Toast.makeText(ReceiverActivity.this,file.getName() + " Received!",Toast.LENGTH_SHORT).show();
                    //  Toast.makeText(ReceiverActivity.this,"Stored in " + file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                    fileReceiver.close();
                    storeContact(file);

                    break;

                case FileReceiver.RECEIVE_ERROR:
                    // Toast.makeText(ReceiverActivity.this,"Error occured : " + (String)msg.obj,Toast.LENGTH_SHORT).show();

                    fileReceiver.close();
                    break;
            }
        }
    };


    private void storeContact(final File myFile) {

       /* sendingProgress.dismiss();
        sendingProgress = ProgressDialog.show(ReceiverActivity.this, "Contact Received" ,Html.fromHtml("Saving the <b>" + "Contact"  + "</b>..."), true);
        sendingProgress.setCancelable(false);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileInputStream fileIn = null;
                try {
                    fileIn = new FileInputStream(myFile);
                    ObjectInputStream in = new ObjectInputStream(fileIn);

                    recievedContact = (Contact) in.readObject();


                    recievedContact.setIsPrimary(0);

                    Date d = new Date();
                    recievedContact.setTimeAdded(d.toString());

                    System.out.println("Step 1");


                    if (recievedContact.getProfilePic() != null) {
                        serialProfilePic = (SerialBitmap) in.readObject();
                        profilePic = serialProfilePic.bitmap;
                        recievedContact.setProfilePic(mUtility.storeImageAsync(profilePic, getApplicationContext(), Utility.PROFILE_PIC));
                        System.out.println("Step 2");
                    }
                    System.out.println("Step 3");

                    if (recievedContact.getVisitingCard() != null) {
                        serialVC = (SerialBitmap) in.readObject();
                        visitingCard = serialVC.bitmap;
                        String mCurrentPhotoPath = mUtility.storeImageAsync(visitingCard, getApplicationContext(), Utility.VISITING_CARD);
                        recievedContact.setVisitingCard(mCurrentPhotoPath);
                        System.out.println("Step 4");
                        CapturePhotoUtils.insertImage(getContentResolver(), visitingCard, recievedContact.getName(), recievedContact.getHomePhone());
                    }
                    System.out.println("Step 5");


                    myFile.delete();


                    System.out.println("Step 6");


                    final String cId = saveToContacts(recievedContact);

                    System.out.println("Step 7");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendingProgress.dismiss();

                            if(cId != null) {

                                new MaterialStyledDialog.Builder(ReceiverActivity.this)
                                        .setTitle("Contact Received!")
                                        .setDescription(Html.fromHtml("Profile was successfully received!"))
                                        .setHeaderColor(R.color.dialog_header_orange)
                                        .setIcon(R.drawable.thumb)
                                        .setNegativeText("Done")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                wifiHotspots.startHotSpot(false);
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .setPositiveText("View Contact")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                wifiHotspots.startHotSpot(false);
                                                Intent profileIntent = new Intent(ReceiverActivity.this, ProfileActivity.class);
                                                profileIntent.putExtra("contact_id", cId);
                                                startActivity(profileIntent);
                                                finish();
                                            }
                                        })
                                        .withDialogAnimation(true)
                                        .show();
                            }
                        }
                    });


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        ButterKnife.bind(this);

        changeStatusBarColor();

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);*/
        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        profileContact = ContactsController.getProfileContact(db);

        db.close();

        mUtility = new Utility();

        wifiHotspots = new wifiHotSpots(this);

        wifiStatus = new WifiStatus(this);


        ssid.setText("Initializing..");

       /* String hotspotName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("hotspot_name", profileContact.getName());

        String encodedHotspotName = "Adcg-" + Base64.encodeToString(hotspotName.getBytes(), Base64.DEFAULT);
        wifiHotspots.setHotSpot(encodedHotspotName, "");

        wifiHotspots.startHotSpot(true);




              fileReceiver = new FileReceiver(this, mHandler);

              fileReceiver.getFile();

              rippleBackground.startRippleAnimation();

              ssid.setText(hotspotName);*/

        if (profileContact.getProfilePic() != null) {
            File profilePic = new File(profileContact.getProfilePic());

            if (profilePic.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());

                profileImage.setImageBitmap(myBitmap);

            }

        }

        doSyncContacts = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("sync_contact", true);
        userAccount = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("contact_sync_account", getUsernameLong(this));

    }

    @OnClick(R.id.btn_back)
    public void backButton(View view) {
        onBackPressed();

    }

    @Override
    protected void onResume() {

        String hotspotName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("hotspot_name", profileContact.getName());

        String encodedHotspotName = "Adcg-" + Base64.encodeToString(hotspotName.getBytes(), Base64.DEFAULT);
        wifiHotspots.setHotSpot(encodedHotspotName, "");

        wifiHotspots.startHotSpot(true);


        fileReceiver = new FileReceiver(this, mHandler);

        fileReceiver.getFile();

        rippleBackground.startRippleAnimation();

        ssid.setText(hotspotName);
        super.onResume();
    }

    // boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {


        wifiHotspots.startHotSpot(false);

        fileReceiver.close();
        super.onBackPressed();

      /*  if (doubleBackToExitPressedOnce) {

        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);*/

    }

    @Override
    protected void onDestroy() {
        try {
            if (wifiHotspots.isWifiApEnabled()) {
                wifiHotspots.startHotSpot(false);
            }
            fileReceiver.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        try {
            if (wifiHotspots.isWifiApEnabled()) {
                wifiHotspots.startHotSpot(false);
            }
            fileReceiver.close();
            rippleBackground.stopRippleAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;
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
            window.setStatusBarColor(getResources().getColor(R.color.receiver_status));
        }
    }

    private String saveToContacts(Contact recievedContact) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        if(doSyncContacts && userAccount != null) {

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.google")
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, userAccount)
                    .build());
        } else {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());
        }

        //------------------------------------------------------ Names

        if (recievedContact.getName() != null) {
            if (contactExists(getApplicationContext(), recievedContact.getName())) {
                recievedContact.setName(recievedContact.getName() + "_CGO");
            }
            ops.add(ContentProviderOperation.newInsert(phoneUri)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            recievedContact.getName()).build());
        }

        //------------------------------------------------------ Mobile Number
        if (recievedContact.getHomePhone() != null) {
            ops.add(ContentProviderOperation.
                    newInsert(phoneUri)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, recievedContact.getHomePhone())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }


        //------------------------------------------------------ Work Numbers
        if (recievedContact.getWorkPhone() != null) {
            ops.add(ContentProviderOperation.newInsert(phoneUri)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, recievedContact.getWorkPhone())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Email
        if (recievedContact.getHomeEmail() != null) {
            ops.add(ContentProviderOperation.newInsert(phoneUri)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, recievedContact.getHomeEmail())
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                    .build());
        }
        //------------------------------------------------------work Email
        if (recievedContact.getWorkEmail() != null) {
            ops.add(ContentProviderOperation.newInsert(phoneUri)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, recievedContact.getWorkEmail())
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
        if(recievedContact.getWorkAddress() != null) {
            if (!recievedContact.getWorkAddress().equals("")) {
                ops.add(ContentProviderOperation.newInsert(phoneUri)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, recievedContact.getWorkAddress())
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
                        .build());
            }
        }
        //------------------------------------------------------ home
        if (recievedContact.getHomeAddress() != null) {
            if (!recievedContact.getHomeAddress().equals("")) {
                ops.add(ContentProviderOperation.newInsert(phoneUri)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, recievedContact.getHomeAddress())
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
                        .build());
            }
        }


        //profile pic
/*
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.KITKAT) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (profilePic != null) {    // If an image is selected successfully
                profilePic.compress(Bitmap.CompressFormat.PNG, 75, stream);

                // Adding insert operation to operations list
                // to insert Photo in the table ContactsContract.Data
                ops.add(ContentProviderOperation.newInsert(phoneUri)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                        .build());

                try {
                    stream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }*/
        try {
            // Asking the Contact provider to create a new contact


            getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);


            CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            String cId = String.valueOf(ContactsController.addContact(db, recievedContact));
            recievedContact.setContactId(cId);

            db.close();

            addHistoryRecord(recievedContact);

            return cId;


        } catch (Exception e1) {
            final Exception e = e1;
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new MaterialStyledDialog.Builder(ReceiverActivity.this)
                            .setTitle("Failed To Save Contact!")
                            .setDescription(Html.fromHtml("There was some problem saving the received contact!Please try again."))
                            .setHeaderColor(R.color.dialog_header)
                            .setIcon(R.drawable.failed)
                            .setPositiveText("Try Again")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                                }
                            })
                            .withDialogAnimation(true)
                            .show();
                }
            });


        }

        return null;

    }

    private void addHistoryRecord(Contact recievedContact) {
        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        History historyRecord = new History();
        historyRecord.setName(recievedContact.getName());
        historyRecord.setState(STATE_RECV);
        historyRecord.setContactId(recievedContact.getContactId());
        historyRecord.setTimeAdded((new Date()).toString());
        historyRecord.setProfilePic(recievedContact.getProfilePic());
        historyRecord.setHomePhone(recievedContact.getHomePhone());
        HistoryController.addHistory(db, historyRecord);

        db.close();
    }



    public boolean contactExists(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {

                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public static String getUsernameLong(Context context) {

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
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {

            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
            Log.i("DGEN ACCOUNT","CALENDAR LIST ACCOUNT/"+account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            return email;

        }
        return null;
    }


}
