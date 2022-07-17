package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.dao.MpaDBStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaDBStorage mpaDBStorage;

    @Autowired
    public MpaService(MpaDBStorage mpaDBStorage) {
        this.mpaDBStorage = mpaDBStorage;
    }

    public List<RatingMPA> findAll() {
        return mpaDBStorage.findAll();
    }

    public RatingMPA findMpaById(Long mpaId) {
        return mpaDBStorage.findMpaById(mpaId);
    }
}
