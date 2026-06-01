package io.github.alineaos.librarymanager.mapper;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.dto.books.BookBasicResponse;
import io.github.alineaos.librarymanager.dto.books.BookCreateRequest;
import io.github.alineaos.librarymanager.dto.books.BookCreateResponse;
import io.github.alineaos.librarymanager.dto.books.BookInfoResponse;
import io.github.alineaos.librarymanager.dto.books.BookUpdateRequest;
import io.github.alineaos.librarymanager.dto.genres.GenreBasicResponse;
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
    Book toBook(BookCreateRequest request);

    BookCreateResponse toBookCreateResponse(Book book, List<GenreBasicResponse> genres);

    BookInfoResponse toBookInfoResponse(Book book, List<GenreBasicResponse> genres);

    @Mapping(target = "genres", expression = "java(genresByBookId.get(book.getId()))")
    BookInfoResponse toBookInfoResponse(Book book, @Context Map<Long, List<GenreBasicResponse>> genresByBookId);

    List<BookInfoResponse> toBookInfoResponseList(List<Book> books, @Context Map<Long, List<GenreBasicResponse>> genresByBookId);

    BookBasicResponse toBookBasicResponse(Book book);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void mergeRequestToBook(BookUpdateRequest request, @MappingTarget Book book);

}
