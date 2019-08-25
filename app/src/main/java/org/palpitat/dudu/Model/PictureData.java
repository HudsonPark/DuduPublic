package org.palpitat.dudu.Model;

public class PictureData {

    private long mDate;
    private String mfolderName;
    private String mImagePath;
    private String mTitle;

    public PictureData(String folderName , long date, String imagePath, String title) {
        this.mfolderName = folderName;
        this.mDate = date;
        this.mImagePath = imagePath;
        this.mTitle = title;
    }

    public String getfolderName() {
        return mfolderName;
    }

    public void setfolderName(String mfolderName) {
        this.mfolderName = mfolderName;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        this.mDate = date;
    }

    public String getImage() {
        return mImagePath;
    }

    public void setImage(String image) {
        this.mImagePath = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}
