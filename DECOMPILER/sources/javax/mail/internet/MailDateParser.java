package javax.mail.internet;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.text.ParseException;

/* compiled from: MailDateFormat */
class MailDateParser {
    int index = 0;
    char[] orig = null;

    public MailDateParser(char[] orig2) {
        this.orig = orig2;
    }

    public void skipUntilNumber() throws ParseException {
        while (true) {
            try {
                char[] cArr = this.orig;
                int i = this.index;
                switch (cArr[i]) {
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
                        return;
                    default:
                        this.index = i + 1;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParseException("No Number Found", this.index);
            }
        }
    }

    public void skipWhiteSpace() {
        int len = this.orig.length;
        while (true) {
            int i = this.index;
            if (i < len) {
                switch (this.orig[i]) {
                    case 9:
                    case 10:
                    case 13:
                    case ' ':
                        this.index = i + 1;
                    default:
                        return;
                }
            } else {
                return;
            }
        }
    }

    public int peekChar() throws ParseException {
        int i = this.index;
        char[] cArr = this.orig;
        if (i < cArr.length) {
            return cArr[i];
        }
        throw new ParseException("No more characters", this.index);
    }

    public void skipChar(char c) throws ParseException {
        int i = this.index;
        char[] cArr = this.orig;
        if (i >= cArr.length) {
            throw new ParseException("No more characters", this.index);
        } else if (cArr[i] == c) {
            this.index = i + 1;
        } else {
            throw new ParseException("Wrong char", this.index);
        }
    }

    public boolean skipIfChar(char c) throws ParseException {
        int i = this.index;
        char[] cArr = this.orig;
        if (i >= cArr.length) {
            throw new ParseException("No more characters", this.index);
        } else if (cArr[i] != c) {
            return false;
        } else {
            this.index = i + 1;
            return true;
        }
    }

    public int parseNumber() throws ParseException {
        int length = this.orig.length;
        boolean gotNum = false;
        int result = 0;
        while (true) {
            int i = this.index;
            if (i < length) {
                switch (this.orig[i]) {
                    case '0':
                        result *= 10;
                        gotNum = true;
                        break;
                    case '1':
                        gotNum = true;
                        result = (result * 10) + 1;
                        break;
                    case '2':
                        gotNum = true;
                        result = (result * 10) + 2;
                        break;
                    case '3':
                        gotNum = true;
                        result = (result * 10) + 3;
                        break;
                    case '4':
                        gotNum = true;
                        result = (result * 10) + 4;
                        break;
                    case '5':
                        gotNum = true;
                        result = (result * 10) + 5;
                        break;
                    case '6':
                        gotNum = true;
                        result = (result * 10) + 6;
                        break;
                    case '7':
                        gotNum = true;
                        result = (result * 10) + 7;
                        break;
                    case '8':
                        gotNum = true;
                        result = (result * 10) + 8;
                        break;
                    case '9':
                        gotNum = true;
                        result = (result * 10) + 9;
                        break;
                    default:
                        if (gotNum) {
                            return result;
                        }
                        throw new ParseException("No Number found", this.index);
                }
                this.index = i + 1;
            } else if (gotNum) {
                return result;
            } else {
                throw new ParseException("No Number found", this.index);
            }
        }
    }

    public int parseMonth() throws ParseException {
        try {
            char[] cArr = this.orig;
            int i = this.index;
            int i2 = i + 1;
            this.index = i2;
            switch (cArr[i]) {
                case 'A':
                case 'a':
                    int i3 = i2 + 1;
                    this.index = i3;
                    char curr = cArr[i2];
                    if (curr != 'P') {
                        if (curr != 'p') {
                            if (curr == 'U' || curr == 'u') {
                                this.index = i3 + 1;
                                char curr2 = cArr[i3];
                                if (curr2 == 'G' || curr2 == 'g') {
                                    return 7;
                                }
                            }
                        }
                    }
                    this.index = i3 + 1;
                    char curr3 = cArr[i3];
                    if (curr3 == 'R' || curr3 == 'r') {
                        return 3;
                    }
                case 'D':
                case 'd':
                    int i4 = i2 + 1;
                    this.index = i4;
                    char curr4 = cArr[i2];
                    if (curr4 == 'E' || curr4 == 'e') {
                        this.index = i4 + 1;
                        char curr5 = cArr[i4];
                        if (curr5 == 'C' || curr5 == 'c') {
                            return 11;
                        }
                    }
                case 'F':
                case 'f':
                    int i5 = i2 + 1;
                    this.index = i5;
                    char curr6 = cArr[i2];
                    if (curr6 == 'E' || curr6 == 'e') {
                        this.index = i5 + 1;
                        char curr7 = cArr[i5];
                        if (curr7 == 'B' || curr7 == 'b') {
                            return 1;
                        }
                    }
                case 'J':
                case 'j':
                    int i6 = i2 + 1;
                    this.index = i6;
                    switch (cArr[i2]) {
                        case 'A':
                        case 'a':
                            this.index = i6 + 1;
                            char curr8 = cArr[i6];
                            if (curr8 == 'N' || curr8 == 'n') {
                                return 0;
                            }
                        case 'U':
                        case 'u':
                            this.index = i6 + 1;
                            char curr9 = cArr[i6];
                            if (curr9 == 'N') {
                                return 5;
                            }
                            if (curr9 == 'n') {
                                return 5;
                            }
                            if (curr9 == 'L' || curr9 == 'l') {
                                return 6;
                            }
                    }
                    break;
                case 'M':
                case 'm':
                    int i7 = i2 + 1;
                    this.index = i7;
                    char curr10 = cArr[i2];
                    if (curr10 == 'A' || curr10 == 'a') {
                        this.index = i7 + 1;
                        char curr11 = cArr[i7];
                        if (curr11 == 'R') {
                            return 2;
                        }
                        if (curr11 == 'r') {
                            return 2;
                        }
                        if (curr11 == 'Y' || curr11 == 'y') {
                            return 4;
                        }
                    }
                case 'N':
                case 'n':
                    int i8 = i2 + 1;
                    this.index = i8;
                    char curr12 = cArr[i2];
                    if (curr12 == 'O' || curr12 == 'o') {
                        this.index = i8 + 1;
                        char curr13 = cArr[i8];
                        if (curr13 == 'V' || curr13 == 'v') {
                            return 10;
                        }
                    }
                case 'O':
                case 'o':
                    int i9 = i2 + 1;
                    this.index = i9;
                    char curr14 = cArr[i2];
                    if (curr14 == 'C' || curr14 == 'c') {
                        this.index = i9 + 1;
                        char curr15 = cArr[i9];
                        if (curr15 == 'T' || curr15 == 't') {
                            return 9;
                        }
                    }
                case 'S':
                case 's':
                    int i10 = i2 + 1;
                    this.index = i10;
                    char curr16 = cArr[i2];
                    if (curr16 == 'E' || curr16 == 'e') {
                        this.index = i10 + 1;
                        char curr17 = cArr[i10];
                        if (curr17 == 'P' || curr17 == 'p') {
                            return 8;
                        }
                    }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        throw new ParseException("Bad Month", this.index);
    }

    public int parseTimeZone() throws ParseException {
        int i = this.index;
        char[] cArr = this.orig;
        if (i < cArr.length) {
            char test = cArr[i];
            if (test == '+' || test == '-') {
                return parseNumericTimeZone();
            }
            return parseAlphaTimeZone();
        }
        throw new ParseException("No more characters", this.index);
    }

    public int parseNumericTimeZone() throws ParseException {
        boolean switchSign = false;
        char[] cArr = this.orig;
        int i = this.index;
        this.index = i + 1;
        char first = cArr[i];
        if (first == '+') {
            switchSign = true;
        } else if (first != '-') {
            throw new ParseException("Bad Numeric TimeZone", this.index);
        }
        int tz = parseNumber();
        int offset = ((tz / 100) * 60) + (tz % 100);
        if (switchSign) {
            return -offset;
        }
        return offset;
    }

    public int parseAlphaTimeZone() throws ParseException {
        int result;
        boolean foundCommon = false;
        try {
            char[] cArr = this.orig;
            int i = this.index;
            int i2 = i + 1;
            this.index = i2;
            switch (cArr[i]) {
                case 'C':
                case 'c':
                    result = 360;
                    foundCommon = true;
                    break;
                case 'E':
                case 'e':
                    result = 300;
                    foundCommon = true;
                    break;
                case 'G':
                case 'g':
                    int i3 = i2 + 1;
                    this.index = i3;
                    char curr = cArr[i2];
                    if (curr == 'M' || curr == 'm') {
                        this.index = i3 + 1;
                        char curr2 = cArr[i3];
                        if (curr2 != 'T') {
                            if (curr2 == 't') {
                            }
                        }
                        result = 0;
                        break;
                    }
                    throw new ParseException("Bad Alpha TimeZone", this.index);
                case 'M':
                case 'm':
                    result = TypedValues.CycleType.TYPE_EASING;
                    foundCommon = true;
                    break;
                case 'P':
                case 'p':
                    result = 480;
                    foundCommon = true;
                    break;
                case 'U':
                case 'u':
                    this.index = i2 + 1;
                    char curr3 = cArr[i2];
                    if (curr3 != 'T') {
                        if (curr3 != 't') {
                            throw new ParseException("Bad Alpha TimeZone", this.index);
                        }
                    }
                    result = 0;
                    break;
                default:
                    throw new ParseException("Bad Alpha TimeZone", this.index);
            }
            if (!foundCommon) {
                return result;
            }
            int i4 = this.index;
            int i5 = i4 + 1;
            this.index = i5;
            char curr4 = cArr[i4];
            if (curr4 == 'S' || curr4 == 's') {
                this.index = i5 + 1;
                char curr5 = cArr[i5];
                if (curr5 == 'T' || curr5 == 't') {
                    return result;
                }
                throw new ParseException("Bad Alpha TimeZone", this.index);
            } else if (curr4 != 'D' && curr4 != 'd') {
                return result;
            } else {
                this.index = i5 + 1;
                char curr6 = cArr[i5];
                if (curr6 == 'T' || curr6 != 't') {
                    return result - 60;
                }
                throw new ParseException("Bad Alpha TimeZone", this.index);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Bad Alpha TimeZone", this.index);
        }
    }

    /* access modifiers changed from: package-private */
    public int getIndex() {
        return this.index;
    }
}
