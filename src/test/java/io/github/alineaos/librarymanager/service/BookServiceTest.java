package io.github.alineaos.librarymanager.service;

import io.github.alineaos.librarymanager.config.UnitTestConfig;
import io.github.alineaos.librarymanager.domain.entity.Book;
import io.github.alineaos.librarymanager.dto.BookFilter;
import io.github.alineaos.librarymanager.dto.request.BookPatchRequest;
import io.github.alineaos.librarymanager.dto.request.BookPostRequest;
import io.github.alineaos.librarymanager.dto.response.BookGetResponse;
import io.github.alineaos.librarymanager.dto.response.BookPostResponse;
import io.github.alineaos.librarymanager.exception.BusinessException;
import io.github.alineaos.librarymanager.mapper.BookMapper;
import io.github.alineaos.librarymanager.repository.BookRepository;
import io.github.alineaos.librarymanager.util.BookFactory;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest extends UnitTestConfig {
    @InjectMocks
    private BookService service;
    @Mock
    private BookRepository repository;
    @Spy
    private BookMapper mapper = Mappers.getMapper(BookMapper.class);
    private final BookFactory bookFactory = new BookFactory();
    private List<Book> bookList;

    @BeforeEach
    void init() {
        bookList = bookFactory.newBookList();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("bookFilterSource")
    @DisplayName("findAll returns a list with filtered books when the filter is valid")
    @Order(1)
    void findAll_ReturnsFilteredBooks_WhenFilterIsValid(BookFilter filter, List<Book> expectedBooks) {
        when(repository.findAll(ArgumentMatchers.<Specification<Book>>any())).thenReturn(expectedBooks);
        List<BookGetResponse> expectedDtos = expectedBooks.stream()
                .map(b -> new BookGetResponse(b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getPublisher(),
                        b.getYear(),
                        b.getEdition(),
                        b.getIsbn(),
                        b.getCreatedAt(),
                        b.getUpdatedAt()))
                .toList();

        List<BookGetResponse> result = service.findAll(filter);

        Assertions.assertThat(result).isNotNull().hasSize(expectedDtos.size());
    }

    @Test
    @DisplayName("findById return a book with given id")
    @Order(2)
    void findById_ReturnsBookById_WhenSuccessful() {
        Book expectedBook = bookList.getFirst();
        BookGetResponse expectedDto = bookFactory.newBookGetResponse();

        when(repository.findById(expectedBook.getId())).thenReturn(Optional.of(expectedBook));

        BookGetResponse result = service.findById(expectedBook.getId());

        Assertions.assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("findById throws NotFoundException when book is not found")
    @Order(3)
    void findById_ThrowsNotFoundException_WhenBookIsNotFound() {
        Book expectedBook = bookList.getFirst();

        when(repository.findById(expectedBook.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findById(expectedBook.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("save creates a book")
    @Order(4)
    void save_CreatesBook_WhenSuccessful() {
        Book bookSaved = bookFactory.newBookSaved();

        when(repository.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Book.class))).thenReturn(bookSaved);

        BookPostResponse result = service.save(bookFactory.newBookPostRequest());

        Assertions.assertThat(result.id()).isEqualTo(bookSaved.getId());
    }

    @Test
    @DisplayName("save throws BusinessException when isbn already exists")
    @Order(5)
    void save_ThrowsBusinessException_WhenIsbnAlreadyExists() {
        Book bookSaved = bookFactory.newBookSaved();
        BookPostRequest expectedDto = bookFactory.newBookPostRequest();

        when(repository.findByIsbn(expectedDto.isbn())).thenReturn(Optional.of(bookSaved));

        Assertions.assertThatException()
                .isThrownBy(() -> service.save(expectedDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("update updates a book")
    @Order(6)
    void update_UpdatesBook_WhenSuccessful() {
        Book bookToUpdate = bookList.getFirst();
        BookPatchRequest expectedDto = bookFactory.newBookPatchRequest();

        Long id = bookToUpdate.getId();
        String isbn = expectedDto.isbn();

        when(repository.findById(id)).thenReturn(Optional.of(bookToUpdate));
        when(repository.findByIsbnAndIdNot(isbn, id)).thenReturn(Optional.empty());
        when(repository.save(bookToUpdate)).thenReturn(bookToUpdate);

        service.update(id, expectedDto);

        Assertions.assertThat(bookToUpdate.getTitle()).isEqualTo(expectedDto.title());
        Assertions.assertThat(bookToUpdate.getIsbn()).isEqualTo(expectedDto.isbn());
        Assertions.assertThat(bookToUpdate.getAuthor()).isEqualTo(expectedDto.author());
    }

    @Test
    @DisplayName("update throws NotFoundException when book is not found")
    @Order(7)
    void update_ThrowsNotFoundException_WhenBookIsNotFound() {
        Book bookToUpdate = bookList.getFirst();
        BookPatchRequest expectedDto = bookFactory.newBookPatchRequest();

        Long id = bookToUpdate.getId();

        when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(id, expectedDto))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("update throws BusinessException when isbn already exists")
    @Order(8)
    void update_ThrowsBusinessException_WhenIsbnAlreadyExists() {
        Book bookToUpdate = bookList.getFirst();
        BookPatchRequest expectedDto = bookFactory.newBookPatchRequest();

        Long id = bookToUpdate.getId();
        String isbn = expectedDto.isbn();

        Book bookFromDb = bookFactory.newBookSaved();
        when(repository.findById(id)).thenReturn(Optional.of(bookToUpdate));
        when(repository.findByIsbnAndIdNot(isbn, id)).thenReturn(Optional.of(bookFromDb));

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(id, expectedDto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("delete removes a book")
    @Order(9)
    void delete_Removes_WhenSuccessful() {
        Book bookToDelete = bookList.getFirst();

        when(repository.findById(bookToDelete.getId())).thenReturn(Optional.of(bookToDelete));
        doNothing().when(repository).delete(bookToDelete);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.delete(bookToDelete.getId()));
    }

    @Test
    @DisplayName("delete throws NotFoundException when book is not found")
    @Order(8)
    void delete_ThrowsNotFoundException_WhenBookIsNotFound() {
        Book bookToDelete = bookList.getFirst();

        when(repository.findById(bookToDelete.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(bookToDelete.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    private static Stream<Arguments> bookFilterSource() {
        BookFactory factory = new BookFactory();
        List<Book> filteredList = factory.newBookList();
        String title = "estrela";
        String publisher = "Rocco";
        return Stream.of(
                Arguments.of(new BookFilter(null, null, null, null, null, null),
                        filteredList),

                Arguments.of(new BookFilter(title, null, null, null, null, null),
                        filteredList.stream()
                                .filter(b -> b.getTitle().contains(title))
                                .toList()
                ),

                Arguments.of(new BookFilter(null, null, publisher, null, null, null),
                        filteredList.stream()
                                .filter(b -> b.getPublisher().contains(publisher))
                                .toList()
                ),

                Arguments.of(new BookFilter(title, null, publisher, null, null, null),
                        filteredList.stream()
                                .filter(u -> u.getTitle().contains(title))
                                .filter(b -> b.getPublisher().contains(publisher))
                                .toList()
                ),

                Arguments.of(new BookFilter("InvalidTitle", null, null, null, null, null),
                        List.of()
                )
        );
    }
}