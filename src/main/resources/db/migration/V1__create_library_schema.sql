CREATE TABLE authors(
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   name VARCHAR(100) NOT NULL,
   bio TEXT,
   created_at DATETIME  NOT NULL,
   updated_at DATETIME NOT NULL
  );


CREATE TABLE books(
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
      title VARCHAR(100) NOT NULL,
       isbn VARCHAR(100) NOT NULL UNIQUE,
        description VARCHAR(255),
         publication_year  INTEGER NOT NULL,
         author_id BIGINT NOT NULL,
          created_at DATETIME  NOT NULL,
           updated_at DATETIME NOT NULL,

                      CONSTRAINT
                          fk_books_authors
                          FOREIGN KEY (author_id)
                              REFERENCES authors(id)
);

CREATE TABLE book_copies(
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      book_id BIGINT NOT NULL,
      copy_status VARCHAR(20) NOT NULL ,
      created_at DATETIME  NOT NULL,
      updated_at DATETIME NOT NULL ,

              CONSTRAINT
                   fk_book_copies_books
                            FOREIGN KEY  (book_id)
                       REFERENCES books(id)
);

CREATE TABLE members(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(100) NOT NULL,
    membership_status VARCHAR(20) NOT NULL,
    created_at DATETIME  NOT NULL ,
    updated_at DATETIME NOT NULL
 );


CREATE TABLE loans(
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        member_id BIGINT NOT NULL,
        book_copy_id BIGINT NOT NULL,
        borrowed_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
        due_date DATETIME NOT NULL,
        returned_at DATETIME,
        loan_status VARCHAR(20) NOT NULL,
        created_at DATETIME  NOT NULL ,
        updated_at DATETIME NOT NULL ,


                      CONSTRAINT
                          fk_loans_members
                          FOREIGN KEY (member_id)
                              REFERENCES members(id),

                      CONSTRAINT
                          fk_loans_book_copies
                          FOREIGN KEY (book_copy_id)
                              REFERENCES book_copies(id)
);