package com.contigo.app.models;

/**
 * Created by Trumpets on 24/10/16.
 */

public class ProfileItem {

    private int mIcon;
    private String mContent;
    private String mType;


    public ProfileItem(int mIcon, String mContent, String mType) {
        this.mIcon = mIcon;
        this.mContent = mContent;
        this.mType = mType;
    }

    public ProfileItem()
    {

    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int mIcon) {
        this.mIcon = mIcon;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }
}
