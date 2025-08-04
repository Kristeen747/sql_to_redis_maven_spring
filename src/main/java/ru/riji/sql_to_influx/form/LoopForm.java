package ru.riji.sql_to_influx.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LoopForm {
    private int redisId;
    private int redisTable;
    private String redisKey;
    private String fieldName;
    private int startValue;
    private int endValue;
    private int step;

    public LoopForm() {
        this.startValue = 0;
        this.endValue = 100000;
        this.step = 1;
        this.fieldName = "id";
    }
}
