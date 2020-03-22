
package com.example.vskfiretv.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExternalIds implements Parcelable
{

    @SerializedName("avc_vending_de")
    @Expose
    private String avcVendingDe;
    @SerializedName("ENTITY_ID")
    @Expose
    private String eNTITYID;
    @SerializedName("avc_vending_us")
    @Expose
    private String avcVendingUs;
    @SerializedName("avc_vending_jp")
    @Expose
    private String avcVendingJp;
    @SerializedName("netflix_jp")
    @Expose
    private String netflixJp;
    @SerializedName("netflix_de")
    @Expose
    private String netflixDe;
    @SerializedName("imdb")
    @Expose
    private String imdb;
    @SerializedName("netflix_gb")
    @Expose
    private String netflixGb;
    @SerializedName("ontv")
    @Expose
    private String ontv;
    @SerializedName("netflix_us")
    @Expose
    private String netflixUs;
    @SerializedName("tms")
    @Expose
    private String tms;
    @SerializedName("avc_vending_gb")
    @Expose
    private String avcVendingGb;
    @SerializedName("ontv_de")
    @Expose
    private String ontvDe;
    @SerializedName("gti")
    @Expose
    private String gti;
    public final static Creator<ExternalIds> CREATOR = new Creator<ExternalIds>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ExternalIds createFromParcel(Parcel in) {
            return new ExternalIds(in);
        }

        public ExternalIds[] newArray(int size) {
            return (new ExternalIds[size]);
        }

    }
    ;

    protected ExternalIds(Parcel in) {
        this.avcVendingDe = ((String) in.readValue((String.class.getClassLoader())));
        this.eNTITYID = ((String) in.readValue((String.class.getClassLoader())));
        this.avcVendingUs = ((String) in.readValue((String.class.getClassLoader())));
        this.avcVendingJp = ((String) in.readValue((String.class.getClassLoader())));
        this.netflixJp = ((String) in.readValue((String.class.getClassLoader())));
        this.netflixDe = ((String) in.readValue((String.class.getClassLoader())));
        this.imdb = ((String) in.readValue((String.class.getClassLoader())));
        this.netflixGb = ((String) in.readValue((String.class.getClassLoader())));
        this.ontv = ((String) in.readValue((String.class.getClassLoader())));
        this.netflixUs = ((String) in.readValue((String.class.getClassLoader())));
        this.tms = ((String) in.readValue((String.class.getClassLoader())));
        this.avcVendingGb = ((String) in.readValue((String.class.getClassLoader())));
        this.ontvDe = ((String) in.readValue((String.class.getClassLoader())));
        this.gti = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ExternalIds() {
    }

    public String getAvcVendingDe() {
        return avcVendingDe;
    }

    public void setAvcVendingDe(String avcVendingDe) {
        this.avcVendingDe = avcVendingDe;
    }

    public String getENTITYID() {
        return eNTITYID;
    }

    public void setENTITYID(String eNTITYID) {
        this.eNTITYID = eNTITYID;
    }

    public String getAvcVendingUs() {
        return avcVendingUs;
    }

    public void setAvcVendingUs(String avcVendingUs) {
        this.avcVendingUs = avcVendingUs;
    }

    public String getAvcVendingJp() {
        return avcVendingJp;
    }

    public void setAvcVendingJp(String avcVendingJp) {
        this.avcVendingJp = avcVendingJp;
    }

    public String getNetflixJp() {
        return netflixJp;
    }

    public void setNetflixJp(String netflixJp) {
        this.netflixJp = netflixJp;
    }

    public String getNetflixDe() {
        return netflixDe;
    }

    public void setNetflixDe(String netflixDe) {
        this.netflixDe = netflixDe;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getNetflixGb() {
        return netflixGb;
    }

    public void setNetflixGb(String netflixGb) {
        this.netflixGb = netflixGb;
    }

    public String getOntv() {
        return ontv;
    }

    public void setOntv(String ontv) {
        this.ontv = ontv;
    }

    public String getNetflixUs() {
        return netflixUs;
    }

    public void setNetflixUs(String netflixUs) {
        this.netflixUs = netflixUs;
    }

    public String getTms() {
        return tms;
    }

    public void setTms(String tms) {
        this.tms = tms;
    }

    public String getAvcVendingGb() {
        return avcVendingGb;
    }

    public void setAvcVendingGb(String avcVendingGb) {
        this.avcVendingGb = avcVendingGb;
    }

    public String getOntvDe() {
        return ontvDe;
    }

    public void setOntvDe(String ontvDe) {
        this.ontvDe = ontvDe;
    }

    public String getGti() {
        return gti;
    }

    public void setGti(String gti) {
        this.gti = gti;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(avcVendingDe);
        dest.writeValue(eNTITYID);
        dest.writeValue(avcVendingUs);
        dest.writeValue(avcVendingJp);
        dest.writeValue(netflixJp);
        dest.writeValue(netflixDe);
        dest.writeValue(imdb);
        dest.writeValue(netflixGb);
        dest.writeValue(ontv);
        dest.writeValue(netflixUs);
        dest.writeValue(tms);
        dest.writeValue(avcVendingGb);
        dest.writeValue(ontvDe);
        dest.writeValue(gti);
    }

    public int describeContents() {
        return  0;
    }

}
