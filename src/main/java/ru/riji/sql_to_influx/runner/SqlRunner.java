package ru.riji.sql_to_influx.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.riji.sql_to_influx.dao.DbDao;
import ru.riji.sql_to_influx.dao.RedisDao;
import ru.riji.sql_to_influx.dao.SqlTaskDao;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.SqlData;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class SqlRunner {

    @Autowired
    private DbDao dbDao;
    private Map<Integer, Connect> map = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    private void init(){
        dbDao.getAll().forEach(x->map.put(x.getId(), x));
    }


    public  SqlData runCommand(int connectId, String query) throws SQLException {
        Connect connect = map.get(connectId);
        try {
         //   Class.forName("org.postgresql.Driver");
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(
            Connection connection = DriverManager.getConnection(connect.getUrl(), connect.getUser(), connect.getPass());
            Statement stmt = connection.createStatement()){

            List<List<String>> rows = new ArrayList<>();

                 boolean results = stmt.execute(query);
                int count = 0;
                do {
                    if (results) {
                        ResultSet rs = stmt.getResultSet();
                        ResultSetMetaData rsmd = rs.getMetaData();

                 int columns = rsmd.getColumnCount();
                 String[] columnNames = new String[columns];
                 String[] columnTypes = new String[columns];
                 String[] customTypes = new String[columns];
                        int redisKeyIndex = 0;
                        for (int i = 0, j = 0; i < columns ; i++) {

                            String columnName =  rsmd.getColumnName(i + 1);
                            String tag = parseTag(columnName);
                            columnTypes[j] = rsmd.getColumnTypeName( i + 1);
                            columnNames[j] = removeTag(columnName,tag);
                            if(tag !=null) {
                                customTypes[j] = tag;
                            }
                            j++;
                        }
                        if(rs.next()){
                do{
                    List<String> row = new ArrayList<>();
                    for(int i = 0; i < columnNames.length; i++){
                       row.add(rs.getString(i+1));
                    }
                    rows.add(row);
                }while (rs.next());
            }
                        return new SqlData(columnNames, columnTypes, customTypes, redisKeyIndex, rows);
                    }
                    else { count = stmt.getUpdateCount(); }
                    results = stmt.getMoreResults();
                }
                while (results || count != -1);



        }catch (SQLException e){
            System.out.println(e.getMessage());
            return new SqlData(e.getMessage());
        }
        return null;
    }

    public Map<String,String> getParams(SqlData data){
        Map<String,String> map = new HashMap<>();
        StringBuilder[] arr = new StringBuilder[data.getCustomTypes().length];
        for (int i = 0; i < arr.length ; i++) {
            arr[i] = new StringBuilder();
        }

        String prefix= "";
        for(int i=0; i < data.getRows().size(); i++) {
            for (int j = 0; j < data.getCustomTypes().length; j++) {
                if(data.getCustomTypes()[j] != null){
                    switch (data.getColumnTypes()[j]) {
                        case "int4":
                        case "int8":
                        case "int2":
                        case "int":
                        case "serial8":
                        case "serial4":
                        case "serial2": {
                            arr[j].append(prefix).append(Long.parseLong(data.getRows().get(i).get(j)));
                            break;
                        }
                        case "float4":
                        case "float8":
                        case "numeric":
                        case "decimal": {
                            arr[j].append(prefix).append(Double.parseDouble(data.getRows().get(i).get(j)));
                            break;
                        }
                        default: {
                            arr[j].append(prefix).append("'").append(data.getRows().get(i).get(j)).append("'");
                            break;
                        }
                    }
                }
            }
            prefix=",";
        }
        for (int j = 0; j < data.getColumnTypes().length; j++) {
            if(data.getCustomTypes()[j] != null) {
                map.put(data.getColumnNames()[j], arr[j].toString());
            }
        }
        return map;
    }


    private static String parseTag(String columnName) {
        if(columnName.matches("(?i).*?_temp")){
            return "temp";
        } else {
            return null;
        }
    }
    private static String removeTag(String columnName, String tag) {
        return columnName.toLowerCase().replaceAll("(?i)_temp", "");
    }

}
