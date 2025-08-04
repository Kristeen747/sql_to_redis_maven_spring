package ru.riji.sql_to_influx.runner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;
import ru.riji.sql_to_influx.model.SqlData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Component
public class CsvRunner {

    public void writeData(SqlData data, String filePath) throws IOException {
        try (
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader(data.getColumnNames()));
        ) {
            for (List<String> row : data.getRows()) {
                csvPrinter.printRecord(row);
            }
            csvPrinter.flush();
        }
    }
} 