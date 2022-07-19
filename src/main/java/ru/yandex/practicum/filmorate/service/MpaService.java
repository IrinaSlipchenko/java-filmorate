package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDBStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDBStorage mpaDBStorage;

    public List<Mpa> findAll() {
        return mpaDBStorage.findAll();
    }

    public Mpa findMpaById(Long mpaId) {
        return mpaDBStorage.findMpaById(mpaId);
    }
}
