package ru.riji.sql_to_influx.service;

import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.riji.sql_to_influx.dao.CsvTaskDao;
import ru.riji.sql_to_influx.dao.RedisDao;
import ru.riji.sql_to_influx.dao.SqlTaskDao;
import ru.riji.sql_to_influx.form.ConnectForm;
import ru.riji.sql_to_influx.form.ExcelForm;
import ru.riji.sql_to_influx.form.KeyValueForm;
import ru.riji.sql_to_influx.form.LoopForm;
import ru.riji.sql_to_influx.model.Redis;
import ru.riji.sql_to_influx.runner.RedisRunner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class RedisService {
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SqlTaskDao sqlTaskDao;
    @Autowired
    private TaskService taskService;

    @Autowired
    private RedisRunner redisRunner;

    @Autowired
    private CsvTaskDao csvTaskDao;

   private final CSVFormat csvFormat = CSVFormat.Builder.create()
            .setAllowMissingColumnNames(true)
            .setDelimiter(";")
            .build();

   @Autowired
   private FileStorageService fileStorageService;

//    public String getRedisData(int id) {
//        Task task = taskService.getTask(id);
//        if(task != null) {
//            return RedisRunner.readData(task.getRedis(), task.getRedisTable(), task.getRedisKey(), task.isUniqueData());
//        }
//        return null;
//    }


    public String getRedisData(Redis form) {
        return redisRunner.readData(form.getRedisId(), form.getRedisTable(), form.getRedisKey());
    }

    public List<String> getRedisTables(Redis form) {
        return redisRunner.getTables(form.getRedisId());
    }
    public Set<String> getRedisKeys(Redis form) {
        return redisRunner.getKeys(form.getRedisId(), form.getRedisTable());
    }

    public String getRedisData(int redisId, int redisTable, String redisKey ,boolean unique) {
        return redisRunner.readData(redisId, redisTable, redisKey, unique);
    }

    public boolean deleteKey(Redis form) {
        redisRunner.deleteData(form.getRedisId(), form.getRedisTable(), form.getRedisKey());
        return true;
    }

    public boolean deleteDatabase(Redis form) {
       redisRunner.deleteDatabase(form.getRedisId(), form.getRedisTable());
        return true;
    }

    public void addKeyValue(KeyValueForm form) {
        redisRunner.writeData(form.getRedisId(), form.getRedisTable(), form.getRedisKey(), form.getRedisValue());
    }

    public void addExcel(ExcelForm form) {
        long start = System.currentTimeMillis();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(form.getFile().getInputStream(), Charset.forName("windows-1251")))) {
            CSVParser csvParser = csvFormat.parse(fileReader);
            List<CSVRecord> csvRecords = csvParser.getRecords();
           redisRunner.writeData(form.getRedisId(), form.getRedisTable(), form.getRedisKey(), csvRecords);
            fileStorageService.storeFile(form.getFile());
            csvTaskDao.add(form);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    public void addRedis(ConnectForm form) {
        redisRunner.addRedis(form);
    }

    public void updateRedis(ConnectForm form) {
        redisRunner.updateRedis(form);
    }

    public void addLoop(LoopForm form) {
        int startValue = form.getStartValue();
        int endValue = form.getEndValue();
        List<Integer> result = new ArrayList<>();
        if(startValue > endValue){
            while(startValue >= endValue ){
                result.add(startValue);
                startValue -= form.getStep();
            }
        } else {
            while (startValue <= endValue){
                result.add(startValue);
                startValue += form.getStep();
            }
        }
        redisRunner.writeData(form.getRedisId(), form.getRedisTable(), form.getRedisKey(), form.getFieldName(), result);
    }
}
