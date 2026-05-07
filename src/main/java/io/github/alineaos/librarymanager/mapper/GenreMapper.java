package io.github.alineaos.librarymanager.mapper;

import io.github.alineaos.librarymanager.domain.entity.Genre;
import io.github.alineaos.librarymanager.dto.request.GenrePostRequest;
import io.github.alineaos.librarymanager.dto.request.GenrePutRequest;
import io.github.alineaos.librarymanager.dto.response.GenreGetResponse;
import io.github.alineaos.librarymanager.dto.response.GenrePostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Genre toGenre (GenrePostRequest postRequest);

    List<GenreGetResponse> toGetResponseList(List<Genre> genreList);
    GenreGetResponse toGetResponse(Genre genre);

    GenrePostResponse toPostResponse(Genre genre);

    void mergeRequestToGenre(GenrePutRequest putRequest, @MappingTarget Genre genre);
}
