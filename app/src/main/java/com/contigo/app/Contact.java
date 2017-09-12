package com.contigo.app;

import java.io.Serializable;

/**
 * Created by Trumpets on 22/10/16.
 */

public class Contact implements Serializable {

    private int _id;
    private String mName;
    private String mHomeEmail;
    private String mHomePhone;
    private String mHomeAddress;
    private String mCompanyName;
    private String mWorkAs;
    private String mWorkEmail;
    private String mWorkPhone;
    private String mWorkAddress;
    private String mProfilePic;
    private String mVisitingCard;
    private String mTimeAdded;
    private String mContactId;
    private int mIsPrimary;

    public Contact() {

    }

    public Contact(String mName, String mHomeEmail, String mHomePhone, String mHomeAddress, String mCompanyName, String mWorkEmail, String mWorkPhone, String mWorkAddress, String mProfilePic, String mVisitingCard, int primary) {
        this.mName = mName;
        this.mHomeEmail = mHomeEmail;
        this.mHomePhone = mHomePhone;
        this.mHomeAddress = mHomeAddress;
        this.mCompanyName = mCompanyName;

        this.mWorkEmail = mWorkEmail;
        this.mWorkPhone = mWorkPhone;
        this.mWorkAddress = mWorkAddress;
        this.mProfilePic = mProfilePic;
        this.mVisitingCard = mVisitingCard;
        this.mIsPrimary = primary;
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

    public String getHomeEmail() {
        return mHomeEmail;
    }

    public void setHomeEmail(String mHomeEmail) {
        this.mHomeEmail = mHomeEmail;
    }

    public String getHomePhone() {
        return mHomePhone;
    }

    public void setHomePhone(String mHomePhone) {
        this.mHomePhone = mHomePhone;
    }

    public String getHomeAddress() {
        return mHomeAddress;
    }

    public void setHomeAddress(String mHomeAddress) {
        this.mHomeAddress = mHomeAddress;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public String getWorkEmail() {
        return mWorkEmail;
    }

    public void setWorkEmail(String mWorkEmail) {
        this.mWorkEmail = mWorkEmail;
    }

    public String getWorkPhone() {
        return mWorkPhone;
    }

    public void setWorkPhone(String mWorkPhone) {
        this.mWorkPhone = mWorkPhone;
    }

    public String getWorkAddress() {
        return mWorkAddress;
    }

    public void setWorkAddress(String mWorkAddress) {
        this.mWorkAddress = mWorkAddress;
    }


    public String getVisitingCard() {
        return mVisitingCard;
    }

    public void setVisitingCard(String mVisitingCard) {
        this.mVisitingCard = mVisitingCard;
    }

    public String getProfilePic() {
        return mProfilePic;
    }

    public void setProfilePic(String mProfilePic) {
        this.mProfilePic = mProfilePic;
    }

    public int isPrimary() {
        return mIsPrimary;
    }

    public void setIsPrimary(int mIsPrimary) {
        this.mIsPrimary = mIsPrimary;
    }

    public String getWorkAs() {
        return mWorkAs;
    }

    public void setWorkAs(String mWorkAs) {
        this.mWorkAs = mWorkAs;
    }

}
