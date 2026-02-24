package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorReceiveDTO;
import ru.yandex.practicum.filmorate.dto.DirectorSendDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorSendDTO createDirector(DirectorReceiveDTO receiveDTO) {
        Director director = DirectorMapper.mapToCreateDomain(receiveDTO);
        return DirectorMapper.mapToSendDTO(directorStorage.createDirector(director));
    }

    public DirectorSendDTO updateDirector(DirectorReceiveDTO receiveDTO) {
        if (receiveDTO.getId() == null)
            throw new IllegalArgumentException("при обновлении режиссера id = null");

        checkThatDirectorExists(receiveDTO.getId());
        Director director = DirectorMapper.mapToUpdateDomain(receiveDTO);

        directorStorage.updateDirector(director);
        DirectorSendDTO sendDTO = DirectorMapper.mapToSendDTO(director);
        log.debug("обновлен режиссер id:{} name:{}", sendDTO.getId(), sendDTO.getName());
        return sendDTO;
    }

    public void deleteDirector(long id) {
        directorStorage.deleteDirector(id);
    }

    public DirectorSendDTO getDirector(long id) {
        checkThatDirectorExists(id);
        Director director = directorStorage.getDirector(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id:" + id + " не найден"));
        return DirectorMapper.mapToSendDTO(director);
    }

    public List<DirectorSendDTO> getAllDirectors() {
        return directorStorage.getAllDirectors().stream()
                .map(DirectorMapper::mapToSendDTO)
                .sorted(Comparator.comparing(DirectorSendDTO::getId))
                .toList();
    }

    private void checkThatDirectorExists(Long id) {
        if (directorStorage.checkIfNotExists(id)) {
            String errText = "Режиссер с id = " + id + " не найден";
            log.error("Ошибка: {}", errText);
            throw new NotFoundException(errText);
        }
    }
}
