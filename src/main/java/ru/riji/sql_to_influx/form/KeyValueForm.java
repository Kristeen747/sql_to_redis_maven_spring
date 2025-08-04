package ru.riji.sql_to_influx.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeyValueForm {
    private int redisId;
    private int redisTable;
    private String redisKey;
    private String redisValue;

}
