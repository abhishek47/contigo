package com.contigo.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.contigo.app.database.CGContactsDbHelper;
import com.contigo.app.database.ContactsController;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.contigo.app.Utility.MY_PERMISSIONS_REQUEST_CAMERA;


public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_image)
    ImageView user_photo;

    @BindView(R.id.visiting_card)
    ImageView visiting_card;

    @BindView(R.id.contact_name)
    EditText disp_name;

    @BindView(R.id.home_phone)
    EditText home_phone;

    @BindView(R.id.home_email)
    EditText home_email;

    @BindView(R.id.home_address)
    EditText home_address;

    @BindView(R.id.company_name)
    EditText company_name;

    @BindView(R.id.work_phone)
    EditText work_phone;

    @BindView(R.id.work_email)
    EditText work_email;

    @BindView(R.id.work_address)
    EditText work_address;

    @BindView(R.id.work_as)
    EditText work_as;





    @BindView(R.id.btn_save_profile)
    Button btnSave;


    private static final int PICK_PROFILE_PIC = 234; // the number doesn't matter
    private static final int PICK_VISITING_CARD = 235; // the number doesn't matter

    private String profilePicPath;
    private String visitingCardPath;

    private Bitmap profilePic;
    private  Bitmap visitingCard;

    PrefManager prefManager;

    Utility mUtility = new Utility();

    private Contact profileContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        prefManager = new PrefManager(this);

        profilePic = null;
        visitingCard = null;


        changeStatusBarColor();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);


        btnSave.setText("Update Profile");




        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if(getIntent().hasExtra("name")) {
            String name = getIntent().getStringExtra("name");
            profileContact = ContactsController.getContact(db, name);
        } else {
            profileContact = ContactsController.getProfileContact(db);
        }
        fillUserInfo();



    }


    @OnClick(R.id.btn_save_profile)
    public void saveProfile()
    {
        String name = disp_name.getText().toString().trim();
        String homePhone = home_phone.getText().toString().trim();
        String workPhone = work_phone.getText().toString().trim();
        String homeEmail = home_email.getText().toString().trim();
        String workEmail = work_email.getText().toString().trim();
        String workAddress = work_address.getText().toString().trim();
        String companyName = company_name.getText().toString().trim();
        String workAs = work_as.getText().toString().trim();
        String homeAddress = home_address.getText().toString().trim();

        Contact contact = new Contact();
        contact.setId(profileContact.getId());
        contact.setName(name);
        contact.setHomePhone(homePhone);
        contact.setHomeEmail(homeEmail);
        contact.setHomeAddress(homeAddress);
        contact.setCompanyName(companyName);
        contact.setWorkAs(workAs);
        contact.setWorkPhone(workPhone);
        contact.setWorkEmail(workEmail);
        contact.setWorkAddress(workAddress);

        contact.setTimeAdded(profileContact.getTimeAdded());
        contact.setContactId(profileContact.getContactId());

        if(profilePic != null)
        {
            profilePicPath = mUtility.storeImage(profilePic, getApplicationContext(), Utility.PROFILE_PIC);
            contact.setProfilePic(profilePicPath);
        } else {
            contact.setProfilePic(profileContact.getProfilePic());
        }


        if(visitingCard != null)
        {
            visitingCardPath = mUtility.storeImage(visitingCard, getApplicationContext(), Utility.VISITING_CARD);
            contact.setVisitingCard(visitingCardPath);
        } else {
            contact.setVisitingCard(profileContact.getVisitingCard());
        }



        contact.setIsPrimary(1);

        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContactsController.updateContact(db, contact);



        launchRingDialog();



    }


    private void fillUserInfo() {

        disp_name.setText(profileContact.getName());
        home_phone.setText(profileContact.getHomePhone());
        home_email.setText(profileContact.getHomeEmail());
        home_address.setText(profileContact.getHomeAddress());
        company_name.setText(profileContact.getCompanyName());
        work_as.setText(profileContact.getWorkAs());
        work_phone.setText(profileContact.getWorkPhone());
        work_email.setText(profileContact.getWorkEmail());
        work_address.setText(profileContact.getWorkAddress());

        if (profileContact.getProfilePic() != null) {
            File profilePic = new File(profileContact.getProfilePic());

            if (profilePic.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());

                user_photo.setImageBitmap(myBitmap);

            }

        }

        if (profileContact.getVisitingCard() != null) {
            File vcardPic = new File(profileContact.getVisitingCard());

            if (vcardPic.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(vcardPic.getAbsolutePath());

                visiting_card.setImageBitmap(myBitmap);

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;
            case R.id.action_done:
                saveProfile();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(EditProfileActivity.this, "Please wait.",	"Updating Your Information ...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    // Let the progress ring for 10 seconds...
                    Thread.sleep(2000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();

                finish();

            }
        }).start();
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



    @OnClick(R.id.profile_image)
    public void onPickImage(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_PROFILE_PIC);
    }

    @OnClick(R.id.visiting_card)
    public void onPickCard(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_VISITING_CARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {

            case PICK_PROFILE_PIC:
               profilePic = ImagePicker.getImageFromResult(this, resultCode, data);
                if(profilePic != null)
                 user_photo.setImageBitmap(profilePic);

                // TODO use bitmap
                break;
            case PICK_VISITING_CARD:
                visitingCard = ImagePicker.getImageFromResult(this, resultCode, data);
                if(visitingCard != null)
                 visiting_card.setImageBitmap(visitingCard);

                // TODO use bitmap
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }


}
