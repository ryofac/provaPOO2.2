package SQLRepositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
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
    public SQLPostRepository(DBCom com){
        this.com = com;
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
                    posts.add(new Post(id, text, new Profile(ownerId, null, null), createdTime, likes, dislikes));
                }
            }
            return posts;
            
        } catch (SQLException e) {
            throw new DBException("ERROR while trying to get all posts from database", e);
        } catch (ProfileNotFoundException e){
            throw new DBException("ERROR while trying to get all posts from database", e);
        } finally {
            com.close();
        }
        
    }

    @Override
    public Integer getPostAmount() throws DBException {
       try{
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
    

    // TODO: Colocar isso em rede social MOTIVO: Faz parte da regra de negócios
    @Override
    public void removeSeenPosts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeSeenPosts'");
    }

    // Método não mais necessário, pois remover usuário já remove os posts relacionados ao usuário
    // TODO: Resolver ocorrências desse método
    @Override
    public void removePostsFromUser(Profile profile) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removePostsFromUser'");
    }

    @Override
    public Post findPostById(Integer id) throws PostNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPostById'");
    }

    @Override
    public List<Post> findPostByOwner(Profile owner) throws PostNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPostByOwner'");
    }

    @Override
    public List<Post> findPostByHashtag(String hashtag) throws PostNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPostByHashtag'");
    }

    @Override
    public List<Post> findPostByProfile(String searchTerm) throws PostNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPostByProfile'");
    }

    @Override
    public List<Post> findPostByPhrase(String searchTerm) throws PostNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPostByPhrase'");
    }

    @Override
    public void deletePost(int idPost) throws PostNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePost'");
    }

    // Main para testes
    public static void main(String[] args) throws DBException {
        var repo = new SQLPostRepository(new DBCom());
        repo.includePost(new Post(1, "Eu amo java", new Profile(1, "robson", "ryan@gmail"), LocalDateTime.now(), 10, 10));
    }
    
}
