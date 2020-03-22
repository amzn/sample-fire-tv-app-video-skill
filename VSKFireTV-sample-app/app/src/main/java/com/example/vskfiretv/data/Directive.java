
package com.example.vskfiretv.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Directive implements Parcelable
{

    @SerializedName("directive")
    @Expose
    private Directive_ directive;
    public final static Creator<Directive> CREATOR = new Creator<Directive>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Directive createFromParcel(Parcel in) {
            return new Directive(in);
        }

        public Directive[] newArray(int size) {
            return (new Directive[size]);
        }

    }
    ;

    protected Directive(Parcel in) {
        this.directive = ((Directive_) in.readValue((Directive_.class.getClassLoader())));
    }

    public Directive() {
    }

    public Directive_ getDirective() {
        return directive;
    }

    public void setDirective(Directive_ directive) {
        this.directive = directive;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(directive);
    }

    public int describeContents() {
        return  0;
    }

}
