package ru.riji.sql_to_influx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.riji.sql_to_influx.dao.*;
import ru.riji.sql_to_influx.form.*;
import ru.riji.sql_to_influx.model.TaskRow;
import ru.riji.sql_to_influx.service.*;
import ru.riji.sql_to_influx.tasks.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private SqlTaskDao sqlTaskDao;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private DbDao dbDao;
    @Autowired
    private IntervalDao intervalDao;
    @Autowired
    private CsvTaskDao csvTaskDao;
    @Autowired
    private ScheduleTaskDao delayTaskDao;
//    @Autowired
//    private FlowService flowService;

    @GetMapping(value ={"/sql"})
    public String index(Model model){
        return "sql";
    }

    @GetMapping(value ={"/api"})
    public String api(Model model){
        return "api";
    }


    @GetMapping(value ={"/settings"})
    public String settings(Model model){
        return "settings";
    }

    @GetMapping(value = "/deleteTask/{id}")
    public String deleteSqlTask(Model model, @PathVariable("id") int id){
        taskService.delete(id);
        return "redirect:/index";
    }
    @GetMapping(value = "/addRedis")
    public String addRedis(Model model){
        model.addAttribute("form", new ConnectForm());
        model.addAttribute("items", redisDao.getAll());
        return "addRedis";
    }
    @GetMapping(value = "/editRedis/{id}")
    public String editRedis(Model model, @PathVariable("id") int id){
        model.addAttribute("form", new ConnectForm(redisDao.getById(id)));
        model.addAttribute("items", redisDao.getAll());
        return "addRedis";
    }
    @PostMapping(value = "/addRedis")
    public String addRedis(Model model, ConnectForm form){
        if(form.getId() == 0){
           redisService.addRedis(form);
        }else{
            redisService.updateRedis(form);
        }
        return "redirect:/addRedis";
    }
    @GetMapping(value = "/deleteRedis")
    public String addInflux(Model model, int id){
         redisDao.delete(id);
        return "redirect:/addRedis";
    }
    @GetMapping(value = "/addDbConnect")
    public String addDbConnect(Model model){
        model.addAttribute("form", new ConnectForm());
        model.addAttribute("items", dbDao.getAll());
        return "addDbConnect";
    }
    @GetMapping(value = "/editDbConnect/{id}")
    public String editDbConnect(Model model, @PathVariable("id") int id){
        model.addAttribute("form", new ConnectForm(dbDao.getById(id)));
        model.addAttribute("items", dbDao.getAll());
        return "addDbConnect";
    }
    @PostMapping(value = "/addDbConnect")
    public String addDbConnect(Model model, ConnectForm form){
        if(form.getId() == 0){
            dbDao.add(form);
        }else{
            dbDao.update(form);
        }
        return "redirect:/addDbConnect";
    }
    @GetMapping(value = "/deleteDbConnect")
    public String deleteDbConnect(Model model, int id){
        dbDao.delete(id);
        return "redirect:/addDbConnect";
    }

    @GetMapping(value = "/addSqlTask")
    public String addSqlTask(Model model){
        model.addAttribute("toDbs", redisDao.getAll());
        model.addAttribute("fromDbs", dbDao.getAll());
        model.addAttribute("groups", taskDao.getGroups());
        model.addAttribute("form", new SqlTaskForm());
        return "addSqlTask";
    }

    @PostMapping(value = "/addSqlTask")
    public String addSqlTask(Model model, SqlTaskForm form){
        if(form.getId() == 0){
            taskService.add(form);
        }else{
            taskService.update(form);
        }
        return "redirect:/sql";
    }

    @GetMapping(value = "/addCsvTask")
    public String addCsvTask(Model model){
        model.addAttribute("toDbs", redisDao.getAll());
        model.addAttribute("form", new ExcelForm());
        model.addAttribute("groups", taskDao.getGroups());
        List<CsvTask> tasks =csvTaskDao.getAll();
        model.addAttribute("tasks", tasks);
        return "addCsvTask";
    }


    @GetMapping(value = "/addLoop")
    public String addLoop(Model model){
        model.addAttribute("toDbs", redisDao.getAll());
        model.addAttribute("form", new LoopForm());
        return "addLoop";
    }

    @PostMapping(value = "/addLoop")
    public String addLoop(Model model, LoopForm form){
        redisService.addLoop(form);
        return "redirect:/addLoop";
    }

    @GetMapping(value = "/addKeyValue")
    public String addKeyValue(Model model){
        model.addAttribute("toDbs", redisDao.getAll());
        model.addAttribute("form", new KeyValueForm());
        return "addKeyValue";
    }

    @PostMapping(value = "/addKeyValue")
    public String addKeyValue(Model model, KeyValueForm form){
        redisService.addKeyValue(form);
        return "redirect:/addKeyValue";
    }

    @GetMapping(value = "/editSqlTask/{id}")
    public String editSqlTask(Model model, @PathVariable("id") int id){
        model.addAttribute("toDbs", redisDao.getAll());
        model.addAttribute("fromDbs", dbDao.getAll());
        model.addAttribute("groups", taskDao.getGroups());
        Task task = taskDao.getById(id);
        task.setRows(taskDao.getTasksById(id));
        model.addAttribute("form", new SqlTaskForm(task));
        return "addSqlTask";
    }

    @GetMapping(value = "/editCsvTask/{id}")
    public String editCsvTask(Model model, @PathVariable("id") int id){
        model.addAttribute("toDbs", redisDao.getAll());
        model.addAttribute("form", new ExcelForm());
        return "addCsvTask";
    }


    @GetMapping(value = "/addSchedule")
    public String addSchedule(Model model){

        List<ScheduleTask> scheduleTasks = delayTaskDao.getAll();
        List<Task> tasks = taskDao.getAllSqlTask();

        model.addAttribute("intervals", intervalDao.getAll());
        model.addAttribute("form", new ScheduleForm());
        model.addAttribute("items", scheduleTasks);
        model.addAttribute("tasks", tasks);
        return "addSchedule";
    }
    @PostMapping(value = "/addSchedule")
    public String addSchedule(Model model, ScheduleForm form){
        if(form.getId() == 0){
            schedulerService.add(form);
        }else{
            schedulerService.update(form);
        }
        return "redirect:/addSchedule";
    }

    @PostMapping(value = "/deleteSchedule")
    public String deleteSchedule(Model model, DeleteForm form){
        schedulerService.delete(form.getId());
        return "redirect:/addSchedule";
    }

    @GetMapping(value = "/addInterval")
    public String addInterval(Model model){
        model.addAttribute("form", new IntervalForm());
        model.addAttribute("items", intervalDao.getAll());
        return "addInterval";
    }
    @GetMapping(value = "/editInterval/{id}")
    public String editInterval(Model model, @PathVariable("id") int id){
        model.addAttribute("form", new IntervalForm(intervalDao.getById(id)));
        model.addAttribute("items", intervalDao.getAll());
        return "addInterval";
    }
    @PostMapping(value = "/addInterval")
    public String addInterval(Model model, IntervalForm form){
        if(form.getId() == 0){
            intervalDao.add(form);
        }else{
            intervalDao.update(form);
        }
        return "redirect:/addInterval";
    }
    @GetMapping(value = "/deleteInterval")
    public String deleteInterval(Model model, DeleteForm form){
        intervalDao.delete(form.getId());
        return "redirect:/addInterval";
    }

    @GetMapping(value = {"/", "/index"})
    public String redisCreator(Model model){
        return "redisCreator";
    }

}
