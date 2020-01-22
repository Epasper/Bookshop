import java.util.ArrayList;
import java.util.List;

public class BookModel {

    private List<Book> books = new ArrayList<>();

    //1 Wyszukaj książkę po ISBN
    public Book findByIsbn(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;

    }

    //2 Wyszukaj książkę po autorze
    public List<Book> findByAuthor(String author) {
        List<Book> booksByAuthor = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                booksByAuthor.add(book);
            }
        }
        return booksByAuthor;
    }

    //3 Wyszukaj książkę po tytule
    //TODO

    //4 Usuń książkę
    //TODO

    //5 Edytuj książkę
    //TODO
}

