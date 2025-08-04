package ru.riji.sql_to_influx.model;

import lombok.Data;
import ru.riji.sql_to_influx.helpers.PasswordUtils;
import ru.riji.sql_to_influx.tasks.ITask;

@Data
public class Connect implements ITask {
    private int id;
    private String name;
    private String url;
    private String user;
    private String pass;

    public Connect(int id, String name, String url, String user, String pass) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.user = user;
        this.pass = PasswordUtils.decriptarString(pass);
    }
}
