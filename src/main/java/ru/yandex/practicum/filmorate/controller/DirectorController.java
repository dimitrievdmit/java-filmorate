package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorCreateDTO;
import ru.yandex.practicum.filmorate.dto.DirectorSendDTO;
import ru.yandex.practicum.filmorate.dto.DirectorUpdateDTO;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

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
    public DirectorSendDTO createDirector(@Valid @RequestBody DirectorCreateDTO createDTO) {
        log.info("запрос создать режиссера с name:{}", createDTO.getName());
        DirectorSendDTO sendDTO = directorService.createDirector(createDTO);
        log.info("режиссер name:{} успешно создан с id:{}", sendDTO.getName(), sendDTO.getId());
        return sendDTO;
    }

    @PutMapping
    public DirectorSendDTO updateDirector(@Valid @RequestBody DirectorUpdateDTO director) {
        log.info("запрос обновить режиссера с id:{}, name:{}", director.getId(), director.getName());
        DirectorSendDTO sendDTO = directorService.updateDirector(director);
        log.info("режиссер name:{} успешно обновлен с id:{}", sendDTO.getName(), sendDTO.getId());
        return sendDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirector(@PathVariable
                               @Min(value = 1, message = "id режиссера должен быть > 0")
                               long id) {
        log.info("запрос удалить режиссера с id:{}", id);
        directorService.deleteDirector(id);
        log.info("режиссер успешно удален");
    }

    @GetMapping("/{id}")
    public DirectorSendDTO getDirector(@PathVariable
                                       @Min(value = 1, message = "id директора должен быть > 0")
                                       long id) {
        log.info("запрос получить режиссера по id:{}", id);
        DirectorSendDTO sendDTO = directorService.getDirector(id);
        log.info("режиссер с id:{} успешно получен", id);
        return sendDTO;
    }

    @GetMapping
    public List<DirectorSendDTO> getAllDirectors() {
        log.info("запрос получить всех режиссеров");
        List<DirectorSendDTO> sendDTOs = directorService.getAllDirectors();
        log.info("список режиссеров успешно получен, размер:{} ", sendDTOs.size());
        return sendDTOs;
    }
}
