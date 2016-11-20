package com.planb.jejupop;

/**
 * Created by Jeong on 2016-09-11.
 */
public class ListItem {
    private String[] mData;

    public ListItem(String[] data ){
        mData = data;
    }

    public ListItem(String title, String content, String imgname){
        mData = new String[3];
        mData[0] = title;
        mData[1] = content;
        mData[2] = imgname;
    }

    public String[] getData(){
        return mData;
    }

    public String getData(int index){
        return mData[index];
    }

    public void setData(String[] data){
        mData = data;
    }
}
