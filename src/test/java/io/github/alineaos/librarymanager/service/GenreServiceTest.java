package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.config.UnitTestConfig;
import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.GenreFilter;
import io.github.alineaos.librarymanager.dto.request.GenrePostRequest;
import io.github.alineaos.librarymanager.dto.request.GenrePutRequest;
import io.github.alineaos.librarymanager.dto.response.GenreGetResponse;
import io.github.alineaos.librarymanager.dto.response.GenrePostResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.mapper.GenreMapper;
import io.github.alineaos.librarymanager.repository.GenreRepository;
import io.github.alineaos.librarymanager.util.GenreFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest extends UnitTestConfig {
    @InjectMocks
    private GenreService service;
    @Mock
    private GenreRepository repository;
    @Spy
    private GenreMapper mapper = Mappers.getMapper(GenreMapper.class);
    private final GenreFactory genreFactory = new GenreFactory();
    private List<Genre> genreList;

    @BeforeEach
    void init() {
        genreList = genreFactory.newGenreList();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("genreFilterSource")
    @DisplayName("findAll returns a list with filtered genres when the filter is valid")
    @Order(1)
    void findAll_ReturnsFilteredGenres_WhenFilterIsValid(GenreFilter filter, List<Genre> expectedGenres) {
        when(repository.findAll(ArgumentMatchers.<Specification<Genre>>any())).thenReturn(expectedGenres);

        List<GenreGetResponse> expectedDtos = expectedGenres.stream()
                .map(g -> new GenreGetResponse(
                        g.getId(),
                        g.getName(),
                        g.getCreatedAt(),
                        g.getUpdatedAt()
                ))
                .toList();

        List<GenreGetResponse> result = service.findAll(filter);

        Assertions.assertThat(result).isNotNull().hasSize(expectedDtos.size());
    }

    @Test
    @DisplayName("findById returns a genre with the given id")
    @Order(2)
    void findById_ReturnsGenreById_WhenSuccessful() {
        Genre expectedGenre = genreList.getFirst();
        GenreGetResponse expectedDto = genreFactory.newGenreGetResponse();
        Long id = expectedGenre.getId();
        when(repository.findById(id)).thenReturn(Optional.of(expectedGenre));

        GenreGetResponse result = service.findById(id);

        Assertions.assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("findById throws NotFoundException when genre is not found")
    @Order(3)
    void findById_ThrowsNotFoundException_WhenGenreIsNotFound() {
        Genre expectedGenre = genreList.getFirst();
        Long id = expectedGenre.getId();

        when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findById(id))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("save creates a genre")
    @Order(4)
    void save_CreatesGenre_WhenSuccessful() {
        Genre genreSaved = genreFactory.newGenreSaved();
        GenrePostRequest expectedDto = genreFactory.newGenrePostRequest();

        when(repository.findByNameIgnoreCase(genreSaved.getName())).thenReturn(Optional.empty());
        when(repository.save(any(Genre.class))).thenReturn(genreSaved);

        GenrePostResponse result = service.save(expectedDto);

        Assertions.assertThat(result.id()).isEqualTo(genreSaved.getId());
    }

    @Test
    @DisplayName("save throws BusinessException when name already exists")
    @Order(5)
    void save_ThrowsBusinessException_WhenNameAlreadyExists() {
        Genre genreSaved = genreFactory.newGenreSaved();
        GenrePostRequest expectedDto = genreFactory.newGenrePostRequest();

        when(repository.findByNameIgnoreCase(genreSaved.getName())).thenReturn(Optional.of(genreSaved));

        Assertions.assertThatException()
                .isThrownBy(() -> service.save(expectedDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("update updates a genre")
    @Order(6)
    void update_UpdatesGenre_WhenSuccessful() {
        Genre genreToUpdate = genreList.getFirst();
        GenrePutRequest expectedDto = genreFactory.newGenrePutRequest();
        Long id = genreToUpdate.getId();

        when(repository.findById(id)).thenReturn(Optional.of(genreToUpdate));
        when(repository.findByNameIgnoreCaseAndIdNot(expectedDto.name(), id)).thenReturn(Optional.empty());
        when(repository.save(genreToUpdate)).thenReturn(genreToUpdate);

        service.update(id, expectedDto);

        Assertions.assertThat(genreToUpdate.getName()).isEqualTo(expectedDto.name());
    }

    @Test
    @DisplayName("update throws NotFoundException when genre is not found")
    @Order(7)
    void update_ThrowsNotFoundException_WhenGenreIsNotFound() {
        Genre genreToUpdate = genreList.getFirst();
        GenrePutRequest expectedDto = genreFactory.newGenrePutRequest();
        Long id = genreToUpdate.getId();

        when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(id, expectedDto))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("update throws BusinessException when name already exists")
    @Order(8)
    void update_ThrowsBusinessException_WhenNameAlreadyExists() {
        Genre genreToUpdate = genreList.getFirst();
        GenrePutRequest expectedDto = genreFactory.newGenrePutRequest();
        Long id = genreToUpdate.getId();

        Genre genreFromDb = genreFactory.newGenreSaved();
        when(repository.findById(id)).thenReturn(Optional.of(genreToUpdate));
        when(repository.findByNameIgnoreCaseAndIdNot(expectedDto.name(), id)).thenReturn(Optional.of(genreFromDb));

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(id, expectedDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("delete removes a genre")
    @Order(9)
    void delete_RemovesGenre_WhenSuccessful() {
        Genre genreToDelete = genreList.getFirst();
        Long id = genreToDelete.getId();

        when(repository.findById(id)).thenReturn(Optional.of(genreToDelete));
        doNothing().when(repository).delete(genreToDelete);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.delete(id));
    }

    @Test
    @DisplayName("delete throws NotFoundException when genre is not found")
    @Order(10)
    void delete_ThrowsNotFoundException_WhenGenreIsNotFound() {
        Genre genreToDelete = genreList.getFirst();
        Long id = genreToDelete.getId();

        when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(id))
                .isInstanceOf(ResponseStatusException.class);
    }

    private static Stream<Arguments> genreFilterSource() {
        GenreFactory factory = new GenreFactory();
        List<Genre> filteredList = factory.newGenreList();
        String name = "Romance";

        return Stream.of(
                Arguments.of(new GenreFilter(null),
                        filteredList
                ),

                Arguments.of(new GenreFilter(name),
                        filteredList.stream()
                                .filter(g -> g.getName().equalsIgnoreCase(name))
                                .toList()
                ),

                Arguments.of(new GenreFilter("InvalidName"),
                        List.of()
                )
        );
    }
}