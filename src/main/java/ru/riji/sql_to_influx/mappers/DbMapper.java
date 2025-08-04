package ru.riji.sql_to_influx.mappers;

import org.springframework.stereotype.Repository;
import ru.riji.sql_to_influx.model.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class DbMapper implements IMapper<Connect>{
    public static String sql_all= "select id, name, url, user, pass from db";
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
