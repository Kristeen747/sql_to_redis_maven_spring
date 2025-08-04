package ru.riji.sql_to_influx.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RedisStat {
    private Map<String, Integer> lists;
}
