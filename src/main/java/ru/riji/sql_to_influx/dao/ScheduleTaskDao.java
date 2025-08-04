package ru.riji.sql_to_influx.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.riji.sql_to_influx.form.ScheduleForm;
import ru.riji.sql_to_influx.helpers.DbUtils;
import ru.riji.sql_to_influx.mappers.ScheduleTaskMapper;
import ru.riji.sql_to_influx.tasks.ScheduleTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ScheduleTaskDao {

    @Autowired
    private ScheduleTaskMapper mapper;

    public List<ScheduleTask> getAll() {
        List<ScheduleTask> items = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(ScheduleTaskMapper.sql_all)
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

    public int add(ScheduleForm form) {
        String sql= "insert into schedule_task(task_id, interval, start_at, cron_exp) values (?,?,?,?)";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql);
            PreparedStatement statementRowId = connection.prepareStatement("SELECT last_insert_rowid()")
        ){
            statement.setInt(1, form.getTaskId()[0]);
            statement.setLong(2, form.getInterval());
            statement.setString(3, form.getStartAt().toString());
            statement.setString(4, form.getCronExp());

            int count = statement.executeUpdate();
            return statementRowId.executeQuery().getInt(1);

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void delete(int id) {
        String sql= "delete from schedule_task where id=?";

        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setInt(1, id);
            statement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public ScheduleTask getId(int id) {
        try(Connection connection = DriverManager.getConnection(DbUtils.getUrl());
            PreparedStatement statement = connection.prepareStatement(ScheduleTaskMapper.sql_get_id)
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

    public void update(ScheduleForm form) {

    }
}
