package cc.bocang.bocang.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xpHuang on 2016/8/21.
 */
public class StringUtil {

    /**
     * 为一个字符串列表的所有元素加上相同的前缀
     *
     * @param str  前缀
     * @param strs 字符串列表
     * @return List<String>
     */
    public static List<String> preToStringArray(String str, List<String> strs) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < strs.size(); i++) {
            String temp = str + strs.get(i);
            list.add(temp);
        }
        return list;
    }

    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof String) {
            return ((String) o).trim().isEmpty() ? true : false;
        } else {
            return false;
        }
    }

    public static String decodeUnicode(String theString) {

        char aChar;
        if (AppUtils.isEmpty(theString)) {
            return "";
        }
        int len = theString.length();

        StringBuffer outBuffer = new StringBuffer(len);

        for (int x = 0; x < len; ) {

            aChar = theString.charAt(x++);

            if (aChar == '\\') {

                aChar = theString.charAt(x++);

                if (aChar == 'u') {
                    int value = 0;

                    for (int i = 0; i < 4; i++) {

                        aChar = theString.charAt(x++);

                        switch (aChar) {

                            case '0':

                            case '1':

                            case '2':

                            case '3':

                            case '4':

                            case '5':

                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

}
