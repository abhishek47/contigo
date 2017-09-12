package com.contigo.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.bottomsheet.BottomSheet;
import com.contigo.app.database.CGContactsDbHelper;
import com.contigo.app.database.ContactsController;
import com.contigo.app.database.HistoryController;
import com.contigo.app.wifiutils.WifiStatus;
import com.contigo.app.wifiutils.wifiHotSpots;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.guo.duoduo.randomtextview.RandomTextView;
import com.guo.duoduo.rippleoutview.RippleView;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.karuppiah7890.fileshare.FileSender;

import static com.contigo.app.Utility.STATE_SENT;

public class SenderActivity extends AppCompatActivity {


    private static final int CONTACT_PICKER_RESULT = 300;
    private FileSender fileSender;
    Button bPickFile;
    private Contact profileContact;
    private Contact contactToSend;
    WifiStatus wifiStatus;
    wifiHotSpots hotspotUtils;

    @BindView(R.id.content)
    RippleBackground rippleBackground;

    RandomTextView randomTextView;

    ProgressDialog sendingProgress;
    Handler ha;
    Runnable scanner;

    boolean wifiWasOn;
    String receiverSsid = null;
    String hname;

    @BindView(R.id.profile_image)
    ImageView profileImage;


    @BindView(R.id.contact_name)
    TextView contactName;

    @BindView(R.id.contact_phone)
    TextView contactPhone;



    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FileSender.CONNECTING :
                   // Toast.makeText(SenderActivity.this,"Connecting...",Toast.LENGTH_SHORT).show();

                    break;

                case FileSender.CONNECTED :
                    //Toast.makeText(SenderActivity.this,"Connected!",Toast.LENGTH_SHORT).show();
                    break;

                case FileSender.SENDING_FILE :
                    //Toast.makeText(SenderActivity.this,"Sending Contact!",Toast.LENGTH_SHORT).show();

                    break;

                case FileSender.FILE_SENT :
                    // Toast.makeText(SenderActivity.this,"Contact Sent!",Toast.LENGTH_SHORT).show();
                    sendingProgress.dismiss();
                    fileSender.close();
                    addHistoryRecord();
                    new MaterialStyledDialog.Builder(SenderActivity.this)
                            .setTitle("Contact Sent!")
                            .setDescription(Html.fromHtml("Your contact has been successfully shared!"))
                            .setHeaderColor(R.color.dialog_header_green)
                            .setIcon(R.drawable.thumb)
                            .setPositiveText("Done")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if(receiverSsid != null && receiverSsid.contains("Adcg"))
                                    {
                                        hotspotUtils.removeWifiNetwork(receiverSsid);
                                    }
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .withDialogAnimation(true)
                            .show();
                    break;

                case FileSender.SEND_ERROR :
                    sendingProgress.dismiss();
                    fileSender.close();
                    new MaterialStyledDialog.Builder(SenderActivity.this)
                            .setTitle("Sending Error!")
                            .setDescription(Html.fromHtml("Their was some error sharing your contact!Please try again."))
                            .setHeaderColor(R.color.dialog_header)
                            .setIcon(R.drawable.failed)
                            .setPositiveText("OK")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .withDialogAnimation(true)
                            .show();
                    //Toast.makeText(SenderActivity.this,"Error occured : " + (String)msg.obj,Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    private void addHistoryRecord() {
        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        History historyRecord = new History();
        historyRecord.setName(hname);
        historyRecord.setState(STATE_SENT);
        historyRecord.setContactId(null);
        historyRecord.setTimeAdded((new Date()).toString());
        historyRecord.setProfilePic(null);
        historyRecord.setHomePhone(profileContact.getHomePhone());
        HistoryController.addHistory(db, historyRecord);

        db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        ButterKnife.bind(this);
        changeStatusBarColor();

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);*/
        rippleBackground.startRippleAnimation();

        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        profileContact = ContactsController.getProfileContact(db);

        if (profileContact.getProfilePic() != null) {
            File profilePic = new File(profileContact.getProfilePic());

            if (profilePic.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());

                profileImage.setImageBitmap(myBitmap);

            }

        }



        if(getIntent().hasExtra("contact"))
        {
            contactToSend = (Contact)getIntent().getSerializableExtra("contact");

        } else {
            contactToSend = profileContact;
        }

        db.close();

        contactName.setText(contactToSend.getName());
        contactPhone.setText(contactToSend.getHomePhone());

        wifiStatus = new WifiStatus(this);
        hotspotUtils = new wifiHotSpots(this);

        wifiWasOn = wifiStatus.istWifiEnabled();

        randomTextView = (RandomTextView) findViewById(
                R.id.random_textview);
        ha = new Handler();

        scanner = new Runnable() {
            @Override
            public void run() {
                //call function
                if(wifiStatus.istWifiEnabled()) {
                    hotspotUtils.showHotspotsList(randomTextView);
                    ha.postDelayed(this, 4000);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //hotspotUtils.showHotspotsList(randomTextView);
                                new MaterialStyledDialog.Builder(SenderActivity.this)
                                        .setTitle("Wifi Not Enabled!")
                                        .setDescription(Html.fromHtml("There was an error enabling your wifi!Please try again"))
                                        .setHeaderColor(R.color.dialog_header)
                                        .setIcon(R.drawable.failed)
                                        .setPositiveText("Ok")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                onBackPressed();
                                                finish();
                                            }
                                        })
                                        .withDialogAnimation(true)
                                        .show();
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        };

        randomTextView.setOnRippleViewClickListener(
                new RandomTextView.OnRippleViewClickListener()
                {
                    @Override
                    public void onRippleViewClicked(View view)
                    {
                        ha.removeCallbacks(scanner);
                        RippleView text = (RippleView) view;
                        hname = text.getText().toString().trim();
                        launchRingDialog(hname);
                    }
                });









    }

    @Override
    protected void onResume() {

        if(!wifiStatus.istWifiEnabled())
          wifiStatus.setWifiEnabled();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!wifiStatus.istWifiEnabled()) {

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hotspotUtils.showHotspotsList(randomTextView);
                        ha.postDelayed(scanner, 4000);
                    }
                });

            }
        }).start();

        super.onResume();

    }

    private void connectToHotspot(String ssid) {



    }

    public void launchRingDialog(final String hname) {
        try {
         //   unregisterReceiver(hotspotUtils.mReceiver);
        } catch (Exception e)
        {

        }
       // final ProgressDialog ringProgressDialog = ProgressDialog.show(SenderActivity.this, "Connecting." , Html.fromHtml("Pairing with <b>" + hname  + "</b>..."), true);
        //ringProgressDialog.setCancelable(false);
        sendingProgress = ProgressDialog.show(SenderActivity.this, "Please Wait" ,Html.fromHtml("Sharing your <b>" + "Contact"  + "</b>..."), true);
        sendingProgress.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    // Let the progress ring for 10 seconds...
                   // final String encodedHotspotName = "Adcg-" + Base64.encodeToString(hname.getBytes(), Base64.DEFAULT);



                  hotspotUtils.connectReceiver(hname);

                  //  hotspotUtils.connectToHotspot(hname, "");

                    int time = 0;
                    while((!hotspotUtils.isConnectedToAP() || !hotspotUtils.getConnectionInfo().getSSID().contains("Adcg")) && time <= 5 ){
                        Thread.sleep(3000);
                        time++;
                    }



                } catch (Exception e) {
                    Toast.makeText(SenderActivity.this, "Exception : " + e.getMessage(), Toast.LENGTH_LONG).show();

                }

                //ringProgressDialog.dismiss();
                if(!hotspotUtils.isConnectedToAP() || !hotspotUtils.getConnectionInfo().getSSID().contains("Adcg")){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //hotspotUtils.showHotspotsList(randomTextView);
                            new MaterialStyledDialog.Builder(SenderActivity.this)
                                    .setTitle("Connection Failed!")
                                    .setDescription(Html.fromHtml("Connecting to <b>" + hname + "</b> failed.Please try again!"))
                                    .setHeaderColor(R.color.dialog_header)
                                    .setIcon(R.drawable.failed)
                                    .setPositiveText("Ok")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            //hotspotUtils.showHotspotsList(randomTextView);
                                            sendingProgress.dismiss();
                                            ha.removeCallbacks(scanner);
                                            finish();

                                        }
                                    })
                                    .withDialogAnimation(true)
                                    .show();
                        }
                    });

                } else {
                    receiverSsid = hotspotUtils.getConnectionInfo().getSSID();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            launchConfirmDialog(hname);
                        }
                    });
                }


            }
        }).start();
    }

    private void launchConfirmDialog(String hname) {

        // hotspotUtils.connectToHotspot(text.getText().toString(), "");
        //sendingProgress = ProgressDialog.show(SenderActivity.this, "Please Wait" ,Html.fromHtml("Sharing your <b>" + "Contact"  + "</b>..."), true);
       // sendingProgress.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMyFile();
            }
        }).start();


        /*new MaterialStyledDialog.Builder(this)
                .setTitle("Connected!")
                .setDescription(Html.fromHtml("Confirm to share your profile with <b>" + hname + "</b>."))
                .setStyle(Style.HEADER_WITH_ICON)
                .setPositiveText("Share")
                .setNegativeText("Cancel")
                .setCancelable(false)
                .setIcon(R.drawable.lsend)
                .setHeaderColor(R.color.dialog_header_green)
                .withDialogAnimation(true)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //hotspotUtils.showHotspotsList(randomTextView);
                        ha.postDelayed(scanner, 4000);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(hotspotUtils.isConnectedToAP())
                        {

                            // hotspotUtils.connectToHotspot(text.getText().toString(), "");
                            sendingProgress = ProgressDialog.show(SenderActivity.this, "Please Wait" ,Html.fromHtml("Sharing your <b>" + "Contact"  + "</b>..."), true);
                            sendingProgress.setCancelable(false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    sendMyFile();
                                }
                            }).start();

                        }
                    }
                })
                .show();*/
    }


    private void sendMyFile() {



        File file = serialiseData();

        fileSender = new FileSender(this,mHandler);

        int code = Utility.CONNECTION_PORT;
        fileSender.sendFile(file,code);




    }

    //bind data to file
    public File serialiseData()
    {

        try
        {
            File myFile = new File("/sdcard/contact.ser");

                myFile.createNewFile();

            myFile.deleteOnExit();


            FileOutputStream fileOut =new FileOutputStream(myFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(contactToSend);

            //check  it contain profile pic or not
            if(contactToSend.getProfilePic() != null &&  !contactToSend.getProfilePic().equals(""))
                out.writeObject(new SerialBitmap(contactToSend.getProfilePic()));

            //check  it contain vc or not
            if(contactToSend.getVisitingCard() != null && !contactToSend.getVisitingCard().equals(""))
                out.writeObject(new SerialBitmap(contactToSend.getVisitingCard()));




            out.close();
            fileOut.close();
            // Toast.makeText(getApplicationContext(), "Serialized data is saved in contact.ser",Toast.LENGTH_LONG).show();
            return myFile;
        }catch(IOException i)
        {


            // Toast.makeText( getApplicationContext(), "problem "+i,Toast.LENGTH_LONG).show();

            i.printStackTrace();
        }


        return null;
    }


    @OnClick(R.id.btn_back)
    public void backButton(View view)
    {

        onBackPressed();
    }

   // boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if(!wifiWasOn)
        {
           wifiStatus.setWifiDisabled();
        }
        ha.removeCallbacks(scanner);
        super.onBackPressed();
        //return;

        /*if (doubleBackToExitPressedOnce) {
         //   unregisterReceiver(hotspotUtils.mReceiver);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
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

                        contactToSend = selectedContact;

                        contactName.setText(contactToSend.getName());
                        contactPhone.setText(contactToSend.getHomePhone());
                    }

                }

                break;
        }
    }


    @OnClick(R.id.change_contact)
    public void changeContact(View view)
    {
        openAndroidBottomMenu();

    }

    @Override
    protected void onDestroy() {

        if(!wifiWasOn && wifiStatus.istWifiEnabled())
        {
            wifiStatus.setWifiDisabled();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(!wifiWasOn && wifiStatus.istWifiEnabled())
        {
            wifiStatus.setWifiDisabled();
        }
        ha.removeCallbacks(scanner);
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
            window.setStatusBarColor(getResources().getColor(R.color.color_sender));
        }
    }

    public void openAndroidBottomMenu() {

        new BottomSheet.Builder(this).title("Sharing Preferences").sheet(R.menu.bottom_menu).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.menu_profile:
                        // TODO when help menu/button is clicked
                        contactToSend = profileContact;
                        contactName.setText(contactToSend.getName());
                        contactPhone.setText(contactToSend.getHomePhone());

                        break;
                    case R.id.menu_contact:
                        // TODO when call menu/button is clicked
                        Intent intent = new Intent(SenderActivity.this, ContactPickerActivity.class)
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
