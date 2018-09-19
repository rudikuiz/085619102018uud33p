package com.android.services.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tambora on 09/09/2016.
 */
public class ContactModel implements Parcelable {

    private String idContact;
    private String nama;
    private String foto;
    private String extra;
    private String notif;

    public ContactModel() {

    }

    public ContactModel(String idContact, String nama, String foto, String extra, String notif) {
        this.idContact = idContact;
        this.nama = nama;
        this.foto = foto;
        this.extra = extra;
        this.notif = notif;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idContact);
        dest.writeString(this.nama);
        dest.writeString(this.foto);
        dest.writeString(this.extra);
        dest.writeString(this.notif);
    }

    protected ContactModel(Parcel in) {
        this.idContact = in.readString();
        this.nama = in.readString();
        this.foto = in.readString();
        this.extra = in.readString();
        this.notif = in.readString();
    }

    public static final Creator<ContactModel> CREATOR = new Creator<ContactModel>() {
        @Override
        public ContactModel createFromParcel(Parcel source) {
            return new ContactModel(source);
        }

        @Override
        public ContactModel[] newArray(int size) {
            return new ContactModel[size];
        }
    };

    public String getIdContact() {
        return idContact;
    }

    public void setIdContact(String idContact) {
        this.idContact = idContact;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }
}
