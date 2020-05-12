package com.hfr.dao;

import com.hfr.bean.Detail;
import com.hfr.utils.JDBCUtils;

import java.sql.*;
import java.util.Date;

public class DetailDao {

    private static Connection connection = null;

    private static PreparedStatement ps = null;

    private static ResultSet rs = null;

    private static JDBCUtils jdbc = new JDBCUtils();

    /**
     * 保存
     *
     * @param d
     */
    public void saveDetail(Detail d) {
        connection = jdbc.getConnection();

        String sql = "insert into XIN_XI_INFO_TEST(ID,SOURCE_NAME,DETAIL_LINK,DETAIL_TITLE,DETAIL_CONTENT,PAGE_TIME,CREATE_TIME,LIST_TITLE,CREATE_BY) values (?,?,?,?,?,?,?,?,?)";
        try {
            ps = connection.prepareStatement(sql);

            ps.setString(1, d.getID());
            ps.setString(2, d.getSOURCE_NAME());
            ps.setString(3, d.getDETAIL_LINK());
            ps.setString(4, d.getDETAIL_TITLE());
            ps.setString(5, d.getDETAIL_CONTENT());
            ps.setString(6, d.getPAGE_TIME());
            ps.setTimestamp(7, new Timestamp(new Date().getTime()));
            ps.setString(8, d.getLIST_TITLE());
            ps.setString(9, d.getCREATE_BY());

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            jdbc.close(connection, ps, rs);
        }
    }

    public Detail findDetailById(String id) {
        Detail d = new Detail();

        connection = jdbc.getConnection();

        String sql = "select * from XIN_XI_INFO_TEST where ID = ?";

        try {
            ps = connection.prepareStatement(sql);

            ps.setString(1, id);

            rs = ps.executeQuery();

            //将结果集遍历显示出来
            while (rs.next()) {
                d.setID(rs.getString("ID"));
//                d.setSOURCE_NAME(rs.getString("SOURCE_NAME"));
//                d.setDETAIL_TITLE(rs.getString("DETAIL_TITLE"));
//                d.setDETAIL_LINK(rs.getString("DETAIL_LINK"));
//                d.setDETAIL_CONTENT(rs.getString("DETAIL_CONTENT"));
//                d.setCREATE_BY(rs.getString("CREATE_BY"));
//                d.setPAGE_TIME(rs.getString("PAGE_TIME"));
//                d.setLIST_TITLE(rs.getString("LIST_TITLE"));
//                d.setCREATE_TIME(rs.getTimestamp("CREATE_TIME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            jdbc.close(connection, ps, rs);
        }

        //结果集为空
        if(d.getID() == null){
            return null;
        }

        return d;
    }
}