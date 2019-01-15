package it.wsh.cn.common_http.http.utils;

import android.os.Build;
import android.text.Html;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class HttpUrlEncodeUtils {

    /**
     * KEY_URL 编码
     * @param input 要编码的字符
     * @return 编码为 UTF-8 的字符串
     */
    public static String urlEncode(final String input) {
        try{
            return URLEncoder.encode(input, "UTF-8");
        }catch (UnsupportedEncodingException e){
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }

    }

    /**
     * KEY_URL 编码
     * @param input   要编码的字符
     * @param charset 字符集
     * @return 编码为字符集的字符串
     */
    public static String urlEncode(final String input, final String charset) {
        try {
            return URLEncoder.encode(input, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    /**
     * KEY_URL 解码
     * @param input 要解码的字符串
     * @return KEY_URL 解码后的字符串
     */
    public static String urlDecode(final String input) {
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    /**
     * KEY_URL 解码
     * @param input   要解码的字符串
     * @param charset 字符集
     * @return KEY_URL 解码为指定字符集的字符串
     */
    public static String urlDecode(final String input, final String charset) {
        try {
            return URLDecoder.decode(input, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    /**
     * Base64 编码
     *
     * @param input 要编码的字符串
     * @return Base64 编码后的字符串
     */
    public static byte[] base64Encode(final String input) {
        return base64Encode(input.getBytes());
    }

    /**
     * Base64 编码
     *
     * @param input 要编码的字节数组
     * @return Base64 编码后的字符串
     */
    public static byte[] base64Encode(final byte[] input) {
        return Base64.encode(input, Base64.NO_WRAP);
    }

    /**
     * Base64 编码
     *
     * @param input 要编码的字节数组
     * @return Base64 编码后的字符串
     */
    public static String base64Encode2String(final byte[] input) {
        return Base64.encodeToString(input, Base64.NO_WRAP);
    }

    /**
     * Base64 解码
     *
     * @param input 要解码的字符串
     * @return Base64 解码后的字符串
     */
    public static byte[] base64Decode(final String input) {
        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base64 解码
     *
     * @param input 要解码的字符串
     * @return Base64 解码后的字符串
     */
    public static byte[] base64Decode(final byte[] input) {
        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base64URL 安全编码
     * <p>将 Base64 中的 KEY_URL 非法字符�?,/=转为其他字符, 见 RFC3548</p>
     *
     * @param input 要 Base64URL 安全编码的字符串
     * @return Base64URL 安全编码后的字符串
     */
    public static byte[] base64UrlSafeEncode(final String input) {
        return Base64.encode(input.getBytes(), Base64.URL_SAFE);
    }

    /**
     * Html 编码
     *
     * @param input 要 Html 编码的字符串
     * @return Html 编码后的字符串
     */
    public static String htmlEncode(final CharSequence input) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0, len = input.length(); i < len; i++) {
            c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    sb.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    sb.append("&amp;"); //$NON-NLS-1$
                    break;
                case '\'':
                    sb.append("&#39;"); //$NON-NLS-1$
                    break;
                case '"':
                    sb.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Html 解码
     *
     * @param input 待解码的字符串
     * @return Html 解码后的字符串
     */
    @SuppressWarnings("deprecation")
    public static CharSequence htmlDecode(final String input) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(input);
        }
    }
}
