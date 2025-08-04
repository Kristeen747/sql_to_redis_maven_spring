package ru.riji.sql_to_influx.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ru.riji.sql_to_influx.tasks.CsvTask;

@Data
@NoArgsConstructor
public class ExcelForm {
    private String redisKey;
    private int redisId;
    private int redisTable;
    private MultipartFile file;

}
