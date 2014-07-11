package vngup.rtreports.common;

import java.util.Comparator;

public class MenuItemComparator implements Comparator<MenuItem> {

    public final static MenuItemComparator instance = new MenuItemComparator();

    @Override
    public int compare(MenuItem mi1, MenuItem mi2) {
        return mi1.position < mi2.position ? -1 : (mi1.position > mi2.position ? 1 : 0);
    }
}
