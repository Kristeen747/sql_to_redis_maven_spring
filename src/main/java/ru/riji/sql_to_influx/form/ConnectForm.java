package ru.riji.sql_to_influx.form;

import lombok.Data;
import ru.riji.sql_to_influx.model.Connect;
import ru.riji.sql_to_influx.model.IForm;

@Data
public class ConnectForm implements IForm {
    private int id;
    private String name;
    private String url;
    private String user;
    private String pass;

    public ConnectForm() {
    }

    public ConnectForm(int id, String name, String url, String user, String pass) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.user = user;
        this.pass = pass;
    }
    public ConnectForm(Connect connect) {
        this.id = connect.getId();
        this.name = connect.getName();
        this.url = connect.getUrl();
        this.user = connect.getUser();
        this.pass = connect.getPass();
    }
}
