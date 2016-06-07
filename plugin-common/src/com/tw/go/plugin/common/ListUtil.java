package com.tw.go.plugin.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

public class ListUtil {
    public static String join(Collection c) {
        return join(c, ", ");
    }

    public static String join(Collection c, String join) {
        StringBuffer sb = new StringBuffer();
        for (Iterator<Object> iterator = c.iterator(); iterator.hasNext(); ) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(join);
            }
        }
        return sb.toString();
    }

    public static String[] splitByFirstOrDefault(String args, Character separator) {
        if (!args.isEmpty()) {
            Character first = args.charAt(0);

            if (!(Character.isLetterOrDigit(first))) {
                separator = first;
                args = args.substring(1);
            }

            return args.split(Pattern.quote(separator.toString()));
        }

        return new String[]{};
    }
}