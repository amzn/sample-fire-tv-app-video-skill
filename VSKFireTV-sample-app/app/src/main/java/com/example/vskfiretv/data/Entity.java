
package com.example.vskfiretv.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entity implements Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("uri")
    @Expose
    private String uri;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("externalIds")
    @Expose
    private ExternalIds externalIds;
    public final static Creator<Entity> CREATOR = new Creator<Entity>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Entity createFromParcel(Parcel in) {
            return new Entity(in);
        }

        public Entity[] newArray(int size) {
            return (new Entity[size]);
        }

    }
    ;

    protected Entity(Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.uri = ((String) in.readValue((String.class.getClassLoader())));
        this.value = ((String) in.readValue((String.class.getClassLoader())));
        this.externalIds = ((ExternalIds) in.readValue((ExternalIds.class.getClassLoader())));
    }

    public Entity() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(uri);
        dest.writeValue(value);
        dest.writeValue(externalIds);
    }

    public int describeContents() {
        return  0;
    }

}
