package io.github.alineaos.librarymanager.mapper;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.dto.request.BookPatchRequest;
import io.github.alineaos.librarymanager.dto.request.BookPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookGetResponse;
import io.github.alineaos.librarymanager.dto.response.BookPostResponse;
import io.github.alineaos.librarymanager.dto.response.GenreBasicResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toBook(BookPostRequest postRequest);

    @Mapping(target = "genres", source = "genresByBookId")
    BookGetResponse toBookGetResponse(Book book, List<GenreBasicResponse> genresByBookId);

    @Mapping(target = "genres", expression = "java(genresByBookId.get(book.getId()))")
    BookGetResponse toBookGetResponse(Book book, @Context Map<Long, List<GenreBasicResponse>> genresByBookId);

    List<BookGetResponse> toBookGetResponseList(List<Book> books, @Context Map<Long, List<GenreBasicResponse>> genresByBookId);

    @Mapping(target = "genres", source = "genreResponses")
    BookPostResponse toBookPostResponse(Book book, List<GenreBasicResponse> genreResponses);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void mergeRequestToBook(BookPatchRequest patchRequest, @MappingTarget Book book);

}
