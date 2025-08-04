package ru.riji.sql_to_influx.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.riji.sql_to_influx.dao.*;
import ru.riji.sql_to_influx.form.SqlTaskForm;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.SqlData;
import ru.riji.sql_to_influx.model.TaskResponse;
import ru.riji.sql_to_influx.runner.RedisRunner;
import ru.riji.sql_to_influx.runner.SqlRunner;
import ru.riji.sql_to_influx.tasks.Task;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

@Service
public class TaskService {
   @Autowired
   private TaskDao taskDao;
   @Autowired
   private SqlTaskDao dao;
   @Autowired
   private CsvTaskDao csvTaskDao;
   @Autowired
   private RedisDao redisDao;
   @Autowired
   private DbDao dbConnect;
   @Autowired
   private RedisRunner redisRunner;
   @Autowired
   private SqlRunner sqlRunner;

    private final CSVFormat csvFormat = CSVFormat.Builder.create()
            .setAllowMissingColumnNames(true)
            .setDelimiter(";")
            .build();

   private Map<Integer, Task> tasks = new HashMap<>();
   private Map<Integer, Connect> databases = new HashMap<>();

   @EventListener(ApplicationReadyEvent.class)
   private void init(){
      taskDao.getAll().forEach(x->tasks.put(x.getId(), x));
      dbConnect.getAll().forEach(x->databases.put(x.getId(), x));
   }

   public Task getTask(int id){
      return tasks.get(id);
   }

   public TaskResponse runById(int id) {
      Task task = taskDao.getById(id);
      task.setRows(taskDao.getTasksById(id));
      task.setTaskDao(taskDao);
      task.setRedisRunner(redisRunner);
      task.setSqlRunner(sqlRunner);
      return runTask(task);
   }

   public int clone(int id) {
      Task task = taskDao.getById(id);
      task.setRows(taskDao.getTasksById(id));
      task.setRedisKey(task.getRedisKey() + "_copy");
      int newId = taskDao.add(new SqlTaskForm(task));
      return 0;
   }

   public SqlData test(SqlTaskForm form){
       Map<String, String> params = new HashMap<>();
       for (int i = 0; i < form.getRows().size() ; i++) {
           try {
               String replaced = StringSubstitutor.replace(form.getRows().get(i).getQuery(), params);
               SqlData data = sqlRunner.runCommand(form.getRows().get(i).getFromDbId(), replaced);
               if(Arrays.stream(data.getCustomTypes()).anyMatch(x -> Objects.equals(x, "temp"))){
                   params.putAll(sqlRunner.getParams(data));
               }
               if( i == form.getRows().size()-1) {
                   return data;
               }
           }catch (SQLException e){
               e.printStackTrace();
           }
       }
       return null;
   }



   public int add(SqlTaskForm form) {
      int id = taskDao.add(form);
      Task task = taskDao.getById(id);
      tasks.put(task.getId(), task);
      return id;
   }

   public void update(SqlTaskForm form) {
        taskDao.update(form);
   }

    private TaskResponse runTaskById(int id){
       Task task = taskDao.getById(id);
       return runTask(task);
    }

    private TaskResponse runTask(Task task){
      ExecutorService executorService = Executors.newSingleThreadExecutor();
       TaskResponse response = null;
       try {
          response = executorService.submit(task).get();
       } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
       }
        return response;
    }

    private List<TaskResponse> runTasks(List<Task> tasks) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void delete(Integer id) {
       Task task = taskDao.getById(id);
       if(task != null) {
           redisRunner.deleteData(task.getRedisId(), task.getRedisTable(), task.getRedisKey());
       }
      taskDao.delete(id);
   }

    public void runByGroup(String name) {
        List<Task> tasks = taskDao.getTasksByGroup(name);
        tasks.forEach(x-> {
            x.setRows(taskDao.getTasksById(x.getId()));
            x.setTaskDao(taskDao);
            x.setSqlRunner(sqlRunner);
            x.setRedisRunner(redisRunner);
        });
        runTasks(tasks);
    }

}
