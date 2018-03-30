package com.wanjun.canalsync.util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * Created with IntelliJ IDEA
 *
 * @author : ShiFeng
 * @date : 2018/3/30
 * @time : 13:38
 * Description:
 */

public class JsonTypeHandler extends BaseTypeHandler<List<?>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<?> parameter,
                                    JdbcType jdbcType) throws SQLException {

        ps.setString(i, JSONUtil.toJson(parameter));
    }

    @Override
    public List<?> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        if(rs.getString(columnName) == null) {
            return new ArrayList<>();
        }
        return JSONUtil.toList(rs.getString(columnName), Integer.class);
    }

    @Override
    public List<?> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if(rs.getString(columnIndex) == null) {
            return new ArrayList<>();
        }
        return JSONUtil.toList(rs.getString(columnIndex), Integer.class);
    }

    @Override
    public List<?> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        if(cs.getString(columnIndex) == null) {
            return new ArrayList<>();
        }
        return JSONUtil.toList(cs.getString(columnIndex), Integer.class);
    }

}
