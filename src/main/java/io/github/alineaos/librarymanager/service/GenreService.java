package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.GenreFilter;
import io.github.alineaos.librarymanager.dto.request.GenrePostRequest;
import io.github.alineaos.librarymanager.dto.request.GenrePutRequest;
import io.github.alineaos.librarymanager.dto.response.GenreGetResponse;
import io.github.alineaos.librarymanager.dto.response.GenrePostResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.exception.NotFoundException;
import io.github.alineaos.librarymanager.mapper.GenreMapper;
import io.github.alineaos.librarymanager.repository.GenreRepository;
import io.github.alineaos.librarymanager.repository.specification.GenreSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RequiredArgsConstructor
@Validated
@Service
public class GenreService {
    private final GenreRepository repository;
    private final GenreMapper mapper;

    public List<GenreGetResponse> findAll(GenreFilter filter){
        List<Genre> genres = repository.findAll(
                GenreSpecification.hasName(filter.name())
        );

        return mapper.toGetResponseList(genres);
    }

    public GenreGetResponse findById(Long id){
        Genre genre = findByIdOrThrowNotFound(id);

        return mapper.toGetResponse(genre);
    }

    public GenrePostResponse save(@Valid GenrePostRequest postRequest){
        assertNameNotExists(postRequest.name());

        Genre genreToSave = mapper.toGenre(postRequest);

        Genre savedGenre = repository.save(genreToSave);

        return mapper.toPostResponse(savedGenre);
    }

    public void update(Long id, @Valid GenrePutRequest putRequest){
        Genre genreToUpdate = findByIdOrThrowNotFound(id);
        assertNameNotExists(putRequest.name(), id);

        mapper.mergeRequestToGenre(putRequest, genreToUpdate);

        repository.save(genreToUpdate);
    }

    public void delete(Long id){
        Genre genreToDelete = findByIdOrThrowNotFound(id);

        repository.delete(genreToDelete);
    }

    private Genre findByIdOrThrowNotFound(Long id){
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Genre not found."));
    }

    private void assertNameNotExists(String name){
        repository.findByNameIgnoreCase(name).ifPresent(this::throwNameAlreadyExistsException);
    }

    private void assertNameNotExists(String name, Long id){
        repository.findByNameIgnoreCaseAndIdNot(name, id).ifPresent(this::throwNameAlreadyExistsException);
    }

    private void throwNameAlreadyExistsException(Genre genre){
        throw  new BusinessException("Genre with name '%s' already exists".formatted(genre.getName()));
    }
}
