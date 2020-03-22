
package com.example.vskfiretv.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Header implements Parcelable
{

    @SerializedName("payloadVersion")
    @Expose
    private String payloadVersion;
    @SerializedName("messageId")
    @Expose
    private String messageId;
    @SerializedName("namespace")
    @Expose
    private String namespace;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("correlationToken")
    @Expose
    private String correlationToken;
    public final static Creator<Header> CREATOR = new Creator<Header>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Header createFromParcel(Parcel in) {
            return new Header(in);
        }

        public Header[] newArray(int size) {
            return (new Header[size]);
        }

    }
    ;

    protected Header(Parcel in) {
        this.payloadVersion = ((String) in.readValue((String.class.getClassLoader())));
        this.messageId = ((String) in.readValue((String.class.getClassLoader())));
        this.namespace = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.correlationToken = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Header() {
    }

    public String getPayloadVersion() {
        return payloadVersion;
    }

    public void setPayloadVersion(String payloadVersion) {
        this.payloadVersion = payloadVersion;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCorrelationToken() {
        return correlationToken;
    }

    public void setCorrelationToken(String correlationToken) {
        this.correlationToken = correlationToken;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(payloadVersion);
        dest.writeValue(messageId);
        dest.writeValue(namespace);
        dest.writeValue(name);
        dest.writeValue(correlationToken);
    }

    public int describeContents() {
        return  0;
    }

}
