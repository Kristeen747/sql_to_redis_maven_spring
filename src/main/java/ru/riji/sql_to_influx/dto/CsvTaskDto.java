package ru.riji.sql_to_influx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.tasks.CsvTask;

@Data
@AllArgsConstructor
public class CsvTaskDto {
    private int id;
    private String redisName;
    private String filename;
    private int redisTable;
    private String redisKey;
    private String addedDate;

    public CsvTaskDto(CsvTask task) {
        this.id = task.getId();
        this.redisName = task.getRedis().getName();
        this.filename = task.getFilename();
        this.redisTable = task.getRedisTable();
        this.redisKey = task.getRedisKey();
        this.addedDate = task.getAddedDate();
    }
}
