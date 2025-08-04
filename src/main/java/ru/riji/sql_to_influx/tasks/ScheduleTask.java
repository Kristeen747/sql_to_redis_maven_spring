package ru.riji.sql_to_influx.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.riji.sql_to_influx.service.TaskService;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

@Data
public class ScheduleTask implements Runnable  {
    private int id;
    private int taskId;
    private long interval;
    private String name;
    private String cronExp;
    private LocalDateTime startAt;

    private TaskService taskService;
    private ScheduledFuture<?> scheduledFuture;

    public ScheduleTask(int id, int taskId, long interval, String name, LocalDateTime startAt, String cronExp) {
        this.id = id;
        this.taskId = taskId;
        this.interval = interval;
        this.name = name;
        this.startAt =startAt;
        this.cronExp=cronExp;
    }

    @Override
    public void run() {
        taskService.runById(taskId);
    }
}
