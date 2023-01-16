package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MimeTypeFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

public class MimetypesFileTypeMap extends FileTypeMap {
    private static final int PROG = 0;
    private static MimeTypeFile defDB = null;
    private static String defaultType = "application/octet-stream";
    private MimeTypeFile[] DB;

    public MimetypesFileTypeMap() {
        MimeTypeFile mf;
        Vector dbv = new Vector(5);
        dbv.addElement((Object) null);
        LogSupport.log("MimetypesFileTypeMap: load HOME");
        try {
            String user_home = System.getProperty("user.home");
            if (!(user_home == null || (mf = loadFile(String.valueOf(user_home) + File.separator + ".mime.types")) == null)) {
                dbv.addElement(mf);
            }
        } catch (SecurityException e) {
        }
        LogSupport.log("MimetypesFileTypeMap: load SYS");
        try {
            MimeTypeFile mf2 = loadFile(String.valueOf(System.getProperty("java.home")) + File.separator + "lib" + File.separator + "mime.types");
            if (mf2 != null) {
                dbv.addElement(mf2);
            }
        } catch (SecurityException e2) {
        }
        LogSupport.log("MimetypesFileTypeMap: load JAR");
        loadAllResources(dbv, "mime.types");
        LogSupport.log("MimetypesFileTypeMap: load DEF");
        synchronized (MimetypesFileTypeMap.class) {
            if (defDB == null) {
                defDB = loadResource("/mimetypes.default");
            }
        }
        MimeTypeFile mimeTypeFile = defDB;
        if (mimeTypeFile != null) {
            dbv.addElement(mimeTypeFile);
        }
        MimeTypeFile[] mimeTypeFileArr = new MimeTypeFile[dbv.size()];
        this.DB = mimeTypeFileArr;
        dbv.copyInto(mimeTypeFileArr);
    }

    private MimeTypeFile loadResource(String name) {
        InputStream clis = null;
        try {
            clis = SecuritySupport.getResourceAsStream(getClass(), name);
            if (clis != null) {
                MimeTypeFile mimeTypeFile = new MimeTypeFile(clis);
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types file: " + name);
                }
                if (clis != null) {
                    try {
                        clis.close();
                    } catch (IOException e) {
                    }
                }
                return mimeTypeFile;
            }
            if (LogSupport.isLoggable()) {
                LogSupport.log("MimetypesFileTypeMap: not loading mime types file: " + name);
            }
            if (clis == null) {
                return null;
            }
            try {
                clis.close();
                return null;
            } catch (IOException e2) {
                return null;
            }
        } catch (IOException e3) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MimetypesFileTypeMap: can't load " + name, e3);
            }
            if (clis == null) {
                return null;
            }
            clis.close();
            return null;
        } catch (SecurityException sex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MimetypesFileTypeMap: can't load " + name, sex);
            }
            if (clis == null) {
                return null;
            }
            clis.close();
            return null;
        } catch (Throwable th) {
            if (clis != null) {
                try {
                    clis.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    private void loadAllResources(Vector v, String name) {
        URL[] urls;
        InputStream clis;
        boolean anyLoaded = false;
        try {
            ClassLoader cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = getClass().getClassLoader();
            }
            if (cld != null) {
                urls = SecuritySupport.getResources(cld, name);
            } else {
                urls = SecuritySupport.getSystemResources(name);
            }
            if (urls != null) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MimetypesFileTypeMap: getResources");
                }
                for (URL url : urls) {
                    clis = null;
                    if (LogSupport.isLoggable()) {
                        LogSupport.log("MimetypesFileTypeMap: URL " + url);
                    }
                    try {
                        InputStream clis2 = SecuritySupport.openStream(url);
                        if (clis2 != null) {
                            v.addElement(new MimeTypeFile(clis2));
                            anyLoaded = true;
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types from URL: " + url);
                            }
                        } else if (LogSupport.isLoggable()) {
                            LogSupport.log("MimetypesFileTypeMap: not loading mime types from URL: " + url);
                        }
                        if (clis2 != null) {
                            try {
                                clis2.close();
                            } catch (IOException e) {
                            }
                        }
                    } catch (IOException ioex) {
                        if (LogSupport.isLoggable()) {
                            LogSupport.log("MimetypesFileTypeMap: can't load " + url, ioex);
                        }
                        if (clis != null) {
                            clis.close();
                        }
                    } catch (SecurityException sex) {
                        if (LogSupport.isLoggable()) {
                            LogSupport.log("MimetypesFileTypeMap: can't load " + url, sex);
                        }
                        if (clis != null) {
                            clis.close();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MimetypesFileTypeMap: can't load " + name, ex);
            }
        } catch (Throwable th) {
            if (clis != null) {
                try {
                    clis.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
        if (!anyLoaded) {
            LogSupport.log("MimetypesFileTypeMap: !anyLoaded");
            MimeTypeFile mf = loadResource("/" + name);
            if (mf != null) {
                v.addElement(mf);
            }
        }
    }

    private MimeTypeFile loadFile(String name) {
        try {
            return new MimeTypeFile(name);
        } catch (IOException e) {
            return null;
        }
    }

    public MimetypesFileTypeMap(String mimeTypeFileName) throws IOException {
        this();
        this.DB[0] = new MimeTypeFile(mimeTypeFileName);
    }

    public MimetypesFileTypeMap(InputStream is) {
        this();
        try {
            this.DB[0] = new MimeTypeFile(is);
        } catch (IOException e) {
        }
    }

    public synchronized void addMimeTypes(String mime_types) {
        MimeTypeFile[] mimeTypeFileArr = this.DB;
        if (mimeTypeFileArr[0] == null) {
            mimeTypeFileArr[0] = new MimeTypeFile();
        }
        this.DB[0].appendToRegistry(mime_types);
    }

    public String getContentType(File f) {
        return getContentType(f.getName());
    }

    public synchronized String getContentType(String filename) {
        int dot_pos = filename.lastIndexOf(".");
        if (dot_pos < 0) {
            return defaultType;
        }
        String file_ext = filename.substring(dot_pos + 1);
        if (file_ext.length() == 0) {
            return defaultType;
        }
        int i = 0;
        while (true) {
            MimeTypeFile[] mimeTypeFileArr = this.DB;
            if (i >= mimeTypeFileArr.length) {
                return defaultType;
            }
            MimeTypeFile mimeTypeFile = mimeTypeFileArr[i];
            if (mimeTypeFile != null) {
                String result = mimeTypeFile.getMIMETypeString(file_ext);
                if (result != null) {
                    return result;
                }
            }
            i++;
        }
    }
}
