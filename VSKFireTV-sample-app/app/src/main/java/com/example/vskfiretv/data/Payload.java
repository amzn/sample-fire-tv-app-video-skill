
package com.example.vskfiretv.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Payload implements Parcelable
{

    @SerializedName("entities")
    @Expose
    private List<Entity> entities = null;
    public final static Creator<Payload> CREATOR = new Creator<Payload>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Payload createFromParcel(Parcel in) {
            return new Payload(in);
        }

        public Payload[] newArray(int size) {
            return (new Payload[size]);
        }

    }
    ;

    protected Payload(Parcel in) {
        in.readList(this.entities, (Entity.class.getClassLoader()));
    }

    public Payload() {
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(entities);
    }

    public int describeContents() {
        return  0;
    }

}
