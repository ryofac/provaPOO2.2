package Models;

import java.time.LocalDateTime;

public class Post {
    private Integer _id;
    private String _text;
    private Integer _likes = 0;
    private Integer _dislikes = 0;
    private LocalDateTime _createdTime;
    private Profile _owner;

    public Post(Integer _id, String _text, Profile _owner) {
        this._id = _id;
        this._text = _text;
        this._owner = _owner;
        this._createdTime = LocalDateTime.now();
    }

    public Post(Integer _id, String _text, Profile _owner, LocalDateTime createdTime, Integer likes, Integer dislikes) {
        this._id = _id;
        this._text = _text;
        this._owner = _owner;
        this._createdTime = createdTime;
        this._likes = likes;
        this._dislikes = dislikes;
    }

    public Boolean isPopular() {
        // Returns true if the like amount is greatear than 50 % of dislikes
        return this._likes > (50 / 100 * this._dislikes);
    }

    public void like() {
        this._likes++;
    }

    public Integer getLikes() {
        return this._likes;
    }

    public Integer getDislikes() {
        return this._dislikes;
    }

    public void dislike() {
        this._dislikes++;
    }

    public Integer getId() {
        return this._id;
    }

    public String getText() {
        return this._text;
    }

    public void setText(String text) {
        this._text = text;
    }

    public Profile getOwner() {
        return this._owner;
    }

    public LocalDateTime getCreatedTime() {
        return this._createdTime;
    }

    // Usado para facilitar a gravação em arquivo:

    // TIPO;ID;TEXTO;IDODONO;TIME;LIKES;DISLIKES
    @Override
    public String toString() {
        String type = this instanceof AdvancedPost ? "AP" : "P";

        return String.format("%s;%d;%s;%d;%s;%d;%d", type, getId(), getText(), getOwner().getId(), getCreatedTime(),
                getLikes(), getDislikes());

    }

}
