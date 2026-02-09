package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmRatingSendDTO;
import ru.yandex.practicum.filmorate.mapper.FilmRatingMapper;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;
import java.util.Comparator;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
@Validated
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public Collection<FilmRatingSendDTO> getAllRatings() {
        return ratingService.getAllRatings()
                .stream()
                .map(FilmRatingMapper::mapToDTO)
                .sorted(Comparator.comparing(FilmRatingSendDTO::getId))
                .toList();
    }

    @GetMapping("/{id}")
    public FilmRatingSendDTO getRating(@PathVariable Integer id) {
        return FilmRatingMapper.mapToDTO((ratingService.getRating(id)));
    }

}
