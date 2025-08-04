package ru.riji.sql_to_influx.form;

import lombok.Data;
import ru.riji.sql_to_influx.model.TaskRow;
import ru.riji.sql_to_influx.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SqlTaskForm {
    private int id;
    private String groupName;
    private int redisId;
    private String name;
    private int redisTable;
    private String csvPath;
    private String exportType;

    private List<SqlTaskRowForm> rows = new ArrayList<>();



    public SqlTaskForm() {
        this.rows.add(new SqlTaskRowForm());
    }


    public SqlTaskForm(Task task) {
        this.id = task.getId();
        this.name = task.getRedisKey();
        this.groupName = task.getGroupName();
        this.redisId = task.getRedisId();
        this.redisTable = task.getRedisTable();
        this.csvPath = task.getCsvPath();
        this.exportType = task.getExportType();
        this.rows.addAll(task.getRows().stream().map(SqlTaskRowForm::new).collect(Collectors.toList()));
    }
}
