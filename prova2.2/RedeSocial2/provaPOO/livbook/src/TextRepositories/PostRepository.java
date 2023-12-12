package TextRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Exceptions.DBException.DBException;
import Exceptions.PostException.PostNotFoundException;
import Models.AdvancedPost;
import Models.Post;
import Models.Profile;
import Repositories.IPostRepository;


public class PostRepository implements IPostRepository {
    private List<Post> posts = new ArrayList<Post>();

    @Override
    public List<Post> getAllPosts() {
        return posts;
    }

    @Override
    public Integer getPostAmount() {
        return posts.size();
    }

    @Override
    public void removeSeenPosts() {
        posts.removeIf(post -> post instanceof AdvancedPost && !((AdvancedPost) post).canSee());

    }

    @Override
    public void removePostsFromUser(Profile profile) {
        posts.removeIf(post -> post.getOwner().equals(profile));
    }

    @Override
    public Post findPostById(Integer id) throws PostNotFoundException {
        Stream<Post> postsStream = posts.stream();
        Stream<Post> postsFound = postsStream.filter(post -> post.getId() == id);
        Optional<Post> found = postsFound.findFirst();
        return found.orElseThrow(() -> new PostNotFoundException("Post not found!"));
    }

    @Override
    public List<Post> findPostByOwner(Profile owner) throws PostNotFoundException {
        Stream<Post> postsStream = posts.stream();
        Stream<Post> postsFinded = postsStream.filter(post -> post.getOwner().equals(owner));
        List<Post> postList = postsFinded.collect(Collectors.toList());
        if(postList.isEmpty()) throw new PostNotFoundException("Post not found!");
        return postList;
    }

    @Override
    public List<Post> findPostByHashtag(String hashtag) throws PostNotFoundException{
        Stream<Post> postsStream = posts.stream();
        Stream<Post> postsFinded = postsStream.filter(post -> {
            if (post instanceof AdvancedPost) {
                return ((AdvancedPost) post).hasHashtag(hashtag);
            }
            return false;
        });
        List<Post> postList = postsFinded.collect(Collectors.toList());
        if(postList.isEmpty()) throw new PostNotFoundException("Post not found!");
        return postList;

    }

    @Override
    public void includePost(Post post) {
        posts.add(post);
    }
    
    @Override
    public List<Post> findPostByPhrase(String searchTerm) throws PostNotFoundException {
        Stream<Post> postsStream = posts.stream();
        Stream<Post> postsFinded = postsStream.filter(post -> post.getText().contains(searchTerm));
        List<Post> postList = postsFinded.collect(Collectors.toList());
        if(postList.isEmpty()) throw new PostNotFoundException("Post not found!");
        return postList;
    }


    @Override
    public void deletePost(int idPost) throws PostNotFoundException {
        Post found = findPostById(idPost);
        posts.remove(found);
    }

    @Override
    public void addLike(int idPost) throws PostNotFoundException, DBException {
        Post found = findPostById(idPost);
        found.like();
    }

    @Override
    public void addDisklike(int idPost) throws PostNotFoundException, DBException {
        Post found = findPostById(idPost);
        found.dislike();
    }

    @Override
    public void decrementViews(int idPost) throws PostNotFoundException, DBException {
        Post found = findPostById(idPost);
        if(found instanceof AdvancedPost){
            ((AdvancedPost) found).decrementViews();
            return;
        }
    }
    
}
