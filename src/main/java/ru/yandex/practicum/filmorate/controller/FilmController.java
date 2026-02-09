package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
        return FilmMapper.mapToSendDTO(filmService.createFilm(FilmMapper.mapToDomain(film)));
    }

    @GetMapping("/{id}")
    public FilmSendDTO getFilm(@PathVariable Long id) {
        return FilmMapper.mapToSendDTO((filmService.getFilm(id)));
    }

    @PutMapping
    public FilmSendDTO updateFilm(@Valid @RequestBody FilmReceiveDTO newFilm) {
        return FilmMapper.mapToSendDTO(filmService.updateFilm(FilmMapper.mapToDomain(newFilm)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
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
