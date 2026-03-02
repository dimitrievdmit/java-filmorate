package ru.yandex.practicum.filmorate.dto.search;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@SuppressWarnings("unused")
public record SearchRequest(
        @NotBlank(message = "Параметр query должен быть заполнен")
        String query,

        @NotBlank(message = "Параметр by должен быть заполнен")
        @Pattern(regexp = "(director|title)(,(director|title))?",
                message = "Атрибут by задан неверно. " +
                        "Допустимые значения: 'director', 'title', 'director,title' или 'title,director'")
        String by
) {}
