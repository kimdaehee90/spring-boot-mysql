package com.covenant.springbootmysql.service;

import com.covenant.springbootmysql.model.*;
import com.covenant.springbootmysql.model.request.AuthorCreationRequest;
import com.covenant.springbootmysql.model.request.BookCreationRequest;
import com.covenant.springbootmysql.model.request.BookLendRequest;
import com.covenant.springbootmysql.model.request.MemberCreationRequest;
import com.covenant.springbootmysql.repository.AuthorRepository;
import com.covenant.springbootmysql.repository.BookRepository;
import com.covenant.springbootmysql.repository.LendRepository;
import com.covenant.springbootmysql.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    // book을 삭제하기
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // member 생성하기
    public Member createMember(MemberCreationRequest request) {
        Member member = new Member();
        BeanUtils.copyProperties(request, member);
        return memberRepository.save(member);
    }

    // member 정보 update 하기
    public Member updateMember (Long id, MemberCreationRequest request) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new EntityNotFoundException(
                    "Member not present in the database");
        }

        Member member = optionalMember.get();
        member.setLastName(request.getLastName());
        member.setFirstName(request.getFirstName());
        return memberRepository.save(member);
    }

    // author 생성하기
    public Author createAuthor (AuthorCreationRequest request) {
        Author author = new Author();
        BeanUtils.copyProperties(request, author);
        return authorRepository.save(author);
    }

    // BookLendRequest로 도서를 대출
    public List<String> lendABook (List<BookLendRequest> list) {
        List<String> booksApprovedToBurrow = new ArrayList<>();
        list.forEach(bookLendRequest -> {
            // bookLendRequest에 bookIds로 되어 있는데 어떻게 바로 getBookId가 되는지 자동 완성도
            // 되지 않는데 이해가 잘 안되네요
            Optional<Book> bookForId =
                    bookRepository.findById(bookLendRequest.getBookId);
            if (!bookForId.isPresent()) {
                throw new EntityNotFoundException(
                        "Cant find any book under given ID");
            }

            Optional<Member> memberForId =
                    memberRepository.findById(bookLendRequest.getMemberId());
            if (!memberForId.isPresent()) {
                throw new EntityNotFoundException(
                        "Member not present in the database");
            }

            Member member = memberForId.get();
            if (member.getStatus() != MemberStatus.ACTIVE) {
                throw new RuntimeException(
                        "User is not active to proceed a lending.");
            }

            Optional<Lend> burrowedBook =
                    lendRepository.findByBookAndStatus(
                            bookForId.get(), LendStatus.BURROWED);

            if (!burrowedBook.isPresent()) {
                booksApprovedToBurrow.add(bookForId.get().getName());
                Lend lend = new Lend();
                lend.setMember(memberForId.get());
                lend.setBook(bookForId.get());
                lend.setStatus(LendStatus.BURROWED);
                lend.setStartOn(Instant.now());
                lend.setDueOn(Instant.now().plus(30, ChronoUnit.DAYS));
                lendRepository.save(lend);
            }
        });

        return booksApprovedToBurrow;
    }
}
