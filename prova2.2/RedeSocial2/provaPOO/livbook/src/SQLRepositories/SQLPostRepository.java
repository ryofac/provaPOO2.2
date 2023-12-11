package SQLRepositories;

import java.util.List;

import Exceptions.PostException.PostNotFoundException;
import Models.Post;
import Models.Profile;
import Repositories.IPostRepository;

public class SQLPostRepository implements IPostRepository {

    @Override
    public void includePost(Post post) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'includePost'");
    }

    @Override
    public List<Post> getAllPosts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllPosts'");
    }

    @Override
    public Integer getPostAmount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPostAmount'");
    }

    @Override
    public void removeSeenPosts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeSeenPosts'");
    }

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
    
}
