package TextRepositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Exceptions.DBException.DBException;
import Exceptions.PostException.PostNotFoundException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.AdvancedPost;
import Models.Post;
import Models.Profile;
import Repositories.IPostRepository;
import Repositories.IProfileRepository;
import Utils.IOUtils;


public class PostRepository implements IPostRepository {
    private List<Post> posts = new ArrayList<Post>();
    IProfileRepository profileRepository;

    public PostRepository(IProfileRepository profileRepository){
        this.profileRepository = profileRepository;
        loadPostsfromFile("src/data/posts.txt");
    }

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

    public void loadPostsfromFile(String filepath) {
        // Formato em que os dados são lidos:
        // Post = TIPO;ID;TEXTO;IDODONO;TIME;LIKES;DISLIKES
        // AdvancedPost = TIPO;ID;TEXTO;IDODONO;TIME;LIKES;DISLIKES;REAMAININGVIEWS;HASHTAGS
        List<String> lines = IOUtils.readLinesOnFile(filepath);
        Stream<String> linesStream = lines.stream();
        linesStream.forEach(line -> {
            String[] data = line.split(";");
            try {
                switch (data[0]) {
                    case "P":
                        // incluindo o post segundo os dados do arquivo
                        includePost(
                                new Post(Integer.parseInt(data[1]), data[2],
                                        profileRepository.findProfileById(Integer.parseInt(data[3])), LocalDateTime.parse(data[4]),
                                        Integer.parseInt(data[5]), Integer.parseInt(data[6])));
                        break;

                    case "AP":
                        // Criando o post a ser adicionado
                        AdvancedPost toBeAdded = new AdvancedPost(Integer.parseInt(data[1]), data[2],
                                profileRepository.findProfileById(Integer.parseInt(data[3])), Integer.parseInt(data[5]),
                                Integer.parseInt(data[6]), LocalDateTime.parse(data[4]), Integer.parseInt(data[7]));

                        // Pegando só as hashtags do arquivo
                        String[] hashtags = data[8].split("-");

                        // Adcionando as hashtags do arquivo ao perfil
                        for (String hashtag : hashtags) {
                            toBeAdded.addHashtag(hashtag);
                        }
                        includePost(toBeAdded);
                        break;
                }
            } catch (ProfileNotFoundException e) {
                System.out.println("DB ERROR: user found in file not related to any post");
            } catch (NumberFormatException e) {
               System.out.println("DB ERROR: invalid data in file");
            } catch (DBException e) {
                System.out.println("DB ERROR: " + e.getMessage());
            }
        });
    }
    
}
