import java.time.LocalDateTime;

public class Post {
    private int id;
    private String title;
    private String link;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return title.equals(post.getTitle()) && description.equals(post.getDescription()) && created.equals(post.getCreated());
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + ((title == null) ? 0 : title.hashCode());
        result = 31 * result + ((description == null) ? 0 : description.hashCode());
        result = 31 * result + ((created == null) ? 0 : created.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return String.format("id = %s, create = %s , title = %s, link = %s, description = %s", id, created, title, link, description);
    }
}
