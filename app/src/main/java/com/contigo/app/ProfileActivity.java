package com.contigo.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.contigo.app.adapters.ProfileAdapter;
import com.contigo.app.database.CGContactsDbHelper;
import com.contigo.app.database.ContactsController;
import com.contigo.app.models.ProfileItem;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.io.File;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    ActionBar ab;

    Contact profileContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        changeStatusBarColor();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        // Get the ActionBar here to configure the way it behaves.
        ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);

        Contact profileContact;

        fillUserInfo(ab);

    }

    private void fillUserInfo(ActionBar ab) {



        CGContactsDbHelper mDbHelper = new CGContactsDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if(getIntent().hasExtra("contact_id")) {
            String cId = getIntent().getStringExtra("contact_id");
           profileContact = ContactsController.getContactById(db, cId);
            if(profileContact == null){
                new MaterialStyledDialog.Builder(ProfileActivity.this)
                        .setTitle("Profile Not Found!")
                        .setDescription(Html.fromHtml("Profile you are looking for is not found!Please try with some other profile."))
                        .setHeaderColor(R.color.dialog_header)
                        .setIcon(R.drawable.failed)
                        .setCancelable(false)
                        .setPositiveText("DISMISS")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                finish();
                            }
                        })
                        .withDialogAnimation(true)
                        .show();
            }
        } else {
            profileContact = ContactsController.getProfileContact(db);
        }

        ab.setTitle(profileContact.getName());


        ArrayList<ProfileItem> profileItems = new ArrayList<>();

        ImageView profileImageView = (ImageView) findViewById(R.id.profile_image);
        ImageView visitingCardView = (ImageView) findViewById(R.id.vcard);

        TextView dispName = (TextView) findViewById(R.id.dispName);

        if (profileContact.getProfilePic() != null) {
            File profilePic = new File(profileContact.getProfilePic());

            if (profilePic.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());

                profileImageView.setImageBitmap(myBitmap);

            }

        }

        if (profileContact.getVisitingCard() != null) {
            File vcardPic = new File(profileContact.getVisitingCard());

            if (vcardPic.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(vcardPic.getAbsolutePath());

                visitingCardView.setImageBitmap(myBitmap);

            }

        }


        dispName.setText(profileContact.getName());

        profileItems.add(new ProfileItem(R.mipmap.phone, profileContact.getHomePhone(), "Mobile"));
        profileItems.add(new ProfileItem(R.mipmap.mail, profileContact.getHomeEmail(), "Email"));
        profileItems.add(new ProfileItem(R.mipmap.placeholder, profileContact.getHomeAddress(), "Home Address"));


        LinearLayout list1 = (LinearLayout) findViewById(R.id.list1);

        list1.removeAllViews();


        for (ProfileItem currentItem: profileItems)
        {
            View listItemView = LayoutInflater.from(this).inflate(R.layout.profile_item, null);

            TextView nameTextView = (TextView) listItemView.findViewById(R.id.content_text);



            if(currentItem.getContent() == null || currentItem.getContent().equals(""))
            {
                nameTextView.setText("--");
            } else {
                nameTextView.setText(currentItem.getContent());
            }

            ImageView iconView = (ImageView) listItemView.findViewById(R.id.icon);

            iconView.setImageResource(currentItem.getIcon());

            TextView typeTextView = (TextView) listItemView.findViewById(R.id.type_text);

            typeTextView.setText(currentItem.getType());

            list1.addView(listItemView);
        }


        ArrayList<ProfileItem> workItems = new ArrayList<>();


        workItems.add(new ProfileItem(R.mipmap.office, profileContact.getCompanyName(), "Organisation"));
        workItems.add(new ProfileItem(R.mipmap.working, profileContact.getWorkAs(), "Designation"));
        workItems.add(new ProfileItem(R.mipmap.tele, profileContact.getWorkPhone(), "Work Phone"));
        workItems.add(new ProfileItem(R.mipmap.mail, profileContact.getWorkEmail(), "Work Email"));
        workItems.add(new ProfileItem(R.mipmap.placeholder, profileContact.getWorkAddress(), "Work Address"));


        ProfileAdapter workAdapter = new ProfileAdapter(this, workItems);

        LinearLayout list2 = (LinearLayout) findViewById(R.id.list2);

        list2.removeAllViews();


        for (ProfileItem currentItem: workItems)
        {
            View listItemView = LayoutInflater.from(this).inflate(R.layout.profile_item, null);

            TextView nameTextView = (TextView) listItemView.findViewById(R.id.content_text);



            if(currentItem.getContent() == null || currentItem.getContent().equals(""))
            {
                nameTextView.setText("--");
            } else {
                nameTextView.setText(currentItem.getContent());
            }

            ImageView iconView = (ImageView) listItemView.findViewById(R.id.icon);

            iconView.setImageResource(currentItem.getIcon());

            TextView typeTextView = (TextView) listItemView.findViewById(R.id.type_text);

            typeTextView.setText(currentItem.getType());

            list2.addView(listItemView);
        }


        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillUserInfo(ab);;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

               onBackPressed();
                return true;
            case R.id.action_edit:
                startActivity(new Intent(this, EditProfileActivity.class));
                return true;
            case R.id.action_contact:
                openContact(getApplicationContext(), profileContact.getHomePhone() );
                //showContactCard(getApplicationContext(), profileContact.getContactId());
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
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }


    public boolean openContact(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                long contactId = cur.getLong(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
                intent.setData(uri);
                startActivity(intent);
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public boolean showContactCard(Context context, String contactId)
    {
        try {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
                intent.setData(uri);
                startActivity(intent);
                return true;

        } finally {

        }
    }


}
