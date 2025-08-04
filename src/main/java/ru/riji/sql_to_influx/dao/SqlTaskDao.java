package ru.riji.sql_to_influx.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.riji.sql_to_influx.form.SqlTaskForm;
import ru.riji.sql_to_influx.helpers.DbUtils;
import ru.riji.sql_to_influx.mappers.SqlTaskMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SqlTaskDao  {

    @Autowired
    private SqlTaskMapper mapper;


//    public List<SqlTask> getAll() {
//        List<SqlTask> items = new ArrayList<>();
//        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
//            PreparedStatement statement = connection.prepareStatement(SqlTaskMapper.sql_all)
//        ){
//            ResultSet rs = statement.executeQuery();
//            while (rs.next()){
//                items.add(mapper.map(rs));
//            }
//        }catch (SQLException e){
//            System.out.println(e.getMessage());
//        }
//        return items;
//    }
//
//
//
//    public SqlTask getById(int id) {
//
//        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
//            PreparedStatement statement = connection.prepareStatement(SqlTaskMapper.sql_get_id)
//        ){
//            statement.setInt(1, id);
//            ResultSet rs = statement.executeQuery();
//            if (rs.next()){
//               return mapper.map(rs);
//            }
//        }catch (SQLException e){
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }
//
//
//    public List<SqlTask> getByGroupName(String name) {
//        List<SqlTask> items = new ArrayList<>();
//        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
//            PreparedStatement statement = connection.prepareStatement(SqlTaskMapper.sql_group_name)
//        ){
//            statement.setString(1, name);
//            ResultSet rs = statement.executeQuery();
//            while (rs.next()){
//                items.add(mapper.map(rs));
//            }
//        }catch (SQLException e){
//            System.out.println(e.getMessage());
//        }
//        return items;
//    }
//
// //   @Override
////    public int add(SqlTaskForm form) {
////        String sql= "insert into sql_task(name, group_name, query, db_id, redis_id, description, redis_table, unique_data) values (?,?,?,?,?,?,?,?)";
////
////        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
////            PreparedStatement statement = connection.prepareStatement(sql);
////            PreparedStatement statementRowId = connection.prepareStatement("SELECT last_insert_rowid()")
////        ){
////            statement.setString(1, form.getName());
////            statement.setString(2, form.getGroupName());
////            statement.setString(3, form.getQuery());
////            statement.setInt(4, form.getFromDbId());
////            statement.setInt(5, form.getToDbId());
////            statement.setString(6, form.getDescription());
////            statement.setInt(7, form.getRedisTable());
////            statement.setBoolean(8, form.isUniqueData());
////            int count = statement.executeUpdate();
////            return statementRowId.executeQuery().getInt(1);
////
////        }catch (SQLException e){
////            System.out.println(e.getMessage());
////        }
////        return 0;
////    }
//
////    @Override
//    public void update(SqlTaskForm form) {
////        String sql= "update sql_task set name=?, query=?, db_id=?, redis_id=?, description=?, group_name=?, redis_table=?, unique_data=? where id =?";
////
////        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
////            PreparedStatement statement = connection.prepareStatement(sql)
////        ){
////            statement.setString(1, form.getName());
////            statement.setString(2, form.getQuery());
////            statement.setInt(3, form.getFromDbId());
////            statement.setInt(4, form.getToDbId());
////            statement.setString(5, form.getDescription());
////            statement.setString(6, form.getGroupName());
////            statement.setInt(7, form.getRedisTable());
////            statement.setBoolean(8, form.isUniqueData());
////            statement.setInt(9, form.getId());
////            statement.executeUpdate();
////
////        }catch (SQLException e){
////            System.out.println(e.getMessage());
////        }
//    }
//
//
//
//
//    public void update(SqlTask task) {
//        String sql= "update sql_task set last_run=?, last_exec_time=?, last_rows=?, status = ? where id =?";
//
//        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
//            PreparedStatement statement = connection.prepareStatement(sql)
//        ){
//            statement.setString(1, task.getLastRun());
//            statement.setLong(2, task.getLastExecTime());
//            statement.setLong(3, task.getLastRows());
//            statement.setString(4, task.getStatus());
//            statement.setLong(5, task.getId());
//
//            statement.executeUpdate();
//
//        }catch (SQLException e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//
//    public void delete(int id) {
//        String pragma= "PRAGMA foreign_keys = ON";
//        String sql= "delete from sql_task where id=?";
//
//        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
//            PreparedStatement statement = connection.prepareStatement(sql);
//            Statement pragmaStatement = connection.createStatement();
//        ){
//            pragmaStatement.executeQuery(pragma);
//            statement.setInt(1, id);
//            statement.executeUpdate();
//
//        }catch (SQLException e){
//            System.out.println(e.getMessage());
//        }
//    }
//    public SqlTask getTaskByName(String name) {
//        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
//            PreparedStatement statement = connection.prepareStatement(SqlTaskMapper.sql_get_name)
//        ){
//            statement.setString(1, name);
//            ResultSet rs = statement.executeQuery();
//            if (rs.next()){
//                return mapper.map(rs);
//            }
//        }catch (SQLException e){
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }
//
//    public SqlTask getTaskByGroupAndName(String name, String group) {
//        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
//            PreparedStatement statement = connection.prepareStatement(SqlTaskMapper.sql_get_name_and_group)
//        ){
//            statement.setString(1, name);
//            statement.setString(2, group);
//            ResultSet rs = statement.executeQuery();
//            if (rs.next()){
//                return mapper.map(rs);
//            }
//        }catch (SQLException e){
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }
//
//    public int add(SqlTaskForm sqlTaskForm) {
//        return 0;
//    }
}
