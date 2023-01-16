package org.apache.harmony.awt.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class TextFlavor {
    public static final Class[] charsetTextClasses = {InputStream.class, ByteBuffer.class, byte[].class};
    public static final Class[] unicodeTextClasses = {String.class, Reader.class, CharBuffer.class, char[].class};

    public static void addUnicodeClasses(SystemFlavorMap fm, String nat, String subType) {
        int i = 0;
        while (true) {
            Class[] clsArr = unicodeTextClasses;
            if (i < clsArr.length) {
                String type = "text/" + subType;
                DataFlavor f = new DataFlavor(String.valueOf(type) + (";class=\"" + clsArr[i].getName() + "\""), type);
                fm.addFlavorForUnencodedNative(nat, f);
                fm.addUnencodedNativeForFlavor(f, nat);
                i++;
            } else {
                return;
            }
        }
    }

    public static void addCharsetClasses(SystemFlavorMap fm, String nat, String subType, String charset) {
        int i = 0;
        while (true) {
            Class[] clsArr = charsetTextClasses;
            if (i < clsArr.length) {
                String type = "text/" + subType;
                DataFlavor f = new DataFlavor(String.valueOf(type) + (";class=\"" + clsArr[i].getName() + "\"" + ";charset=\"" + charset + "\""), type);
                fm.addFlavorForUnencodedNative(nat, f);
                fm.addUnencodedNativeForFlavor(f, nat);
                i++;
            } else {
                return;
            }
        }
    }
}
