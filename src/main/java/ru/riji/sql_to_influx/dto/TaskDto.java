package ru.riji.sql_to_influx.dto;

import lombok.Data;
import ru.riji.sql_to_influx.tasks.Task;

@Data
public class TaskDto {
    private int id;
    private String redisName;
    private String redisKey;
    private int redisTable;
    private String taskType;
    private String groupName;
    private String status;
    private String lastRun;
    private long lastExecTime;
    private int lastRows;
    private boolean uniqueData;

    public TaskDto(Task task) {
        this.id = task.getId();
    //    this.redisName = task.getRedis().getName();
        this.redisKey = task.getRedisKey();
        this.redisTable = task.getRedisTable();
        this.taskType = task.getTaskType();
        this.groupName = task.getGroupName();
        this.status = task.getStatus();
        this.lastRun = task.getLastRun();
        this.lastExecTime = task.getLastExecTime();
        this.lastRows = task.getLastRows();
        }
}
