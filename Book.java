import javafx.beans.property.SimpleStringProperty;

public class Book {
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;

    public Book(String title, String author) {
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public SimpleStringProperty authorProperty() {
        return author;
    }
}
