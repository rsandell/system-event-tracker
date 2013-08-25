/*
 * The MIT License
 *
 * Copyright (c) 2013, Robert Sandell - sandell.robert@gmail.com. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.joinedminds.tools.evet;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Useful functions.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public final class Functions {
    private Functions() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isEmpty(Object[] arr) {
        if (arr == null) {
            return true;
        } else {
            return arr.length <= 0;
        }
    }

    public static boolean isTrimmedEmpty(String str) {
        return str == null || (str.isEmpty() || str.trim().isEmpty());
    }

    public static String ifNullOrEmpty(String str, String thenValue) {
        if (isEmpty(str)) {
            return thenValue;
        } else {
            return str;
        }
    }

    public static <T> T ifNull(T obj, T thenVal) {
        return obj == null ? thenVal : obj;
    }

    public static String appendIfNotNull(String text, String suffix, String nullText) {
        return text == null ? nullText : text + suffix;
    }

    public static String prependIfNotNullOrEmpty(String text, String prefix, String nullText) {
        return text == null || text.isEmpty() ? nullText : prefix + text;
    }

    public static String emptyIfNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    public static String getRootUrl() {
        StaplerRequest request = Stapler.getCurrentRequest();
        if (request == null) {
            throw new IllegalStateException("Needs to be called from a http request thread.");
        }
        return request.getContextPath();
    }

    public static boolean contains(String needle, String[] haystack) {
        for (String s : haystack) {
            if(needle.equals(haystack)) {
                return true;
            }
        }
        return false;
    }
}
