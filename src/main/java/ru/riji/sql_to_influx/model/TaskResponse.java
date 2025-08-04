package ru.riji.sql_to_influx.model;

import lombok.Data;

@Data
public class TaskResponse {
    private int id;
    private String name;
    private String lastRun;
    private long lastExecTime;
    private long lastRows;
    private String status;
    private SqlData data;


    public TaskResponse() {
    }
}
