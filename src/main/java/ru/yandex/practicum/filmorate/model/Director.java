package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Director {
    @Min(value = 1L, message = "id режиссера должен быть > 0")
    private Long id;
    @NotNull
    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;
}
