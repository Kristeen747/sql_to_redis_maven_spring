package ru.riji.sql_to_influx.tasks;

import lombok.Data;
import ru.riji.sql_to_influx.model.Connect;

@Data
public class CsvTask{
    private int id;
    private Connect redis;
    private String filename;
    private int redisTable;
    private String redisKey;
    private String addedDate;

    public CsvTask(int id, int redisId, String redisName, String redisUrl, String redisUser, String redisPass, String redisKey, int redisTable,
                   String filename, String addedDate) {
        this.id=id;
        this.filename = filename;
        this.redis = new Connect(redisId,redisName,redisUrl,redisUser,redisPass);
        this.redisKey = redisKey;
        this.redisTable =redisTable;
        this.addedDate = addedDate;
    }

}
