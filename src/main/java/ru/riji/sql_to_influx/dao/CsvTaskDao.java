package ru.riji.sql_to_influx.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.riji.sql_to_influx.form.ExcelForm;
import ru.riji.sql_to_influx.helpers.DbUtils;
import ru.riji.sql_to_influx.helpers.Utils;
import ru.riji.sql_to_influx.mappers.CsvTaskMapper;
import ru.riji.sql_to_influx.tasks.CsvTask;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CsvTaskDao  {

    @Autowired
    private CsvTaskMapper mapper;

    public List<CsvTask> getAll() {
        List<CsvTask> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(CsvTaskMapper.sql_all)
        ){
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                items.add(mapper.map(rs));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return items;
    }

    public int add(ExcelForm form) {
        String sql= "insert into csv_task(file_name, added_date, redis_id, redis_table, redis_key) values (?,?,?,?,?)";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql);
            PreparedStatement statementRowId = connection.prepareStatement("SELECT last_insert_rowid()")
        ){
            statement.setString(1, form.getFile().getOriginalFilename());
            statement.setString(2, LocalDateTime.now().format(Utils.formatter));
            statement.setInt(3, form.getRedisId());
            statement.setInt(4, form.getRedisTable());
            statement.setString(5, form.getRedisKey());

            int count = statement.executeUpdate();
            return statementRowId.executeQuery().getInt(1);

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

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


    public void delete(int id) {
        String sql= "delete from csv_task where id=?";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setInt(1, id);
            statement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public CsvTask getById(int id) {
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(CsvTaskMapper.sql_get_id)
        ){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                return mapper.map(rs);
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        return null;
    }

}
