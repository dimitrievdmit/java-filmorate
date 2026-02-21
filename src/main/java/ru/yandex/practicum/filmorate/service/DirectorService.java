package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dto.DirectorReceiveDTO;
import ru.yandex.practicum.filmorate.dto.DirectorSendDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class DirectorService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;

    public DirectorSendDTO createDirector(DirectorReceiveDTO receiveDTO){
        Director director = DirectorMapper.mapToCreateDomain(receiveDTO);
        System.out.println("createDirector" + director);
        DirectorSendDTO d =  DirectorMapper.mapToSendDTO(directorStorage.createDirector(director));
        System.out.println("Отправка созданного режиссера " + d);
        return d;
    }

    public DirectorSendDTO updateDirector(DirectorReceiveDTO receiveDTO){
        if (receiveDTO.getId() == null)
            throw new IllegalArgumentException("при обновлении режиссера id = null");
        checkThatDirectorExists(receiveDTO.getId());
        Director director = DirectorMapper.mapToUpdateDomain(receiveDTO);
        directorStorage.updateDirector(director);
        DirectorSendDTO dsdto = DirectorMapper.mapToSendDTO(director);
                log.info("обновлен режиссер id:{} name:{}", dsdto.getId(), dsdto.getName());
        return dsdto;
    }

    public void deleteDirector(long id){
        directorStorage.deleteDirector(id);
    }

    public DirectorSendDTO getDirector(long id) {
        checkThatDirectorExists(id);
        Director director = directorStorage.getDirector(
                id).orElseThrow(() -> new NotFoundException("Режиссер с id:" + id + " не найден"));
        return DirectorMapper.mapToSendDTO(director);
    }

    public List<DirectorSendDTO> getAllDirectors() {
        return directorStorage.getAllDirectors().stream()
                .map(DirectorMapper::mapToSendDTO)
                .toList();
    }

    private void checkThatDirectorExists(Long id) {
        log.info("Проверить, что режиссер существует.");
        if (directorStorage.checkIfNotExists(id)) {
            String errText = "Режиссер с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }
}
