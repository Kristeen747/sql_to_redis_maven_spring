package ru.riji.sql_to_influx.mappers;

import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class SqlTaskMapper  {

    public static String groups= "select distinct(st.group_name) from sql_task st";
    public static String sql_influx_dbs= "select distinct(st.influx_db) from sql_task st";
    public static String sql_all= "select st.id id, st.name name, st.group_name group_name, st.query query, st.last_run last_run, st.last_exec_time last_exec_time, st.last_rows last_rows, st.status status, db.id db_id, db.name db_name, db.url db_url, db.user db_user, db.pass db_pass, i.id redis_id, i.name redis_name, i.url redis_url, i.user redis_user, i.pass redis_pass, st.description description, st.redis_table, st.unique_data from sql_task st " +
        " join db on st.db_id = db.id " +
        " join redis i on st.redis_id = i.id";
    public static String sql_get_id= sql_all + " where st.id=?";
    public static String sql_get_name= sql_all + " where st.name=?";
    public static String sql_group_name= "select st.id id, st.name name, st.group_name group_name, st.query query, st.last_run last_run, st.last_exec_time last_exec_time, st.last_rows last_rows, st.status status,  db.id db_id, db.name db_name, db.url db_url, db.user db_user, db.pass db_pass, i.id redis_id, i.name redis_name, i.url redis_url, i.user redis_user, i.pass redis_pass, st.description description, st.redis_table , st.unique_data  from sql_task st " +
            " join db on st.db_id = db.id " +
            " join redis i on st.redis_id = i.id " +
            " where group_name = ?";
    public static String sql_get_name_and_group = sql_all + " where st.name=? and st.group_name=?";

//    @Override
//    public SqlTask map(ResultSet rs) throws SQLException {
//        return null; //new SqlTask(
//                rs.getInt("id"),
//                rs.getString("name"),
//                rs.getString("group_name"),
//                rs.getString("query"),
//                rs.getString("last_run"),
//                rs.getInt("last_exec_time"),
//                rs.getInt("last_rows"),
//                rs.getString("status"),
//                rs.getInt("db_id"),
//                rs.getString("db_name"),
//                rs.getString("db_url"),
//                rs.getString("db_user"),
//                rs.getString("db_pass"),
//                rs.getInt("redis_id"),
//                rs.getString("redis_name"),
//                rs.getString("redis_url"),
//                rs.getString("redis_user"),
//                rs.getString("redis_pass"),
//                rs.getString("description"),
//                rs.getInt("redis_table"),
//                rs.getBoolean("unique_data")
//        );
  //  }
}
