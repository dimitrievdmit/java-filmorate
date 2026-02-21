package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmSendDTO;
import ru.yandex.practicum.filmorate.dto.FilmReceiveDTO;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmSendDTO> getAllFilms() {
        return filmService.getAllFilms()
                .stream()
                .map(FilmMapper::mapToSendDTO)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmSendDTO createFilm(@Valid @RequestBody FilmReceiveDTO film) {
        log.info("запрос создать фильм");
        return FilmMapper.mapToSendDTO(filmService.createFilm(FilmMapper.mapToDomain(film)));
    }

    @PutMapping
    public FilmSendDTO updateFilm(@Valid @RequestBody FilmReceiveDTO newFilm) {
        log.info("запрос обновить фильм id:{} ", newFilm.getId());
        System.out.println("Тело " + newFilm);
        return FilmMapper.mapToSendDTO(filmService.updateFilm(FilmMapper.mapToDomain(newFilm)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Long id) {
        log.info("запрос удалить фильм id:{}", id);
        filmService.deleteFilm(id);
    }

    @GetMapping("/{id}")
    public FilmSendDTO getFilm(@PathVariable Long id) {
        log.info("запрос получить фильм ид:{}", id);
        return FilmMapper.mapToSendDTO((filmService.getFilm(id)));
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmSendDTO filmAddLike(@PathVariable Long id, @PathVariable Long userId) {
        return FilmMapper.mapToSendDTO(filmService.filmAddLike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public FilmSendDTO filmDeleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return FilmMapper.mapToSendDTO(filmService.filmDeleteLike(id, userId));
    }

    @PutMapping("/{id}/genre/{genreId}")
    public FilmSendDTO filmAddGenre(@PathVariable Long id, @PathVariable Integer genreId) {
        return FilmMapper.mapToSendDTO(filmService.filmAddGenre(id, genreId));
    }

    @DeleteMapping("/{id}/genre/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public FilmSendDTO filmDeleteGenre(@PathVariable Long id, @PathVariable Integer genreId) {
        return FilmMapper.mapToSendDTO(filmService.filmDeleteGenre(id, genreId));
    }

    @GetMapping("/director/{directorId}")
    public List<FilmSendDTO> getSortedDirectorFilms(@PathVariable
                                                        @Min(value = 1L, message = "Параметр должен быть > 0")
                                                        int directorId,
                                             @RequestParam()
                                             @Pattern(regexp = "year|likes", message = "атрибут sortBy задан не верно")
                                             String sortBy) {
        return filmService.getSortedDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/popular")
    public Collection<FilmSendDTO> getPopularFilms(
            @RequestParam(defaultValue = "10L")
            @Positive(message = "Параметр должен быть положительным числом")
            Long count
    ) {
        return filmService.getPopularFilms(count)
                .stream()
                .map(FilmMapper::mapToSendDTO)
                .toList();
    }
}
