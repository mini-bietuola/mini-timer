package com.netease.mini.bietuola.config.nos;

import java.io.InputStream;

/**
 * 文件系统相关元数据信息
 *
 */
public class NosMetadata {

    private String objectName;

    private InputStream inputStream;

    private long size;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
