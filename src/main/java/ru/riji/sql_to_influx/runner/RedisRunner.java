package ru.riji.sql_to_influx.runner;

import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.riji.sql_to_influx.dao.RedisDao;
import ru.riji.sql_to_influx.form.ConnectForm;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.SqlData;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
public class RedisRunner {

    @Autowired
    private RedisDao redisDao;

    private Map<Integer, Connect> map = new HashMap<>();


    @EventListener(ApplicationReadyEvent.class)
    private void init(){
        redisDao.getAll().forEach(x-> map.put(x.getId(), x));
    }

    public void writeData(int redisId, int redisTable, SqlData data) {
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
               Jedis jedis = pool.getResource())  {
            jedis.select(redisTable);
            List<List<String>> rows = data.getRows();

           // long lenBefore = jedis.llen(key); //1
           // System.out.println("lenBefore" + lenBefore);

            Map<String, Integer> lengthsBefore = new HashMap<>();
            Map<String, Integer> lengthsAfter = new HashMap<>();

            Map<String, List<List<String>>>  groups = rows.stream().collect(Collectors.groupingBy(x->x.get(0)));
            for(Map.Entry<String, List<List<String>>> entry : groups.entrySet()) {
                long lenBefore = jedis.llen(entry.getKey()); //1
                for (List<String> row : entry.getValue()) {
                    JSONObject jo = new JSONObject();
                    for (int i = 1; i < row.size(); i++) {
                        jo.put(data.getColumnNames()[i], row.get(i));
                    }
                    jedis.lpush(row.get(0), jo.toString());
                }
                if (lenBefore > 0) {
                    long lenAfter = jedis.llen(entry.getKey()); // 2
                    System.out.println("lenAfter" + lenAfter);
                    // jedis.ltrim(key, lenBefore, lenAfter-1);
                    jedis.ltrim(entry.getKey(), 0, lenAfter - lenBefore - 1);
                    System.out.println("lenAfter update " + jedis.llen(entry.getKey()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void writeData(int redisId, int redisTable, String key, List<CSVRecord> csvRecords ) {
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
             Jedis jedis = pool.getResource())  {
           jedis.select(redisTable);

           long lenBefore = jedis.llen(key);

         String[] headers = csvRecords.get(0).values();

            for (int i=1; i< csvRecords.size(); i++) {
                JSONObject jo = new JSONObject();
                String[] records = csvRecords.get(i).values();
                for (int j = 0; j < records.length; j++) {
                    jo.put(headers[j], records[j]);
                }
                jedis.lpush(key, jo.toString());
            }

          if (lenBefore > 0) {
              long lenAfter = jedis.llen(key);
              jedis.ltrim(key, 0, lenAfter-lenBefore-1);
          }
      } catch (Exception e) {
          e.printStackTrace();
          System.out.println(e.getMessage());
      }
    }

    public void writeData(int redisId, int redisTable, String key, String fieldName,  List<Integer> nums ) {
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
             Jedis jedis = pool.getResource())  {
            jedis.select(redisTable);

            long lenBefore = jedis.llen(key);

            for (int i=0; i< nums.size(); i++) {
                JSONObject jo = new JSONObject();
                jo.put(fieldName, nums.get(i));
                jedis.lpush(key, jo.toString());
            }

            if (lenBefore > 0) {
                long lenAfter = jedis.llen(key);
                jedis.ltrim(key, 0, lenAfter-lenBefore-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


    public void writeData(int redisId, int redisTable, String key, String value ) {
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
             Jedis jedis = pool.getResource())  {
             jedis.select(redisTable);
             jedis.set(key,value);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


    public String readData(int redisId, int redisTable, String redisKey, boolean unique) {
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
             Jedis jedis = pool.getResource())  {
             jedis.select(redisTable);

            switch (jedis.type(redisKey)){
                case "list":{
                    if(unique) {
                        return jedis.rpop(redisKey);
                    }else {
                        return jedis.rpoplpush(redisKey, redisKey);
                    }
                }
                case "string":{
                    if(unique) {
                        String value = jedis.get(redisKey);
                        jedis.del(redisKey);
                        return value;
                    }else {
                        return jedis.get(redisKey);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
       return null;
    }

    public void deleteData(int redisId, int redisTable, String redisKey){
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
             Jedis jedis = pool.getResource())  {
            jedis.select(redisTable);
            jedis.del(redisKey);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public Set<String> getKeys(int redisId, int redisTable) {
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
             Jedis jedis = pool.getResource())  {
             jedis.select(redisTable);
             return jedis.keys("*");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }


    private Pattern pattern = Pattern.compile("db(\\d+):keys=(\\d+)");

    public List<String> getTables(int redisId) {
        List<String> result = new ArrayList<>();
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
             Jedis jedis = pool.getResource())  {
             String info = jedis.info("keyspace");
            System.out.println(info);
             Matcher matcher = pattern.matcher(info);
             while (matcher.find()){
                 String group  = matcher.group(1);
                 System.out.println(group);
                 result.add(group);
             }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public String readData(int redisId, int redisTable, String redisKey) {
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
             Jedis jedis = pool.getResource())  {
            jedis.select(redisTable);

            switch (jedis.type(redisKey)){
                case "list":{
                    return jedis.lrange(redisKey,0,1).get(0);
                }
                case "string":{
                    return jedis.get(redisKey);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteDatabase(int redisId, int redisTable) {
        Connect connect = map.get(redisId);
        try (JedisPool pool = new JedisPool(connect.getUrl());
             Jedis jedis = pool.getResource())  {
             jedis.select(redisTable);
             jedis.flushDB();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void addRedis(ConnectForm form) {
        Connect connect = redisDao.getById(redisDao.add(form));
        map.put(connect.getId(), connect);
    }

    public void updateRedis(ConnectForm form) {
        redisDao.update(form);
        Connect connect = redisDao.getById(form.getId());
        map.put(connect.getId(), connect);
    }
}

