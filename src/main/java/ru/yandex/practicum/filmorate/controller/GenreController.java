package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmGenreSendDTO;
import ru.yandex.practicum.filmorate.mapper.FilmGenreMapper;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.Comparator;

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
                .sorted(Comparator.comparing(FilmGenreSendDTO::id))
                .toList();
    }

    @GetMapping("/{id}")
    public FilmGenreSendDTO getGenre(@PathVariable Integer id) {
        return FilmGenreMapper.mapToDTO(genreService.getGenre(id));
    }

}
