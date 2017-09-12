package com.contigo.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.contigo.app.database.CGContactsDbHelper;
import com.contigo.app.database.ContactsController;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.contigo.app.Utility.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.contigo.app.Utility.checkAndRequestPermissions;


public class NewProfileActivity extends AppCompatActivity {

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


    private static final int PICK_PROFILE_PIC = 234; // the number doesn't matter
    private static final int PICK_VISITING_CARD = 235; // the number doesn't matter

    private String profilePicPath;
    private String visitingCardPath;

    private Bitmap profilePic;
    private  Bitmap visitingCard;

    PrefManager prefManager;

    Utility mUtility = new Utility();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_new_profile);
        ButterKnife.bind(this);

        prefManager = new PrefManager(this);

        profilePic = null;
        visitingCard = null;



        changeStatusBarColor();

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


    @OnClick(R.id.btn_save_profile)
    public void saveProfile(View view)
    {
        String name = disp_name.getText().toString().trim();
        String homePhone = home_phone.getText().toString().trim();
        String workPhone = work_phone.getText().toString().trim();
        String homeEmail = home_email.getText().toString().trim();
        String workEmail = work_email.getText().toString().trim();
        String workAddress = work_address.getText().toString().trim();
        String companyName = company_name.getText().toString().trim();
        String workAs = work_as.getText().toString().trim();
        String homeAddress = home_address.getText().toString();

        Contact contact = new Contact();
        contact.setName(name);
        contact.setHomePhone(homePhone);
        contact.setHomeEmail(homeEmail);
        contact.setHomeAddress(homeAddress);
        contact.setCompanyName(companyName);
        contact.setWorkAs(workAs);
        contact.setWorkPhone(workPhone);
        contact.setWorkEmail(workEmail);
        contact.setWorkAddress(workAddress);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        contact.setTimeAdded(date);
        contact.setContactId("PRI");

        if(profilePic != null)
        {
            profilePicPath = mUtility.storeImageAsync(profilePic, getApplicationContext(), Utility.PROFILE_PIC);
        }
        contact.setProfilePic(profilePicPath);

        if(visitingCard != null)
        {
            visitingCardPath = mUtility.storeImageAsync(visitingCard, getApplicationContext(), Utility.VISITING_CARD);
        }

        contact.setVisitingCard(visitingCardPath);

        contact.setIsPrimary(1);

        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContactsController.addContact(db, contact);

        int count = ContactsController.getContactsCount(db);

        if(count > 0) {

            db.close();
            prefManager.setFirstTimeLaunch(false);
            launchRingDialog();




        } else {

            db.close();
            Toast.makeText(getApplicationContext(), "There was an error saving your profile!Please try again.", Toast.LENGTH_LONG).show();
        }
    }



    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(NewProfileActivity.this, "Please wait.",	"Saving Your Information ...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    // Let the progress ring for 10 seconds...
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
                startActivity(new Intent(NewProfileActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
            window.setStatusBarColor(getResources().getColor(R.color.bg_screen5));
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
