package ru.riji.sql_to_influx.mappers;

import org.springframework.stereotype.Component;
import ru.riji.sql_to_influx.tasks.CsvTask;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CsvTaskMapper implements IMapper<CsvTask> {

    public static String sql_all= "select ct.id id, ct.file_name file_name, ct.added_date added_date, i.id redis_id, i.name redis_name, i.url redis_url, i.user redis_user, i.pass redis_pass, ct.redis_table redis_table, ct.redis_key redis_key from csv_task ct " +
            " join redis i on ct.redis_id = i.id ";
    public static String sql_get_id = sql_all + " where ct.id = ?";

    @Override
    public CsvTask map(ResultSet rs) throws SQLException {
        return new CsvTask(
                rs.getInt("id"),
                rs.getInt("redis_id"),
                rs.getString("redis_name"),
                rs.getString("redis_url"),
                rs.getString("redis_user"),
                rs.getString("redis_pass"),
                rs.getString("redis_key"),
                rs.getInt("redis_table"),
                rs.getString("file_name"),
                rs.getString("added_date")
        );
    }
}
