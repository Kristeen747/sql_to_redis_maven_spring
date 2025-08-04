package ru.riji.sql_to_influx.model;

import lombok.Data;

@Data
public class RedisData {
    private int redisId;
    private int redisTable;
    private String redisKey;
    private String value;
}
