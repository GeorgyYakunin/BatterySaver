package com.example.batterysaver.Model;

public class Setting {
    private int avatar;
    private String title;
    private boolean isShow;
    private boolean isChecked;

    public Setting(int avatar, String title, boolean isShow, boolean isChecked) {
        this.avatar = avatar;
        this.title = title;
        this.isShow = isShow;
        this.isChecked = isChecked;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
