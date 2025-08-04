package ru.riji.sql_to_influx.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.riji.sql_to_influx.form.SqlTaskForm;
import ru.riji.sql_to_influx.helpers.DbUtils;
import ru.riji.sql_to_influx.mappers.TaskMapper;
import ru.riji.sql_to_influx.model.TaskRow;
import ru.riji.sql_to_influx.tasks.Task;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskDao {
    @Autowired
    private TaskMapper taskMapper;

    public List<Task> getAll() {
        List<Task> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(TaskMapper.sql_all)
        ){
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                items.add(taskMapper.map(rs));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return items;
    }

    public List<Task> getAllSqlTask() {
        List<Task> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(TaskMapper.sql_by_type)
        ){
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                items.add(taskMapper.map(rs));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return items;
    }


    public List<String> getGroups() {
        List<String> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(TaskMapper.groups)
        ){
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                items.add(rs.getString(1));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return items;
    }

    public int add(SqlTaskForm form) {
        String insert_task= "insert into task(redis_id, redis_key, redis_table, group_name, task_type, enable, added_date, csv_path, export_type) values (?,?,?,?,?,?,?,?,?)";
        String insert_detail= "insert into sql_task(task_id, query, db_id) values (?,?,?) ";


        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(insert_task);
            PreparedStatement statementDetail = connection.prepareStatement(insert_detail);
            PreparedStatement statementRowId = connection.prepareStatement("SELECT last_insert_rowid()")
        ){
            statement.setInt(1, form.getRedisId());
            statement.setString(2, form.getName());
            statement.setInt(3, form.getRedisTable());
            statement.setString(4, form.getGroupName());
            statement.setString(5, "sql");
            statement.setBoolean(6,true);
            statement.setString(7, LocalDateTime.now().toString());
            statement.setString(8, form.getCsvPath());
            statement.setString(9, form.getExportType());
            int count = statement.executeUpdate();
            int taskId = statementRowId.executeQuery().getInt(1);

            // add task detail

//            StringBuilder sb = new StringBuilder();
//            sb.append(insert_detail);
//
//            String prefix = "";
//            for(int i = 0; i < form.getRows().size(); i++){
//                sb.append(prefix).append("(").append(taskId).append(",\"")
//                        .append(form.getRows().get(i).getQuery())
//                        .append("\",").append(form.getRows().get(i).getFromDbId())
//                        .append(")");
//                prefix = ",";
//            }

//            System.out.println(sb.toString());
//            PreparedStatement statementDetail = connection.prepareStatement(sb.toString());
//            statementDetail.executeUpdate();

            for(int i = 0; i < form.getRows().size(); i++){
                statementDetail.setInt(1, taskId);
                statementDetail.setString(2, form.getRows().get(i).getQuery());
                statementDetail.setInt(3, form.getRows().get(i).getFromDbId());
                statementDetail.executeUpdate();
            }

            return taskId;

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return 0;
    }


    public void update(Task task) {
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(TaskMapper.task_status_update)
        ){
            statement.setString(1, task.getLastRun());
            statement.setLong(2, task.getLastExecTime());
            statement.setLong(3, task.getLastRows());
            statement.setString(4, task.getStatus());
            statement.setLong(5, task.getId());

            statement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void update(SqlTaskForm form) {
        String sql= "delete from sql_task where task_id=?";
        String insert_detail= "insert into sql_task(task_id, query, db_id) values (?,?,?) ";
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(TaskMapper.task_update);
            PreparedStatement statementDetail = connection.prepareStatement(insert_detail);
            PreparedStatement statementRowId = connection.prepareStatement("SELECT last_insert_rowid()");
            PreparedStatement deleteStatement = connection.prepareStatement(sql)

        ){
            // delete sub_tasks
            deleteStatement.setInt(1, form.getId());
            deleteStatement.executeUpdate();

            // update task
            statement.setInt(1, form.getRedisId());
            statement.setInt(2, form.getRedisTable());
            statement.setString(3, form.getGroupName());
            statement.setString(4, form.getName());
            statement.setString(5, form.getCsvPath());
            statement.setString(6, form.getExportType());
            statement.setInt(7, form.getId());
            statement.executeUpdate();


//            StringBuilder sb = new StringBuilder();
//            sb.append(insert_detail);

//            String prefix = "";
//            for(int i = 0; i < form.getRows().size(); i++){
//                sb.append(prefix).append("(").append(form.getId()).append(",\"")
//                        .append(form.getRows().get(i).getQuery())
//                        .append("\",").append(form.getRows().get(i).getFromDbId())
//                        .append(")");
//                prefix = ",";
//            }
//
//            System.out.println(sb.toString());

            for(int i = 0; i < form.getRows().size(); i++){
                statementDetail.setInt(1, form.getId());
                statementDetail.setString(2, form.getRows().get(i).getQuery());
                statementDetail.setInt(3, form.getRows().get(i).getFromDbId());
                statementDetail.executeUpdate();
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void delete(int id) {
        String pragma= "PRAGMA foreign_keys = ON";
        String sql= "delete from task where id=?";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql);
            Statement pragmaStatement = connection.createStatement();
        ){
            pragmaStatement.execute(pragma);
            statement.setInt(1, id);
            statement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public Task getById(int id) {
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(TaskMapper.sql_by_id)
        ){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                return taskMapper.map(rs);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<TaskRow> getTasksById(int id) {
        String query = "select * from sql_task st where st.task_id = ? ";

        List<TaskRow> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(query)
        ){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                items.add(taskMapper.mapTaskRow(rs));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return items;
    }

    public List<Task> getTasksByGroup(String name) {
        List<Task> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(TaskMapper.sql_task_by_group)
        ){
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                items.add(taskMapper.map(rs));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return items;
    }

    public List<Task> getUpdated() {
        List<Task> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(TaskMapper.sql_all)
        ){
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                items.add(taskMapper.map(rs));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return items;
    }
}
