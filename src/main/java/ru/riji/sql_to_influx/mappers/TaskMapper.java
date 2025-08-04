package ru.riji.sql_to_influx.mappers;

import org.springframework.stereotype.Component;
import ru.riji.sql_to_influx.model.TaskRow;
import ru.riji.sql_to_influx.tasks.Task;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TaskMapper {
    public static String task_status_update= "update task set last_run=?, last_exec_time=?, last_rows=?, status = ? where id =?";
    public static String task_update= "update task set redis_id=?, redis_table=?, group_name=?, redis_key=?, csv_path=?, export_type=? where id=?";
    public static String sql_task_update= "update sql_task set db_id=?, query=? where task_id=?";
    public static String groups = "select distinct(group_name) from task";
    public static String sql_all= "select " +
            "t.id id, " +
            "t.redis_id redis_id, " +
            "t.redis_key redis_key, " +
            "t.redis_table redis_table, " +
            "t.group_name group_name, " +
            "t.status status, " +
            "t.last_run last_run, " +
            "t.last_exec_time last_exec_time, " +
            "t.last_rows last_rows, " +
            "t.unique_data unique_data, " +
            "t.task_type task_type, " +
            "t.csv_path csv_path, " +
            "t.export_type export_type, " +
            "r.name redis_name, " +
            "r.url redis_url, " +
            "r.user redis_user, " +
            "r.pass redis_pass " +
            "from task t " +
            "left join redis r on t.redis_id = r.id";

    public static String sql_task_all= "select " +
            "t.id id, " +
            "t.redis_id redis_id, " +
            "t.redis_key redis_key, " +
            "t.redis_table redis_table, " +
            "t.group_name group_name, " +
            "t.status status, " +
            "t.last_run last_run, " +
            "t.last_exec_time last_exec_time, " +
            "t.last_rows last_rows, " +
            "t.unique_data unique_data, " +
            "t.task_type task_type, " +
            "t.csv_path csv_path, " +
            "t.export_type export_type, " +
            "r.name redis_name, " +
            "r.url redis_url, " +
            "r.user redis_user, " +
            "r.pass redis_pass, " +
            "db.id db_id, " +
            "db.name db_name, " +
            "db.url db_url, " +
            "db.user db_user, " +
            "db.pass db_pass, " +
            "st.query query " +
            "from task t " +
            "join sql_task st on t.id = st.task_id " +
            "left join redis r on t.redis_id = r.id " +
            "join db on st.db_id = db.id";

    public static String sql_task_by_id = sql_task_all +
            " where t.id = ?";
    public static String sql_task_by_group = sql_task_all +
            " where t.group_name = ?";




    public static String sql_by_id = sql_all + " where t.id = ?";

    public static String sql_by_type = sql_all + " where t.task_type = 'sql'";
    public static String sql_tasks_by_group = sql_all + " where t.group_name = ?";

    public Task map(ResultSet rs) throws SQLException {
        return new Task(
                rs.getInt("id"),
                rs.getInt("redis_id"),
                rs.getString("redis_key"),
                rs.getInt("redis_table"),
                rs.getString("task_type"),
                rs.getString("group_name"),
                rs.getString("status"),
                rs.getString("last_run"),
                rs.getLong("last_exec_time"),
                rs.getInt("last_rows"),
                rs.getString("csv_path"),
                rs.getString("export_type")
        );
    }

    public TaskRow mapTaskRow(ResultSet rs) throws SQLException {
        return new TaskRow(
                rs.getInt("id"),
                rs.getString("query"),
                rs.getInt("db_id")
                );
    }
}
