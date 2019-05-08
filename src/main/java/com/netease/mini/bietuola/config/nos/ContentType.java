package com.netease.mini.bietuola.config.nos;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件类型
 * @author hzlifagui
 *
 */
public enum ContentType {
    JPG("jpg", "image/jpeg"),JPEG("jpeg", "image/jpeg"),JPE("jpe", "image/jpeg"), JFIF("jfif", "image/jpeg"),
    TIF("tif", "image/tiff"),TIFF("tiff", "image/tiff"),
    FAX("fax", "image/fax"),
    GIF("gif", "image/gif"),
    ICO("ico", "image/x-icon"),
    NET("net", "image/pnetvue"),
    PNG("png", "image/png"),
    RP("rp", "image/vnd.rn-realpix"),
    WBMP("wbmp", "image/vnd.wap.wbmp"),
    BMP("bmp", "application/x-bmp"),
    BIN("bin", "application/octet-stream");

    private static Map<String, ContentType> m = Collections.synchronizedMap(new HashMap<String, ContentType>());
    private String suffix;
    private String ct;
    static {
        for(ContentType c : ContentType.values()) {
            m.put(c.getSuffix(), c);
        }
    }
    private ContentType(String suffix, String ct) {
        this.suffix = suffix;
        this.ct = ct;
    }

    public static ContentType getContentTypeBySuffix(String suffix) {
        suffix = StringUtils.trimToEmpty(suffix);
        if(StringUtils.isBlank(suffix)) {
            return BIN;
        }
        suffix = suffix.toLowerCase();
        ContentType ct = m.get(suffix);
        if(ct == null) {
            ct = BIN;
        }
        return ct;
    }
    /**
     * 获取suffix
     * @return suffix suffix
     */
    public String getSuffix() {
        return suffix;
    }
    /**
     * 获取ct
     * @return ct ct
     */
    public String getCt() {
        return ct;
    }

}

