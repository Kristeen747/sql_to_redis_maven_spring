package ru.riji.sql_to_influx.model;


import lombok.Data;

@Data
public class Redis {
    private int redisId;
    private int redisTable;
    private String redisKey;
    private boolean unique;
}

