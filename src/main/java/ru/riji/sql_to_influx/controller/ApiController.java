package ru.riji.sql_to_influx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.riji.sql_to_influx.dao.SqlTaskDao;
import ru.riji.sql_to_influx.form.ExcelForm;
import ru.riji.sql_to_influx.form.KeyValueForm;
import ru.riji.sql_to_influx.model.RedisData;
import ru.riji.sql_to_influx.service.FileStorageService;
import ru.riji.sql_to_influx.service.RedisService;
import ru.riji.sql_to_influx.service.TaskService;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class ApiController {
    private static int counter;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private SqlTaskDao dao;

    @PostMapping(value = {"/api/run/{id}"})
    public ResponseEntity<?> runTask(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>(taskService.runById(id), HttpStatus.OK);
    }

    @PostMapping(value = {"/api/run/group/{name}"})
    public void runTask(@PathVariable(value = "name") String name) {
       taskService.runByGroup(name);
    }


    @GetMapping(value = {"/api/redis"}, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRedisData(@RequestParam(value = "redisId", required = true) int redisId,  @RequestParam(value = "redisTable", required = true) int redisTable,
                                          @RequestParam(value = "redisKey", required = true) String redisKey, @RequestParam(value = "unique", required = true) boolean unique) {
        String response = redisService.getRedisData(redisId,redisTable,redisKey,unique);
        if(response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = {"/api/redis"}, consumes = "application/json")
    public ResponseEntity<?> addRedisData(@RequestBody KeyValueForm redisData) {
        redisService.addKeyValue(redisData);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(value = {"/api/redis"})
    public ResponseEntity<?> addRedisData(ExcelForm form) {
        redisService.addExcel(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = {"/download/{filename}"})
    public ResponseEntity<?> addRedisData(@PathVariable("filename") String filename, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(filename);

        String contentType = null;

        try{
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        if(contentType == null){
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename()+"\"")
                .body(resource);
    }


}


