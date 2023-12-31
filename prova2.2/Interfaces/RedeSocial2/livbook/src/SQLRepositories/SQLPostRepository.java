package SQLRepositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Exceptions.DBException.DBException;
import Exceptions.PostException.PostNotFoundException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.AdvancedPost;
import Models.Post;
import Models.Profile;
import Repositories.IPostRepository;
import Repositories.IProfileRepository;

public class SQLPostRepository implements IPostRepository {
    DBCom com;
    IProfileRepository profileRepository;
    public SQLPostRepository(DBCom com, IProfileRepository profileRepository){
        this.com = com;
        this.profileRepository = profileRepository;
    }

    @Override
    public void includePost(Post post) throws DBException {
        try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "INSERT INTO POST( " +
                    "ID, TEXT, OWNERID, CREATEDTIME, LIKES, DISLIKES, HASHTAGS, REMAININGVIEWS" + 
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                );

            psmt.setInt(1, post.getId());
            psmt.setString(2, post.getText());
            psmt.setInt(3, post.getOwner().getId());
            psmt.setTimestamp(4, Timestamp.valueOf(post.getCreatedTime()));
            psmt.setInt(5, post.getLikes());
            psmt.setInt(6, post.getDislikes());
            if (post instanceof AdvancedPost){
                psmt.setString(7, String.join(",", ((AdvancedPost) post).getHashtags()));
                psmt.setInt(8, ((AdvancedPost) post).getRemainingViews());
            } else{
                psmt.setNull(7, Types.VARCHAR);
                psmt.setNull(8, Types.INTEGER);
            }
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("ERROR while trying to add post into database", e);
        } finally {
            com.close();
        }
    }

    @Override
    public List<Post> getAllPosts() throws DBException {
        try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT * FROM POST"
            );
            var rs = psmt.executeQuery();
            List<Post> posts = new ArrayList<Post>();
            while (rs.next()){
                var id = rs.getInt("ID");
                var text = rs.getString("TEXT");
                var ownerId = rs.getInt("OWNERID");
                var createdTime = rs.getTimestamp("CREATEDTIME").toLocalDateTime();
                var likes = rs.getInt("LIKES");
                var dislikes = rs.getInt("DISLIKES");
                var hashtags = rs.getString("HASHTAGS");
                var remainingViews = rs.getInt("REMAININGVIEWS");
                if (hashtags != null){
                    List<String> hashtagsList = Arrays.asList(hashtags.split(","));
                    AdvancedPost toAdd = new AdvancedPost(id, text, profileRepository.findProfileById(ownerId), likes, dislikes, createdTime, remainingViews);
                    hashtagsList.stream().forEach(hashtag -> toAdd.addHashtag(hashtag));
                    posts.add(toAdd);
                } else{
                    posts.add(new Post(id, text, profileRepository.findProfileById(ownerId), createdTime, likes, dislikes));
                }
            }
            return posts;
            
        } catch (SQLException e) {
            throw new DBException("ERROR while trying to get all posts from database: " + e.getCause(), e);
        } catch (ProfileNotFoundException e){
            throw new DBException("ERROR while trying to get all posts from database" + e.getCause(), e);
        } finally {
            com.close();
        }
        
    }

    @Override
    public Integer getPostAmount() throws DBException {
       try{
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT COUNT(*) FROM POST"
            );
            var rs = psmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e){
            throw new DBException("ERROR while trying to get post amount from database", e);
        } finally {
            com.close();
        }
    }

    @Override
    public void removePostsFromUser(Profile profile) throws DBException {
        try { 
            com.connect();
            // Deletando os posts associados ao perfil
            PreparedStatement psmt = com.con.prepareStatement(
            "DELETE FROM POST WHERE OWNERID = ?"
            );
            psmt.setInt(1, profile.getId());
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("ERROR while trying to remove posts from user", e);
        } finally { 
            com.close();
        }
    }

    @Override
    public Post findPostById(Integer id) throws PostNotFoundException, DBException {
        try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT * FROM POST WHERE ID = ?"
            );
            psmt.setInt(1, id);
            var rs = psmt.executeQuery();
            if (rs.next()){
                var text = rs.getString("TEXT");
                var ownerId = rs.getInt("OWNERID");
                var createdTime = rs.getTimestamp("CREATEDTIME").toLocalDateTime();
                var likes = rs.getInt("LIKES");
                var dislikes = rs.getInt("DISLIKES");
                var hashtags = rs.getString("HASHTAGS");
                var remainingViews = rs.getInt("REMAININGVIEWS");
                if (hashtags != null){
                    List<String> hashtagsList = Arrays.asList(hashtags.split(","));
                    AdvancedPost toAdd = new AdvancedPost(id, text, profileRepository.findProfileById(ownerId), likes, dislikes, createdTime, remainingViews);
                    hashtagsList.stream().forEach(hashtag -> toAdd.addHashtag(hashtag));
                    return toAdd;
                } else{
                    return new Post(id, text, profileRepository.findProfileById(ownerId), createdTime, likes, dislikes);
                }
            } else{
                throw new PostNotFoundException("Post with id " + id + " not found");
            }
        } catch (SQLException e) {
            throw new DBException("Post with id " + id + " not found", e);
        } catch (ProfileNotFoundException e){
            throw new PostNotFoundException("Post with id " + id + " not found");
        } finally {
            com.close();

        }
    }

    @Override
    public List<Post> findPostByOwner(Profile owner) throws PostNotFoundException, DBException {
        try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT * FROM POST WHERE OWNERID = ?"
            );
            psmt.setInt(1, owner.getId());
            var rs = psmt.executeQuery();
            List<Post> posts = new ArrayList<Post>();
            while (rs.next()){
                var id = rs.getInt("ID");
                var text = rs.getString("TEXT");
                var ownerId = rs.getInt("OWNERID");
                var createdTime = rs.getTimestamp("CREATEDTIME").toLocalDateTime();
                var likes = rs.getInt("LIKES");
                var dislikes = rs.getInt("DISLIKES");
                var hashtags = rs.getString("HASHTAGS");
                var remainingViews = rs.getInt("REMAININGVIEWS");
                if (hashtags != null){
                    List<String> hashtagsList = Arrays.asList(hashtags.split(","));
                    AdvancedPost toAdd = new AdvancedPost(id, text, profileRepository.findProfileById(ownerId), likes, dislikes, createdTime, remainingViews);
                    hashtagsList.stream().forEach(hashtag -> toAdd.addHashtag(hashtag));
                    posts.add(toAdd);
                } else{
                    posts.add(new Post(id, text, profileRepository.findProfileById(ownerId), createdTime, likes, dislikes));
                }
            }
            return posts;
        } catch (SQLException e) {
            throw new PostNotFoundException("Posts from owner " + owner.getId() + " not found");
        } catch (ProfileNotFoundException e){
            throw new PostNotFoundException("Posts from owner " + owner.getId() + " not found");
        } finally {
            com.close();
        }
    }

    @Override
    public List<Post> findPostByHashtag(String hashtag) throws PostNotFoundException, DBException {
        try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT * FROM POST WHERE HASHTAGS LIKE ?"
            );
            psmt.setString(1, "%" + hashtag + "%");
            var rs = psmt.executeQuery();
            List<Post> posts = new ArrayList<Post>();
            while (rs.next()){
                var id = rs.getInt("ID");
                var text = rs.getString("TEXT");
                var ownerId = rs.getInt("OWNERID");
                var createdTime = rs.getTimestamp("CREATEDTIME").toLocalDateTime();
                var likes = rs.getInt("LIKES");
                var dislikes = rs.getInt("DISLIKES");
                var hashtags = rs.getString("HASHTAGS");
                var remainingViews = rs.getInt("REMAININGVIEWS");
                if (hashtags != null){
                    List<String> hashtagsList = Arrays.asList(hashtags.split(","));
                    AdvancedPost toAdd = new AdvancedPost(id, text, profileRepository.findProfileById(ownerId), likes, dislikes, createdTime, remainingViews);
                    hashtagsList.stream().forEach(hasht -> toAdd.addHashtag(hasht));
                    posts.add(toAdd);
                } else{
                    posts.add(new Post(id, text, profileRepository.findProfileById(ownerId), createdTime, likes, dislikes));
                }
            }
            return posts;
        } catch (SQLException e) {
            throw new PostNotFoundException("Posts with hashtag " + hashtag + " not found");
        } catch (ProfileNotFoundException e){
            throw new PostNotFoundException("Posts with hashtag " + hashtag + " not found");
        } finally {
            com.close();
        }
    }

    @Override
    public List<Post> findPostByPhrase(String searchTerm) throws PostNotFoundException, DBException {
        try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT * FROM POST WHERE TEXT ILIKE '%?%'"
                );
            psmt.setString(1, searchTerm);
            var rs = psmt.executeQuery();
            List<Post> posts = new ArrayList<Post>();
            if(!rs.next()) throw new PostNotFoundException("Posts with phrase " + searchTerm + " not found");

            while (rs.next()){
                var id = rs.getInt("ID");
                var text = rs.getString("TEXT");
                var ownerId = rs.getInt("OWNERID");
                var createdTime = rs.getTimestamp("CREATEDTIME").toLocalDateTime();
                var likes = rs.getInt("LIKES");
                var dislikes = rs.getInt("DISLIKES");
                var hashtags = rs.getString("HASHTAGS");
                var remainingViews = rs.getInt("REMAININGVIEWS");
                if (hashtags != null){
                    List<String> hashtagsList = Arrays.asList(hashtags.split(","));
                    AdvancedPost toAdd = new AdvancedPost(id, text, profileRepository.findProfileById(ownerId), likes, dislikes, createdTime, remainingViews);
                    hashtagsList.stream().forEach(hashtag -> toAdd.addHashtag(hashtag));
                    posts.add(toAdd);
                } else{
                    posts.add(new Post(id, text, profileRepository.findProfileById(ownerId), createdTime, likes, dislikes));
                }
            }
            return posts;
           
        } catch (SQLException e) {
            throw new DBException("Posts with phrase " + searchTerm + " not found", e);
        } catch (ProfileNotFoundException e){
            throw new PostNotFoundException("Posts with phrase " + searchTerm + " not found");
        } finally {
            com.close();

        }
    }

    @Override
    public void deletePost(int idPost) throws PostNotFoundException, DBException {
       try {
        com.connect();
        PreparedStatement psmt = com.con.prepareStatement(
            "DELETE FROM POST WHERE ID = ?"
        );
        psmt.setInt(1, idPost);
        if(psmt.executeUpdate() == 0) throw new PostNotFoundException("Post with id " + idPost + " not found");
        } catch (SQLException e) {
            throw new DBException("Error while trying to delete post", e);
        } finally {
            com.close();
       }
    }

    @Override
    public void removeSeenPosts() throws DBException {
        com.connect();
        try {
            PreparedStatement psmt = com.con.prepareStatement(
                "DELETE FROM POST WHERE REMAININGVIEWS <= 0"
            );
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Error while trying to delete posts viewed", e);
        } finally {
            com.close();
        }
    }

    @Override
    public void addLike(int idPost) throws PostNotFoundException, DBException {
        try{
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "UPDATE POST SET LIKES = LIKES + 1 WHERE ID = ?"
            );
            psmt.setInt(1, idPost);
            if(psmt.executeUpdate() == 0) throw new PostNotFoundException("Post with id " + idPost + " not found");
        } catch (SQLException e) {
            throw new DBException("ERROR while trying to add like", e);
        } finally {
            com.close();
        }
    }

    @Override
    public void addDisklike(int idPost) throws PostNotFoundException, DBException {
        try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "UPDATE POST SET DISLIKES = DISLIKES + 1 WHERE ID = ?"
            );
            psmt.setInt(1, idPost);
            if(psmt.executeUpdate() == 0) throw new PostNotFoundException("Post with id " + idPost + " not found");
        } catch (SQLException e) {
            throw new DBException("ERROR while trying to add dislike", e);
        } finally {
            com.close();
        }
    }

    @Override
    public void decrementViews(int idPost) throws PostNotFoundException, DBException {
        try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "UPDATE POST SET REMAININGVIEWS = REMAININGVIEWS - 1 WHERE ID = ?"
            );
            psmt.setInt(1, idPost);
            if(psmt.executeUpdate() == 0) throw new PostNotFoundException("Post with id " + idPost + " not found");
        } catch (SQLException e) {
            throw new DBException("ERROR while trying to decrement views", e);
        } finally {
            com.close();
        }
    }
    
}
