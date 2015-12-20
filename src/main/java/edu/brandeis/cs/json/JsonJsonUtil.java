package edu.brandeis.cs.json;

import java.util.logging.Logger;

/**
 * Created by 310201833 on 2015/10/28.
 */
public class JsonJsonUtil {

    private static Logger LOG = Logger.getLogger(JsonJsonUtil.class.getName());

    public boolean isNull(Object obj) {
        return obj == null;
    }

    public boolean has(Object obj, String s) {
        if (obj == null)
            return false;
//        LOG.info(String.format("JsonJsonUtil.has ( %s , %s) ", obj.toString(), s));
        return obj.toString().contains(s);
    }

    public boolean hasAny(Object obj, String... arr) {
        if (obj == null)
            return false;
        for (String s : arr) {
            if (obj.toString().contains(s))
                return true;
        }
        return false;
    }

    public boolean contains(Object obj, String s) {

        return obj != null && obj.toString().contains(s);
    }

    public boolean containsAny(Object obj, String... arr) {
        if (obj == null)
            return false;
        for (String s : arr) {
            if (obj.toString().contains(s))
                return true;
        }
        return false;
    }

    public boolean hasSubstring(Object obj, String s) {
        return obj != null && obj.toString().contains(s);
    }

    public boolean isSubstring(Object obj, String s) {
        return obj != null && obj.toString().contains(s);
    }

    public String toUpper(Object obj) {
        if (obj != null) {
            return obj.toString().trim().toUpperCase();
        }
        return null;
    }

    public String toLower(Object obj) {
        if (obj != null) {
            return obj.toString().trim().toLowerCase();
        }
        return null;
    }

    public String toCapital(Object obj) {
        if (obj != null) {
            String s = obj.toString().trim();
            if (s.length() > 1) {
                return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
            } else {
                return s.toUpperCase();
            }
        }
        return null;
    }


    static int id = 0;

    public String toId(Object obj) {
        if (obj != null)
            return obj.toString();
        LOG.info(String.format("JsonJsonUtil.toId ( %s) ", obj.toString()));
        return String.format("T%d", ++id);
    }

    public String lastWord(Object obj) {
        if (obj == null)
            return null;
        LOG.info(String.format("JsonJsonUtil.lastWord ( %s) ", obj.toString()));
        String[] arr = obj.toString().split("\\W");
        if (arr.length > 0)
            return arr[arr.length - 1];
        return null;
    }

    public String appendNewLine(Object obj) {
        StringBuilder sb = new StringBuilder();
        if (obj != null) {
            sb.append(obj.toString());
        }
        sb.append("\n");
        return sb.toString();
    }

    public String s_(Object obj){
        return obj.toString();
    }

    public int i_(Object obj){
        return Integer.parseInt(obj.toString());
    }

    public long l_(Object obj){
        return Long.parseLong(obj.toString());
    }

    public float f_(Object obj){
        return Float.parseFloat(obj.toString());
    }

    public boolean b_(Object obj){
        return Boolean.parseBoolean(obj.toString());
    }
}
