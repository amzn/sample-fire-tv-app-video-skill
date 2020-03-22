
package com.example.vskfiretv.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Endpoint implements Parcelable
{

    @SerializedName("cookie")
    @Expose
    private Cookie cookie;
    @SerializedName("endpointId")
    @Expose
    private String endpointId;
    @SerializedName("scope")
    @Expose
    private Scope scope;
    public final static Creator<Endpoint> CREATOR = new Creator<Endpoint>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Endpoint createFromParcel(Parcel in) {
            return new Endpoint(in);
        }

        public Endpoint[] newArray(int size) {
            return (new Endpoint[size]);
        }

    }
    ;

    protected Endpoint(Parcel in) {
        this.cookie = ((Cookie) in.readValue((Cookie.class.getClassLoader())));
        this.endpointId = ((String) in.readValue((String.class.getClassLoader())));
        this.scope = ((Scope) in.readValue((Scope.class.getClassLoader())));
    }

    public Endpoint() {
    }

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cookie);
        dest.writeValue(endpointId);
        dest.writeValue(scope);
    }

    public int describeContents() {
        return  0;
    }

}
