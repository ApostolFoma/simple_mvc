package org.example.app.services;

import org.example.web.dto.Book;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

@Repository
public class BookRepository implements ProjectRepository<Book>, ApplicationContextAware  {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    //private final List<Book> repo = new ArrayList<>();
    private ApplicationContext context;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Book> retreiveAll() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books", (ResultSet rs, int rowNum)->{
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setSize(rs.getInt("size"));
            return book;
        });
        return new ArrayList<>(books);
    }

    @Override
    public void store(Book book) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author", book.getAuthor());
        parameterSource.addValue("title", book.getTitle());
        parameterSource.addValue("size", book.getSize());
        jdbcTemplate.update("INSERT INTO books(author,title,size) VALUES(:author, :title, :size)", parameterSource);
        logger.info("store new book: " + book);
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", bookIdToRemove);
        jdbcTemplate.update("DELETE FROM books WHERE id = :id", parameterSource);
        logger.info("remove book completed");
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private void defaultInit() {
        logger.info("default INIT in book repo bean ");
    }

    private void defaultDestroy() {
        logger.info("default DESTROY in book repo bean ");
    }

    // задание 1.3 модуль 1
    @Override
    public boolean removeItemByRegex(String queryRegex) {
        boolean isDeleted = false;
        if (isNumber(queryRegex)) {
            Integer size = Integer.parseInt(queryRegex);
            for (Book book : retreiveAll()) {
                if (book.getSize() == size) {
                    logger.info("remove book by regex (size) completed: " + book);
                    //repo.remove(book);
                    isDeleted = true;
                }
            }
            return isDeleted;
        }
        for (Book book : retreiveAll()) {
            if (book.getAuthor().equals(queryRegex)
                    || book.getTitle().equals(queryRegex)) {
                logger.info("remove book by regex completed: " + book);
                //repo.remove(book);
                isDeleted = true;
            }
        }
        return isDeleted;
    }

    public boolean isNumber(String queryRegex) {
        return queryRegex.matches("[0-9]+");
    }

}

