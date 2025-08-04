package ru.riji.sql_to_influx.model;

import lombok.Data;

import java.util.List;

@Data
public class SqlData {
    private String[] columnNames;
    private String[] columnTypes;
    private String[] customTypes;
    private int redisKeyIndex;
    private List<List<String>> rows;
    private String errorMessage;

    public SqlData(String[] columnNames, String[] columnTypes, String[]customTypes, int redisKeyIndex, List<List<String>> rows) {
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.customTypes = customTypes;
        this.redisKeyIndex = redisKeyIndex;
        this.rows = rows;
    }
    public SqlData(String errorMessage) {
        this.errorMessage =errorMessage;
    }
}
