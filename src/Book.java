import java.util.ArrayList;

public class Book {

    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int year;
    private String publisher;
    private int cost;
    private ArrayList listOfRecommendations = new ArrayList();


    public Book(String isbn, String title, String author, String genre, int year, String publisher, int cost) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.publisher = publisher;
        this.cost = cost;

    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", year=" + year +
                ", publisher='" + publisher + '\'' +
                ", cost=" + cost +
                ", listOfRecommendations=" + listOfRecommendations +
                '}';
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public ArrayList getListOfRecommendations() {
        return listOfRecommendations;
    }

    public void setListOfRecommendations(ArrayList listOfRecommendations) {
        this.listOfRecommendations = listOfRecommendations;
    }
}