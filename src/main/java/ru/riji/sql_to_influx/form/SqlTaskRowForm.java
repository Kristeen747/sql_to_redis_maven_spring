package ru.riji.sql_to_influx.form;

import lombok.Data;
import ru.riji.sql_to_influx.model.TaskRow;

@Data
public class SqlTaskRowForm {
    private int id;
    private int fromDbId;
    private String query;

    public SqlTaskRowForm() {
    }

    public SqlTaskRowForm(TaskRow task) {
        this.id = task.getId();
        this.fromDbId = task.getDbId();
        this.query = task.getQuery();
    }

}
