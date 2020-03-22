
package com.example.vskfiretv.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Directive_ implements Parcelable
{

    @SerializedName("payload")
    @Expose
    private Payload payload;
    @SerializedName("header")
    @Expose
    private Header header;
    @SerializedName("endpoint")
    @Expose
    private Endpoint endpoint;
    public final static Creator<Directive_> CREATOR = new Creator<Directive_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Directive_ createFromParcel(Parcel in) {
            return new Directive_(in);
        }

        public Directive_[] newArray(int size) {
            return (new Directive_[size]);
        }

    }
    ;

    protected Directive_(Parcel in) {
        this.payload = ((Payload) in.readValue((Payload.class.getClassLoader())));
        this.header = ((Header) in.readValue((Header.class.getClassLoader())));
        this.endpoint = ((Endpoint) in.readValue((Endpoint.class.getClassLoader())));
    }

    public Directive_() {
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(payload);
        dest.writeValue(header);
        dest.writeValue(endpoint);
    }

    public int describeContents() {
        return  0;
    }

}
