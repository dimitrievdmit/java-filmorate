package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmGenreSendDTO;
import ru.yandex.practicum.filmorate.mapper.FilmGenreMapper;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
@Validated
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<FilmGenreSendDTO> getAllGenres() {
        return genreService.getAllGenres()
                .stream()
                .map(FilmGenreMapper::mapToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public FilmGenreSendDTO getGenre(@PathVariable Integer id) {
        return FilmGenreMapper.mapToDTO(genreService.getGenre(id));
    }

}
