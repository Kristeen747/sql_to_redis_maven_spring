package ru.riji.sql_to_influx.form;

import lombok.Data;
import ru.riji.sql_to_influx.model.IForm;
import ru.riji.sql_to_influx.model.Interval;

@Data
public class IntervalForm implements IForm {
    private int id;
    private String name;
    private long value;

    public IntervalForm() {
    }

    public IntervalForm(Interval interval) {
        this.id = interval.getId();
        this.name = interval.getName();
        this.value = interval.getValue();
    }
}
