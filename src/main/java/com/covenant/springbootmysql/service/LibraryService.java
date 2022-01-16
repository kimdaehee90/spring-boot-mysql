package com.covenant.springbootmysql.service;

import com.covenant.springbootmysql.model.Author;
import com.covenant.springbootmysql.model.Book;
import com.covenant.springbootmysql.model.request.BookCreationRequest;
import com.covenant.springbootmysql.repository.AuthorRepository;
import com.covenant.springbootmysql.repository.BookRepository;
import com.covenant.springbootmysql.repository.LendRepository;
import com.covenant.springbootmysql.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final AuthorRepository authorRepository;
    private final MemberRepository memberRepository;
    private final LendRepository lendRepository;
    private final BookRepository bookRepository;

    // id에 해당하는 book 조회
    public Book readBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return book.get();
        }
        throw new EntityNotFoundException("Cant find any book under given ID");
    }

    // 모든 book 조회
    public List<Book> readBooks(){
        return bookRepository.findAll();
    }

    // isbn으로 book 조회하기
    public Book readBook(String isbn){
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if(book.isPresent()){
            return book.get();
        }
        throw new EntityNotFoundException("Cant find any book under given ISBN");
    }

    // book을 새로 생성하기
    public Book createBook(BookCreationRequest book) {
        Optional<Author> author = authorRepository.findById(book.getAuthorId());
        if (!author.isPresent()) {
            throw new EntityNotFoundException(
                    "Author Not Found");
        }

        //BeanUtils.copyProperties은 처음 보네요
        Book bookToCreate = new Book();
        BeanUtils.copyProperties(book, bookToCreate);
        bookToCreate.setAuthor(author.get());
        return bookRepository.save(bookToCreate);
    }
}
