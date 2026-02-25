package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorReceiveDTO;
import ru.yandex.practicum.filmorate.dto.DirectorSendDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class DBDirectorServiceTest {

    @Mock
    private DirectorStorage directorStorage;

    @InjectMocks
    private DirectorService directorService;

    @Test
    void shouldCreateDirector() {
        // Arrange
        DirectorReceiveDTO receiveDTO = new DirectorReceiveDTO(1L, "Steven Spielberg");
        Director domainDirector = new Director(1L, "Steven Spielberg");
        DirectorSendDTO expectedDTO = new DirectorSendDTO(1L, "Steven Spielberg");

        when(directorStorage.createDirector(any(Director.class))).thenReturn(domainDirector);

        // Act
        DirectorSendDTO result = directorService.createDirector(receiveDTO);

        // Assert
        assertThat(result).isEqualTo(expectedDTO);
        verify(directorStorage, times(1)).createDirector(any(Director.class));
    }

    @Test
    void shouldGetDirectorByIdWhenDirectorExists() {
        // Arrange
        long directorId = 1L;
        Director domainDirector = new Director(directorId, "Quentin Tarantino");
        DirectorSendDTO expectedDTO = new DirectorSendDTO(directorId, "Quentin Tarantino");

        when(directorStorage.checkIfNotExists(directorId)).thenReturn(false);
        when(directorStorage.getDirector(directorId)).thenReturn(Optional.of(domainDirector));

        // Act
        DirectorSendDTO result = directorService.getDirector(directorId);

        // Assert
        assertThat(result).isEqualTo(expectedDTO);
        verify(directorStorage, times(1)).checkIfNotExists(directorId);
        verify(directorStorage, times(1)).getDirector(directorId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDirectorNotFound() {
        // Arrange
        long nonExistentId = 999L;

        when(directorStorage.getDirector(nonExistentId)).thenReturn(Optional.empty());
        when(directorStorage.checkIfNotExists(nonExistentId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> directorService.getDirector(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Режиссер с id = " + nonExistentId + " не найден");
    }

    @Test
    void shouldUpdateDirector() {
        // Arrange
        DirectorReceiveDTO receiveDTO = new DirectorReceiveDTO(1L, "Martin Scorsese");
        Director existingDirector = new Director(1L, "Old Name");
        Director updatedDirector = new Director(1L, "Martin Scorsese");
        DirectorSendDTO expectedDTO = new DirectorSendDTO(1L, "Martin Scorsese");

        when(directorStorage.checkIfNotExists(1L)).thenReturn(false);
        when(directorStorage.getDirector(1L)).thenReturn(Optional.of(existingDirector));

        // Act
        DirectorSendDTO result = directorService.updateDirector(receiveDTO);

        // Assert
        assertThat(result).isEqualTo(expectedDTO);
        verify(directorStorage, times(1)).updateDirector(any(Director.class));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUpdatingWithNullId() {
        // Arrange
        DirectorReceiveDTO receiveDTO = new DirectorReceiveDTO(null, "Name");

        // Act & Assert
        assertThatThrownBy(() -> directorService.updateDirector(receiveDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("при обновлении режиссера id = null");
    }

    @Test
    void shouldDeleteDirector() {
        // Arrange
        long directorId = 1L;

        doNothing().when(directorStorage).deleteDirector(directorId);

        // Act
        directorService.deleteDirector(directorId);

        // Assert
        verify(directorStorage, times(1)).deleteDirector(directorId);
    }

    @Test
    void shouldGetAllDirectors() {
        // Arrange
        List<Director> directors = List.of(
                new Director(1L, "Christopher Nolan"),
                new Director(2L, "James Cameron")
        );
        List<DirectorSendDTO> expectedDTOs = directors.stream()
                .map(d -> new DirectorSendDTO(d.getId(), d.getName()))
                .toList();

        when(directorStorage.getAllDirectors()).thenReturn(directors);

        // Act
        List<DirectorSendDTO> result = directorService.getAllDirectors();

        // Assert
        assertThat(result).isEqualTo(expectedDTOs);
        verify(directorStorage, times(1)).getAllDirectors();
    }

    @Test
    void shouldCheckDirectorExistsWhenDirectorExists() {
        // Arrange
        long directorId = 1L;

        when(directorStorage.checkIfNotExists(directorId)).thenReturn(false);

        // Act & Assert — проверяем, что исключение не выбрасывается
        assertThatNoException()
                .isThrownBy(() -> directorService.checkThatDirectorExists(directorId));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDirectorDoesNotExist() {
        // Arrange
        long nonExistentId = 999L;
        String expectedErrorMessage = "Режиссер с id = " + nonExistentId + " не найден";

        when(directorStorage.checkIfNotExists(nonExistentId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> directorService.checkThatDirectorExists(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(expectedErrorMessage);
    }
}
