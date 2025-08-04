package ru.riji.sql_to_influx.dao;

import ru.riji.sql_to_influx.model.IForm;
import ru.riji.sql_to_influx.tasks.ITask;

import java.util.List;

 public interface IDAO<T, F extends IForm> {
   List<T> getAll();
   T getById(int id);
   List<T> getByGroupName(String name);
   int add(F f);
   void update(F f);
   void update(T t);
   void delete(int id);

 }
