package ru.riji.sql_to_influx.mappers;

import org.springframework.stereotype.Component;
import ru.riji.sql_to_influx.tasks.ScheduleTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class ScheduleTaskMapper implements IMapper<ScheduleTask> {

    public static String sql_all= "select dt.id id, dt.task_id task_id, st.redis_key task_name, dt.interval interval, dt.start_at, dt.cron_exp from schedule_task dt " +
        " join task st on dt.task_id = st.id ";
    public static String sql_get_id = sql_all + " where dt.id = ?";

    @Override
    public ScheduleTask map(ResultSet rs) throws SQLException {
        return new ScheduleTask(
                rs.getInt("id"),
                rs.getInt("task_id"),
                rs.getLong("interval"),
                rs.getString("task_name"),
                rs.getString("start_at") !=null ?  LocalDateTime.parse(rs.getString("start_at")): LocalDateTime.now(),
                rs.getString("cron_exp")
        );
    }
}
