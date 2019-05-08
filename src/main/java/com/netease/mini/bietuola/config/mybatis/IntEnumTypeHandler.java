package com.netease.mini.bietuola.config.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/28
 */
public class IntEnumTypeHandler<E extends Enum<E> & IntEnum> extends BaseTypeHandler<E> {

    private final Class<E> type;

    public IntEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getIntValue());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName);
        if (obj == null) {
            return null;
        } else {
//            int value = rs.getInt(columnName);
            return IntEnumCache.getEnumValue(this.type, (Integer) obj);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = rs.getObject(columnIndex);
        if (obj == null) {
            return null;
        } else {
//            int value = rs.getInt(columnName);
            return IntEnumCache.getEnumValue(this.type, (Integer) obj);
        }
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object obj = cs.getObject(columnIndex);
        if (obj == null) {
            return null;
        } else {
//            int value = rs.getInt(columnName);
            return IntEnumCache.getEnumValue(this.type, (Integer) obj);
        }
    }
}
