package ru.riji.sql_to_influx.tasks;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.riji.sql_to_influx.dao.TaskDao;
import ru.riji.sql_to_influx.form.SqlTaskForm;
import ru.riji.sql_to_influx.form.SqlTaskRowForm;
import ru.riji.sql_to_influx.helpers.Utils;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.SqlData;
import ru.riji.sql_to_influx.model.TaskResponse;
import ru.riji.sql_to_influx.model.TaskRow;
import ru.riji.sql_to_influx.runner.CsvRunner;
import ru.riji.sql_to_influx.runner.RedisRunner;
import ru.riji.sql_to_influx.runner.SqlRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;

@Data
public class Task implements Callable<TaskResponse> {
    private int id;
    private int redisId;
    private String redisKey;
    private int redisTable;
    private String taskType;
    private String groupName;
    private String status;
    private String lastRun;
    private long lastExecTime;
    private int lastRows;
    private List<TaskRow> rows;
    private String csvPath;
    private String exportType;


    @Autowired
    private RedisRunner redisRunner;
    @Autowired
    private CsvRunner csvRunner;
    @Autowired
    private SqlRunner sqlRunner;
    @Autowired
    private TaskDao taskDao;

    public Task(int id, int redisId, String redisKey, int redisTable, String taskType, String groupName, String status, String lastRun, long lastExecTime, int lastRows, String csvPath, String exportType) {
        this.id = id;
        this.redisId = redisId;
        this.redisKey = redisKey;
        this.redisTable = redisTable;
        this.taskType = taskType;
        this.groupName = groupName;
        this.status = status;
        this.lastRun = lastRun;
        this.lastExecTime = lastExecTime;
        this.lastRows = lastRows;
        this.csvPath = csvPath;
        this.exportType = exportType;
    }

    public Task() {
    }

    private void resetStatus(){
        this.setLastRun(LocalDateTime.now().format(Utils.formatter));
        this.setStatus("run");
        this.setLastRows(0);
        this.setLastExecTime(0);
        taskDao.update(this);
    }

    @Override
    public TaskResponse call() throws Exception {
        resetStatus();
        Map<String, String> params = new HashMap<>();
        TaskResponse taskResponse = new TaskResponse();
        long start = System.currentTimeMillis();

        try {
        for (int i = 0; i < rows.size() ; i++) {
                String replaced = StringSubstitutor.replace(rows.get(i).getQuery(), params);
                SqlData data = sqlRunner.runCommand(rows.get(i).getDbId(), replaced);

                if(data.getErrorMessage() !=null){
                    this.setStatus("error");
                    break;
                } else {
                    if(Arrays.stream(data.getCustomTypes()).anyMatch(x -> Objects.equals(x, "temp"))){
                        params.putAll(sqlRunner.getParams(data));
                    }
                    if(i == rows.size()-1) {
                        if(this.getExportType().equals("csv")){
                            csvRunner.writeData(data, this.getCsvPath());
                        } else {
                            redisRunner.writeData(this.getRedisId(), this.getRedisTable(), data);
                        }
                        this.setLastExecTime((System.currentTimeMillis() - start) / 1000);
                        this.setLastRows(data.getRows().size());
                        this.setStatus("done");
                    }
                }
        }
            taskDao.update(this);
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }

        return taskResponse;
    }





}
