package ru.riji.sql_to_influx.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IMapper<T> {
   T map(ResultSet rs) throws SQLException;
}
