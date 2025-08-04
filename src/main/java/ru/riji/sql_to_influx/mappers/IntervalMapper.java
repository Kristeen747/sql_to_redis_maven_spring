package ru.riji.sql_to_influx.mappers;

import org.springframework.stereotype.Component;
import ru.riji.sql_to_influx.model.Interval;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class IntervalMapper implements IMapper<Interval>{

    public static String sql_all= "select id, name, value from interval st";
    public static String sql_get_id= sql_all + " where st.id=?";

    @Override
    public Interval map(ResultSet rs) throws SQLException {
        return new Interval(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getLong("value")
        );
    }
}
