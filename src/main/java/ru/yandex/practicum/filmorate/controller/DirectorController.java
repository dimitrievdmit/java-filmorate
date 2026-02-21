package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorReceiveDTO;
import ru.yandex.practicum.filmorate.dto.DirectorSendDTO;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
@Validated

public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorSendDTO createDirector(@Valid @RequestBody DirectorReceiveDTO director) {
        log.info("запрос создать режиссера с name:{}", director.getName());
        DirectorSendDTO dto = directorService.createDirector(director);
        log.info("Директор создан " + dto);
        return dto;
    }

    @PutMapping
        public DirectorSendDTO updateDirector(@Valid @RequestBody DirectorReceiveDTO director) {
        log.info("запрос обновить режиссера с id:{}, name:{}",director.getId(), director.getName());
        DirectorSendDTO dto = directorService.updateDirector(director);
        log.info("Директор обновлен " + director);
        return dto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirector(@PathVariable
                                   @Min(value = 1, message = "id директора должен быть > 0")
                                   long id) {
        log.info("запрос удалить режиссера с id:{}", id);
        directorService.deleteDirector(id);
    }

    @GetMapping("/{id}")
    public DirectorSendDTO getDirector(@PathVariable
                                    @Min(value = 1, message = "id директора должен быть > 0")
                                    long id) {
        log.info("запрос получить режиссера по id:{}", id);
        return directorService.getDirector(id);
    }

    @GetMapping
    public List<DirectorSendDTO> getAllDirectors() {
        log.info("запрос получить всех режиссеров");
        return directorService.getAllDirectors();
    }

}
