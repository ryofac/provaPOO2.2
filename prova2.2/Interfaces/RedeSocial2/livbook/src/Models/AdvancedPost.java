package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdvancedPost extends Post {
    List<String> hashtags = new ArrayList<String>();
    Integer remainingViews;

    public AdvancedPost(Integer _id, String _text, Profile _owner, Integer remainingViews) {
        super(_id, _text, _owner);
        this.remainingViews = remainingViews;

    }
    
    public AdvancedPost(Integer _id, String _text, Profile _owner, Integer likes, Integer dislikes,
            LocalDateTime createdDataTime, Integer remainingViews) {
        super(_id, _text, _owner, createdDataTime, likes, dislikes);
        this.remainingViews = remainingViews;
        
    }

    public List<String> getHashtags() {
        return this.hashtags;
    }

    public Integer getRemainingViews() {
        return this.remainingViews;
    }

    public void addHashtag(String hashtag) {
        this.hashtags.add(hashtag);
    }

    public Boolean decrementViews() {
        if (this.remainingViews - 1 < 0) {
            return false;
        }
        remainingViews--;
        return true;
    }

    public Boolean hasHashtag(String hashtag) {
        for (String actualHashtag : hashtags) {
            if (hashtag.equals(actualHashtag)) {
                return true;
            }
        }
        return false;
    }

    public Boolean canSee() {
        return remainingViews > 0;
    }

    // TIPO;ID;TEXTO;IDODONO;TIME;LIKES;DISLIKES;REAMAININGVIEWS;HASHTAGS
    @Override
    public String toString() {
        String out = super.toString();
        out += String.format(";%d;", this.remainingViews);
        for (String hashtag : hashtags) {
            out += hashtag.trim() + "-";
        }
        out += '\b';
        return out;

    }
}