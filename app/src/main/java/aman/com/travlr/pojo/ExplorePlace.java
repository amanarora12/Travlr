package aman.com.travlr.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aman on 20-08-2015.
 */
public class ExplorePlace implements Parcelable {
    private String id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String categoryName;
    private String ratings;
    private String tipCount;
    private String venuePhotoUrl;
    private String formattedAddress;
    private String postalCode;
    private String categoryIconUrl;
    private String ratingColor;

    public ExplorePlace(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        categoryName = in.readString();
        ratings = in.readString();
        tipCount = in.readString();
        venuePhotoUrl = in.readString();
        formattedAddress=in.readString();
        postalCode=in.readString();
        categoryIconUrl=in.readString();
        ratingColor=in.readString();
    }

    public static final Creator<ExplorePlace> CREATOR = new Creator<ExplorePlace>() {
        @Override
        public ExplorePlace createFromParcel(Parcel in) {
            return new ExplorePlace(in);
        }

        @Override
        public ExplorePlace[] newArray(int size) {
            return new ExplorePlace[size];
        }
    };

    public ExplorePlace() {

    }

    public void setId(String id){
        this.id=id;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public void setCity(String city){
        this.city=city;
    }
    public void setRatings(String ratings){
        this.ratings=ratings;
    }
    public void setCategoryName(String categoryName){
        this.categoryName=categoryName;
    }
    public void setTipCount(String tipCount){
        this.tipCount=tipCount;
    }
    public void setVenuePhotoUrl(String venuePhotoUrl){
        this.venuePhotoUrl=venuePhotoUrl;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public void setRatingColor(String ratingColor) {
        this.ratingColor = ratingColor;
    }

    public void setCategoryIconUrl(String categoryIconUrl) {
        this.categoryIconUrl = categoryIconUrl;
    }

    public String getId() {
        return id;
    }

    public String getFullAddress(){
        return address+","+city;
    }
    public String getName(){
        return name;
    }
    public String getCategoryName(){
        return categoryName;
    }
    public String getRatings(){
        return ratings;
    }
    public String getTipCount(){
        return tipCount;
    }
    public String getVenuePhotoUrl(){
        return venuePhotoUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCategoryIconUrl() {
        return categoryIconUrl;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getRatingColor() {
        return ratingColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(categoryName);
        dest.writeString(ratings);
        dest.writeString(tipCount);
        dest.writeString(venuePhotoUrl);
        dest.writeString(formattedAddress);
        dest.writeString(postalCode);
        dest.writeString(categoryIconUrl);
        dest.writeString(ratingColor);
    }
}
