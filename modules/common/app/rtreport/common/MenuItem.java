package rtreport.common;

public class MenuItem {

    public final static MenuItem[] EMPTY_ARRAY = new MenuItem[0];

    public String id, title, url, description;
    public long position = System.currentTimeMillis();

    public MenuItem(String id, String title, String url, String description) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.description = description;
    }
}
