package ru.riji.sql_to_influx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ru.riji.sql_to_influx.dao.ScheduleTaskDao;
import ru.riji.sql_to_influx.form.ScheduleForm;
import ru.riji.sql_to_influx.tasks.ScheduleTask;

import java.time.*;
import java.util.*;

import static org.quartz.JobBuilder.newJob;

@Service
public class SchedulerService{

   @Autowired
   private TaskService taskService;
   @Autowired
   private ScheduleTaskDao scheduleTaskDao;
   private Map<Integer, ScheduleTask> tasks = new HashMap<>();
   @Autowired
   private ThreadPoolTaskScheduler scheduler;

   @EventListener(ApplicationReadyEvent.class)
   public void runAll() throws InterruptedException {
      System.out.println("start all");
      List<ScheduleTask> tasks = scheduleTaskDao.getAll();
      for( ScheduleTask task : tasks){
            runTask(task);
            Thread.sleep(60 - 60 / tasks.size());
         }
      }
   private void runTask(ScheduleTask task){
      task.setTaskService(taskService);
      LocalDateTime ldt = LocalDate.now().atTime(task.getStartAt().toLocalTime());

      if(ldt.isBefore(LocalDateTime.now())){
         ldt = ldt.plusDays(1);
      }
      Date out = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
      task.setScheduledFuture(scheduler.scheduleAtFixedRate(task, out, task.getInterval()));
      tasks.put(task.getId(), task);
   }

   public boolean stopTask(int id) {
     ScheduleTask task  =tasks.get(id);
      if(task!=null){
         task.getScheduledFuture().cancel(true);
         tasks.remove(id);
         scheduleTaskDao.delete(id);
      }
      return false;
   }

   public void add(ScheduleForm form) {
      int[] taskIds = form.getTaskId();
      for (int taskId : taskIds) {
         form.setTaskId(new int[]{taskId});
         int id = scheduleTaskDao.add(form);
         ScheduleTask task = scheduleTaskDao.getId(id);
         runTask(task);
      }
   }

   public void update(ScheduleForm form) {
      scheduleTaskDao.update(form);
   }

   public void delete(int id) {
      stopTask(id);
   }

   public List<ScheduleTask> getAll(){
      return scheduleTaskDao.getAll();
   }
}
