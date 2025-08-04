package ru.riji.sql_to_influx.mappers;

import org.springframework.stereotype.Component;
import ru.riji.sql_to_influx.model.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RedisMapper implements  IMapper<Connect>{
    public static String sql_all= "select id, name, url, user, pass from redis ";
    public static String sql_get_id= sql_all + " where id=?";

    @Override
    public Connect map(ResultSet rs) throws SQLException {
        return new Connect(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("url"),
                rs.getString("user"),
                rs.getString("pass")
        );
    }
}
