package vngup.rtreports.common;

public class MenuItem {

    public final static MenuItem[] EMPTY_ARRAY = new MenuItem[0];

    public String id, title, url;
    public long position = System.currentTimeMillis();

    public MenuItem() {
    }

    public MenuItem(String id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }
}
