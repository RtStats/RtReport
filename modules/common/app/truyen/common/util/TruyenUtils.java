package truyen.common.util;

import org.apache.commons.lang3.ArrayUtils;

import truyen.common.bo.ConfDao;

import com.ibm.icu.text.Normalizer;

public class TruyenUtils {

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

    public static String conf(String key) {
        return ConfDao.getConf(key);
    }

    public static String denormalizeTCVN6909(String str) {
        if (str == null) {
            return "";
        }
        str = Normalizer.decompose(str.replace("đ", "d").replace("Đ", "D"), false);
        str = str.replaceAll("[\u0100-\uffff]+", "");
        return str;
    }
}
