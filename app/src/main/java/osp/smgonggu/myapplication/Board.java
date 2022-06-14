package osp.smgonggu.myapplication;

import android.graphics.Bitmap;

public class Board {

    private String UID;
    private String title;
    private String content;
    private String index;
    public Board() {}

    public Board(String UID,String title,String content,String index) {
        this.UID=UID;
        this.title=title;
        this.content=content;
        this.index=index;
    }

    public String getUID() {
        return UID;
    }

    public String getTitle() {
         return title;
    }

    public String getContent() {
        return content;
    }
    public String getIndex() {
        return index;
    }
}