package utils.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import play.mvc.Http.Request;

import com.github.ddth.plommon.utils.PlayAppUtils;

public class SiteUtils {

    private final static char[] TRUE_CHAR_ARR = { '1', 'y', 'Y', 't', 'T' };
    private final static String[] TRUE_STRING_ARR = { "1", "Y", "YES", "T", "TRUE" };

    public static boolean toBoolean(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue();
        }

        if (obj instanceof Number) {
            return ((Number) obj).longValue() != 0;
        }

        if (obj instanceof Character) {
            char c = ((Character) obj).charValue();
            return ArrayUtils.contains(TRUE_CHAR_ARR, c);
        }

        if (obj instanceof String) {
            String str = ((String) obj).toUpperCase().trim();
            return ArrayUtils.contains(TRUE_STRING_ARR, str);
        }

        return false;
    }

    public static String extractModuleName(Request request) {
        String appContext = PlayAppUtils.appConfigString("application.context");
        String uri = request.uri();
        if (!StringUtils.isBlank(appContext) && uri.startsWith(appContext)) {
            uri = uri.substring(appContext.length());
        }
        String[] tokens = StringUtils.split(uri, '/');
        return tokens != null && tokens.length > 0 ? tokens[0] : null;
    }

    /**
     * If the webapp is running on
     * {@code http://site-name.domain.com:port/index}, this method returns the
     * {@code site-name} part.
     * 
     * @return
     */
    public static String extractSiteName() {
        final Pattern PATTERN = Pattern.compile("^([\\w_-]+)");
        String host = PlayAppUtils.siteDomain();
        Matcher matcher = PATTERN.matcher(host);
        return matcher.find() ? matcher.group(1) : "";
    }
}
