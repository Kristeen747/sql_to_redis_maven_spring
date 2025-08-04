package ru.riji.sql_to_influx.form;

import lombok.Data;

@Data
public class ScheduleForm {
    private int id;
    private int[] taskId;
    private long interval;
    private String startAt;
    private String cronExp;

    public ScheduleForm() {
    }
}
