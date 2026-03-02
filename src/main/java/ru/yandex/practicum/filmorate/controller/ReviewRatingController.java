package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.ReviewRatingService;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewRatingController {

    private final ReviewRatingService reviewRatingService;

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewRatingService.addLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Void> addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewRatingService.addDislike(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewRatingService.removeVote(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Void> removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewRatingService.removeVote(id, userId);
        return ResponseEntity.ok().build();
    }
}