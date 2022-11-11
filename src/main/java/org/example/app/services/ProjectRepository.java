package org.example.app.services;

import java.util.List;

public interface ProjectRepository<T> {
    List<T> retreiveAll();

    void store(T book);

    boolean removeItemById(Integer bookIdToRemove);

    // задание 1.3.4 модуль 1
    boolean removeItemByRegex(String queryRegex);
}


