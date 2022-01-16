package com.covenant.springbootmysql.controller;

import com.covenant.springbootmysql.model.Book;
import com.covenant.springbootmysql.model.request.BookCreationRequest;
import com.covenant.springbootmysql.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    /**
     * *주의! 실제 서비스를 개발할때는 Repository에서 반환하는 ResponseEntity를 응답값으로 반환하면 안됩니다.
     * RepositoryEntity 스팩이 변경되면 API의 응답값이 변경되기 때문입니다.
     * 조회한 객체를 응답값으로 매핑하는 로직이 필요하지만 해당 예제에서는 생략하겠습니다.
     */
    // 위의 문구가 이해 안됨
    @GetMapping("/book")
    public ResponseEntity readBooks(@RequestParam(required = false) String isbn) {
        if (isbn == null) {
            return ResponseEntity.ok(libraryService.readBooks());
        }
        return ResponseEntity.ok(libraryService.readBook(isbn));
    }

    // bookId로 조회
    @GetMapping("/book/{bookId}")
    public ResponseEntity<Book> readBook (@PathVariable Long bookId) {
        return ResponseEntity.ok(libraryService.readBook(bookId));
    }

    // book 생성
    @PostMapping("/book")
    public ResponseEntity<Book> createBook (@RequestBody BookCreationRequest request) {
        return ResponseEntity.ok(libraryService.createBook(request));
    }

    // book 삭제
    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<Void> deleteBook (@PathVariable Long bookId) {
        libraryService.deleteBook(bookId);
        return ResponseEntity.ok().build();
    }
}
