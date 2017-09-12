package com.contigo.app;

import java.io.Serializable;

/**
 * Created by Trumpets on 06/12/16.
 */

public class History implements Serializable {

    private int _id;
    private String mName;
    private String mHomePhone;
    private String mProfilePic;
    private String mTimeAdded;
    private String mContactId;
    private String mState;

    public History() {

    }

    public History(String mName, String mHomePhone, String mProfilePic, String mTimeAdded, String mState, String mContactId) {
        this.mName = mName;
        this.mHomePhone = mHomePhone;
        this.mProfilePic = mProfilePic;
        this.mTimeAdded = mTimeAdded;
        this.mTimeAdded = mTimeAdded;
        this.mContactId = mContactId;
        this.mState = mState;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getTimeAdded() {
        return mTimeAdded;
    }

    public void setTimeAdded(String mTimeAdded) {
        this.mTimeAdded = mTimeAdded;
    }

    public String getContactId() {
        return mContactId;
    }

    public void setContactId(String mContactId) {
        this.mContactId = mContactId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getHomePhone() {
        return mHomePhone;
    }

    public void setHomePhone(String mHomePhone) {
        this.mHomePhone = mHomePhone;
    }

    public String getProfilePic() {
        return mProfilePic;
    }

    public void setProfilePic(String mProfilePic) {
        this.mProfilePic = mProfilePic;
    }

    public String getState() {
        return mState;
    }

    public void setState(String mState) {
        this.mState = mState;
    }
}
