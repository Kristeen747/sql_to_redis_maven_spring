package ru.riji.sql_to_influx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.riji.sql_to_influx.dao.CsvTaskDao;
import ru.riji.sql_to_influx.dao.RedisDao;
import ru.riji.sql_to_influx.dao.TaskDao;
import ru.riji.sql_to_influx.dto.CsvTaskDto;
import ru.riji.sql_to_influx.dto.TaskDto;
import ru.riji.sql_to_influx.form.SqlTaskForm;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.Redis;
import ru.riji.sql_to_influx.model.SqlData;
import ru.riji.sql_to_influx.service.FileStorageService;
import ru.riji.sql_to_influx.service.RedisService;
import ru.riji.sql_to_influx.service.TaskService;
import ru.riji.sql_to_influx.tasks.CsvTask;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AppController {

    @Autowired
    TaskService taskService;
    @Autowired
    RedisService redisService;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private CsvTaskDao csvTaskDao;

    @Autowired
    private RedisDao redisDao;
//    @Autowired
//    private FlowService flowService;

    @Autowired
    private FileStorageService fileStorageService;


    @GetMapping(value = {"/tasks"})
    public ResponseEntity<?> tasks(RequestEntity<?> request){
        List<TaskDto> tasks =  taskDao.getAll().stream().map(TaskDto::new).collect(Collectors.toList());
        Map<String, List<TaskDto>> map = tasks.stream().collect(Collectors.groupingBy(x-> x.getGroupName() == null ? "" : x.getGroupName() , Collectors.toList()));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping(value = {"/update-tasks"})
    public ResponseEntity<?> updateTasks(RequestEntity<?> request){
        List<TaskDto> tasks =  taskDao.getUpdated().stream().map(TaskDto::new).collect(Collectors.toList());
        Map<String, List<TaskDto>> map = tasks.stream().collect(Collectors.groupingBy(x-> x.getGroupName() == null ? "" : x.getGroupName() , Collectors.toList()));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping(value = {"/start"})
    public ResponseEntity<?> start( @RequestParam("id") Integer id){
        taskService.runById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PostMapping(value = {"/clone"})
    public ResponseEntity<?> clone( @RequestParam("id") Integer id){
        taskService.clone(id);
        return new ResponseEntity<>(taskDao.getAll(), HttpStatus.OK);
    }
    @PostMapping(value = {"/delete"})
    public ResponseEntity<?> delete( @RequestParam("id") Integer id){
        taskService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping(value = {"/test"})
    public ResponseEntity<?> test(Model model, SqlTaskForm form)  {
        SqlData sqlData = taskService.test(form);
        return new ResponseEntity<>(sqlData, HttpStatus.OK);
    }

    @PostMapping(value = {"/save"})
    public ResponseEntity<?> saveSqlTask(Model model, SqlTaskForm form)  {
        if(form.getId() == 0){
            taskService.add(form);
        } else {
            taskService.update(form);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = {"/redis"})
    public ResponseEntity<?> getRedis(RequestEntity<?> request){
        List<Connect>  redisAll = redisDao.getAll();
        return new ResponseEntity<>(redisAll, HttpStatus.OK);
    }
    @PostMapping(value = {"/redisTables"})
    public ResponseEntity<?> getRedisTables(@RequestBody Redis form){
        return new ResponseEntity<>(redisService.getRedisTables(form), HttpStatus.OK);
    }
    @PostMapping(value = {"/redisKeys"})
    public ResponseEntity<?> getRedisKeys(@RequestBody  Redis form){
        return new ResponseEntity<>(new TreeSet<>(redisService.getRedisKeys(form)), HttpStatus.OK);
    }
    @PostMapping(value = {"/redisData"})
    public ResponseEntity<?> getRedisData(@RequestBody  Redis form){
        return new ResponseEntity<>(redisService.getRedisData(form), HttpStatus.OK);
    }
    @PostMapping(value = {"/deleteKey"})
    public ResponseEntity<?> deleteKey(@RequestBody  Redis form){
        return new ResponseEntity<>(redisService.deleteKey(form), HttpStatus.OK);
    }
    @PostMapping(value = {"/deleteDatabase"})
    public ResponseEntity<?> deleteDatabase(@RequestBody  Redis form){
        return new ResponseEntity<>(redisService.deleteDatabase(form), HttpStatus.OK);
    }

    @GetMapping(value = {"/tasks/csv"})
    public ResponseEntity<?> csvTasks(RequestEntity<?> request){
        List<CsvTaskDto> tasks =  csvTaskDao.getAll().stream().map(CsvTaskDto::new).collect(Collectors.toList());
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    @GetMapping(value = {"/tasks/csv/{id}/delete"})
    public ResponseEntity<?> deleteCsvTask(RequestEntity<?> request, @PathVariable("id") int id){
        CsvTask task =  csvTaskDao.getById(id);
        fileStorageService.deleteFile(task.getFilename());
        csvTaskDao.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }





}
