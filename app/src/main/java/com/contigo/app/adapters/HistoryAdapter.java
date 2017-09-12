package com.contigo.app.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.contigo.app.History;
import com.contigo.app.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.contigo.app.Utility.STATE_SENT;

/**
 * Created by Trumpets on 22/10/16.
 */

public class HistoryAdapter extends ArrayAdapter<History> {

    public HistoryAdapter(Activity context, ArrayList<History> history) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, history);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.history_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        History currentContact = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.contact_name);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        nameTextView.setText(currentContact.getName());

        RelativeTimeTextView timeTextView = (RelativeTimeTextView) listItemView.findViewById(R.id.timestamp);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView

        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.US);
        try {
            String temp = currentContact.getTimeAdded();
            Date timeAdded = formatter.parse(temp);

            timeTextView.setReferenceTime(timeAdded.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }





        ImageView stateImage = (ImageView) listItemView.findViewById(R.id.state);


        if(currentContact.getState().equals(STATE_SENT))
        {
            timeTextView.setPrefix("Shared ");
            stateImage.setImageResource(R.drawable.sent);
        } else {
            timeTextView.setPrefix("Received ");
            stateImage.setImageResource(R.drawable.received);
        }


        // Find the ImageView in the list_item.xml layout with the ID list_item_icon
        ImageView iconView = (ImageView) listItemView.findViewById(R.id.profile_image);
        // Get the image resource ID from the current AndroidFlavor object and
        // set the image to iconView

        if (currentContact.getProfilePic() != null) {
            File profilePic = new File(currentContact.getProfilePic());

            if (profilePic.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());

                iconView.setImageBitmap(myBitmap);

            }

        }


        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }
}
