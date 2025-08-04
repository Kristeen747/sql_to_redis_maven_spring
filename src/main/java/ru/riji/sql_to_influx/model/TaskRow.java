package ru.riji.sql_to_influx.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TaskRow {
    private int id;
    private String query;
    private int dbId;

    public TaskRow(int id, String query, int dbId) {
        this.id = id;
        this.query = query;
        this.dbId = dbId;
    }
}
