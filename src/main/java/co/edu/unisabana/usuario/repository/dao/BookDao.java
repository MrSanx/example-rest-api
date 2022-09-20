package co.edu.unisabana.usuario.repository.dao;

import co.edu.unisabana.usuario.repository.dao.entity.BookEntity;
import co.edu.unisabana.usuario.exception.PreliminaryRegisterException;
import co.edu.unisabana.usuario.service.library.model.Book;
import co.edu.unisabana.usuario.service.library.port.AddBookPort;
import co.edu.unisabana.usuario.service.library.port.RegisterBookPort;
import co.edu.unisabana.usuario.service.library.port.SearchBookPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Repository
public class BookDao implements SearchBookPort, RegisterBookPort, AddBookPort {

    static List<BookEntity> listBooks = new ArrayList<>();

    @Override
    public void registerBook(Book newBook) {
        BookEntity bookEntity = BookEntity.fromModel(newBook);
        if (validationRegisterBook(bookEntity)) {
            bookEntity.setId(listBooks.size() + 1);
            listBooks.add(bookEntity);
        }
    }

    public boolean validationRegisterBook(BookEntity book) {
        boolean validationAuthor = !book.getAuthor().equals("unknown");

        if (validationAuthor) {
            return true;
        } else {
            throw new IllegalArgumentException("El libro ya ha sido registrado.");
        }
    }

    @Override
    public boolean addBook(String name) {
        for (BookEntity book : listBooks) {
            if (book.getName().equals(name) && validationAddBook(book)) {
                book.setQuantity(book.getQuantity() + 1);
                return true;
            }
        }
        // Para evitar confusiones futuras, se debe crear excepciones personalizadas
        // conforme existan reglas de negocio
        // que las ameriten. POR FAVOR AGREGUEN CONTEXTO
        throw new PreliminaryRegisterException(name);
    }

    private boolean validationAddBook(BookEntity book) {
        int max = 7;
        if (book.getQuantity() <= max) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean validateExistsBook(String nameBook) {
        AtomicBoolean exists = new AtomicBoolean(false);
        listBooks.forEach(book -> {
            if (book.getName().equals(nameBook)) {
                exists.set(true);
            }
        });
        return exists.get();
    }

    @Override
    public List<BookEntity> getAuthorBooks(String name) {
        List<BookEntity> authorBooks = new ArrayList<>();
        listBooks.forEach(book -> { if (book.getAuthor().equals(name)) { authorBooks.add(book); } });
        return authorBooks;
    }

}
