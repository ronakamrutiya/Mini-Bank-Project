package com.minibank.util;

import com.minibank.model.annotation.Id;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class Repository<T> {
    private final HashMap<String,T> entities = new HashMap<>();
    public synchronized T save(T entity) { entities.put(idOf(entity), entity); return entity; }
    @SafeVarargs public final synchronized void saveAll(T... items) { for (T item : items) save(item); }
    public synchronized T findById(String id) { return entities.get(id); }
    public synchronized ArrayList<T> findAll() { return new ArrayList<>(entities.values()); }
    public synchronized boolean delete(String id) { return entities.remove(id) != null; }
    private String idOf(T entity) {
        for (Class<?> type = entity.getClass(); type != null; type = type.getSuperclass()) for (Field field : type.getDeclaredFields()) if (field.isAnnotationPresent(Id.class)) {
            try { field.setAccessible(true); Object id = field.get(entity); if (id == null) throw new IllegalArgumentException("@Id cannot be null"); return id.toString(); }
            catch (ReflectiveOperationException e) { throw new IllegalArgumentException("Cannot read @Id field", e); }
        }
        throw new IllegalArgumentException("Entity has no @Id field: " + entity.getClass().getName());
    }
    public static <N extends Number> double average(N[] values) { if (values.length == 0) return 0; double sum = 0; for (N n : values) sum += n.doubleValue(); return sum / values.length; }
}
