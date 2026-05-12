package io.github.alineaos.librarymanager.mapper;

import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.dto.request.BookPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toBook(BookPostRequest postRequest);

    BookPostResponse toBookPostResponse(Book book);
}
