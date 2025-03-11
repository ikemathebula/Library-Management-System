import javafx.beans.property.SimpleStringProperty;

public class Book {
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;
    private final SimpleStringProperty status;

    public Book(String title, String author, String status) {
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.status = new SimpleStringProperty(status);
    }

    public SimpleStringProperty titleProperty() { return title; }
    public SimpleStringProperty authorProperty() { return author; }
    public SimpleStringProperty statusProperty() { return status; }

    public String getTitle() { return title.get(); }
    public String getAuthor() { return author.get(); }
    public String getStatus() { return status.get(); }
}

