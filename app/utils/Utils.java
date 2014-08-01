package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.ddth.plommon.utils.PlayAppUtils;

public class Utils {
    /**
     * If the webapp is running on
     * {@code http://site-name.domain.com:port/index}, this method returns the
     * {@code site-name} part.
     * 
     * @return
     */
    public static String getSiteName() {
        final Pattern PATTERN = Pattern.compile("^([\\w_-]+)");
        String host = PlayAppUtils.siteDomain();
        Matcher matcher = PATTERN.matcher(host);
        return matcher.find() ? matcher.group(1) : "";
    }
}
