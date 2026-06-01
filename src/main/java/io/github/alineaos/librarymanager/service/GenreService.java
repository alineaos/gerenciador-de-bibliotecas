package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.genres.GenreFilter;
import io.github.alineaos.librarymanager.dto.genres.GenreCreateRequest;
import io.github.alineaos.librarymanager.dto.genres.GenreUpdateRequest;
import io.github.alineaos.librarymanager.dto.genres.GenreInfoResponse;
import io.github.alineaos.librarymanager.dto.genres.GenreCreateResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.mapper.GenreMapper;
import io.github.alineaos.librarymanager.repository.GenreRepository;
import io.github.alineaos.librarymanager.repository.specification.GenreSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RequiredArgsConstructor
@Validated
@Service
public class GenreService {
    private final GenreRepository repository;
    private final GenreMapper mapper;
    private BookGenreService bookGenreService;

    @Autowired(required = false)
    public void setBookGenreService(@Lazy BookGenreService bookGenreService) {
        this.bookGenreService = bookGenreService;
    }

    public List<GenreInfoResponse> findAll(GenreFilter filter) {
        List<Genre> genres = repository.findAll(
                GenreSpecification.hasName(filter.name())
        );

        return mapper.toGenreInfoResponseList(genres);
    }

    public GenreInfoResponse findById(Long id) {
        Genre genre = findByIdOrThrowNotFound(id);

        return mapper.toGenreInfoResponse(genre);
    }

    public GenreCreateResponse save(@Valid GenreCreateRequest request) {
        assertNameNotExists(request.name());

        Genre genreToSave = mapper.toGenre(request);

        Genre savedGenre = repository.save(genreToSave);

        return mapper.toGenreCreateResponse(savedGenre);
    }

    public void update(Long id, @Valid GenreUpdateRequest request) {
        Genre genreToUpdate = findByIdOrThrowNotFound(id);
        assertNameNotExists(request.name(), id);

        mapper.mergeRequestToGenre(request, genreToUpdate);

        repository.save(genreToUpdate);
    }

    @Transactional
    public void delete(Long id) {
        Genre genreToDelete = findByIdOrThrowNotFound(id);

        bookGenreService.deleteBookGenreByGenre(genreToDelete);
        repository.delete(genreToDelete);
    }

    public void assertGenreByIdExists(List<Long> genreIds){
        genreIds.forEach(this::findByIdOrThrowNotFound);
    }

    public Genre getReferenceById(Long id){
        return repository.getReferenceById(id);
    }

    private Genre findByIdOrThrowNotFound(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Genre not found."));
    }

    private void assertNameNotExists(String name) {
        repository.findByNameIgnoreCase(name).ifPresent(this::throwNameAlreadyExistsException);
    }

    private void assertNameNotExists(String name, Long id) {
        repository.findByNameIgnoreCaseAndIdNot(name, id).ifPresent(this::throwNameAlreadyExistsException);
    }

    private void throwNameAlreadyExistsException(Genre genre) {
        throw new BusinessException("Genre with name '%s' already exists".formatted(genre.getName()));
    }
}
