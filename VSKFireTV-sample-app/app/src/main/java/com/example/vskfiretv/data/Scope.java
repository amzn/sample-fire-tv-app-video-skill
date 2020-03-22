
package com.example.vskfiretv.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Scope implements Parcelable
{

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("type")
    @Expose
    private String type;
    public final static Creator<Scope> CREATOR = new Creator<Scope>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Scope createFromParcel(Parcel in) {
            return new Scope(in);
        }

        public Scope[] newArray(int size) {
            return (new Scope[size]);
        }

    }
    ;

    protected Scope(Parcel in) {
        this.token = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Scope() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(token);
        dest.writeValue(type);
    }

    public int describeContents() {
        return  0;
    }

}
