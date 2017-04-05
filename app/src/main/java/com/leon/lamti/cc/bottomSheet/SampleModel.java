package com.leon.lamti.cc.bottomSheet;

public class SampleModel {

    public String titleId;
    public int imageId;

    public SampleModel(String titleId, int imageId) {
        this.titleId = titleId;
        this.imageId = imageId;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}