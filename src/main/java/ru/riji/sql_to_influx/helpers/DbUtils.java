package ru.riji.sql_to_influx.helpers;


import lombok.Data;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


@Data
@Service
public class DbUtils {
    private static final int connectionSize = 20;
    private static String url;

    private BlockingQueue<Connection> connections = new ArrayBlockingQueue<>(connectionSize);

    private String table_task =  "CREATE TABLE IF NOT EXISTS task (\n" +
            "    id                    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    redis_id              INTEGER REFERENCES redis (id) ON DELETE SET NULL,\n" +
            "    redis_key             CHAR,\n" +
            "    redis_table           INTEGER,\n" +
            "    group_name            CHAR,\n" +
            "    status                CHAR,\n" +
            "    last_run              CHAR,\n" +
            "    last_exec_time        INTEGER,\n" +
            "    last_rows             INTEGER,\n" +
            "    unique_data           BOOLEAN,\n " +
            "    task_type             CHAR,\n" +
            "    enable                BOOLEAN,\n" +
            "    added_date            CHAR,\n" +
            "    updated_date            CHAR,\n" +
            "    UNIQUE(redis_id,redis_key,redis_table)\n" +
            ")";

    private String table_sql_task = "CREATE TABLE IF NOT EXISTS sql_task (\n" +
            "    id                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    task_id           INTEGER REFERENCES task (id) ON DELETE CASCADE,\n" +
            "    query             CHAR,\n" +
            "    db_id             INTEGER REFERENCES db (id)\n" +
            ")";

    private String table_csv_task = "CREATE TABLE IF NOT EXISTS csv_task (\n" +
            "    id                INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    added_date        CHAR,\n" +
            "    file_name         CHAR,\n" +
            "    redis_id          INTEGER REFERENCES redis (id) ON DELETE SET NULL,\n" +
            "    redis_key         CHAR,\n" +
            "    redis_table       INTEGER\n" +
            ")";

    private String table_schedule_task = "CREATE TABLE IF NOT EXISTS schedule_task (\n" +
            "    id                  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    task_id             INTEGER REFERENCES task (id) ON DELETE CASCADE,\n" +
            "    interval            INTEGER\n" +
            ")";

    private String table_connect = "CREATE TABLE IF NOT EXISTS db (\n" +
            "    id     INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name   CHAR,\n" +
            "    url    CHAR,\n" +
            "    user   CHAR,\n" +
            "    pass   CHAR\n" +
            ")";

    private String table_redis = "CREATE TABLE IF NOT EXISTS redis (\n" +
            "    id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name CHAR,\n" +
            "    url  CHAR,\n" +
            "    user CHAR,\n" +
            "    pass CHAR\n" +
            ")";
    private String table_interval = "CREATE TABLE IF NOT EXISTS interval (\n" +
            "    id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    name CHAR,\n" +
            "    value  BIGINT\n" +
            ")";

    private String update_schedule_task = "ALTER TABLE schedule_task " +
            "ADD COLUMN start_at CHAR\n";

    private String update_schedule_task2 = "ALTER TABLE schedule_task " +
            "ADD COLUMN cron_exp CHAR\n";

    private String update_task_add_csv_path = "ALTER TABLE task ADD COLUMN csv_path CHAR";
    private String update_task_add_export_type = "ALTER TABLE task ADD COLUMN export_type CHAR";


    public static String getUrl() {
        return url;
    }

    @EventListener(ApplicationReadyEvent.class)
    void init() throws SQLException, IOException {
        String path= "";
        if(System.getProperty("os.name").contains("Windows")) {
            path = "C:/sqlite/db";
        }else {
            path = System.getProperty("user.home") + "/sql_to_redis/sqlite/db";
        }
        Path dbPath = Paths.get(path);
        if(Files.notExists(dbPath)) {
            Files.createDirectories(dbPath);
            System.out.println("dir created");
        }

        url = "jdbc:sqlite:" + path+"/pool.db";


        //  updateDb("DROP TABLE csv_task ");

        //     updateDb("DROP TABLE query_flow_tasks");

        updateDb(table_task);
        updateDb(table_sql_task);
        updateDb(table_csv_task);
        updateDb(table_schedule_task);
        updateDb(table_connect);
        updateDb(table_redis);
        updateDb(table_interval);


        try {
            updateDb(update_schedule_task);
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column")) {
                throw e;
            }
        }
        try {
            updateDb(update_schedule_task2);
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column")) {
                throw e;
            }
        }
        try {
            updateDb(update_task_add_csv_path);
        } catch (SQLException e){
            if (!e.getMessage().contains("duplicate column name")) {
                throw e;
            }
        }
        try {
            updateDb(update_task_add_export_type);
        } catch (SQLException e){
            if (!e.getMessage().contains("duplicate column name")) {
                throw e;
            }
        }


     //   updateDb(table_flow_tasks);

      //  updateDb(update_1);

        // new
     //   updateDb(update_task_add_updated_date);



   //     updateDb("DROP TABLE query_flow_tasks");

     //  updateDb(update_schedule_task2);

    //     updateDb(table_csv_task);
    //     updateDb(table_delay_task);

        //  updateDb(update_sql_task);
       // updateDb(insert_interval);
    }

    private void updateDb(String query) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement()){
            statement.execute(query);
        }
    }

}
