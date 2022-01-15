package com.covenant.springbootmysql.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    // cascade의 경우 ALL만 기억이 나서 다른 전략은 잘 기억이 안나네요
    // fetch의 경우 다른 점은 알겠는데 언제 어떤걸 써야하는지 잘 모르겠네요
    @JsonBackReference
    @OneToMany(mappedBy = "author",
            fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Book> books;
}
