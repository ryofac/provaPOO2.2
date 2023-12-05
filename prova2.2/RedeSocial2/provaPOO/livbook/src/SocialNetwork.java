import Repositories.ProfileRepository;
import Utils.ConsoleColors;
import Utils.IOUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import Exceptions.AlreadyExistsException;
import Exceptions.PostException.PostNotFoundException;
import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.AdvancedPost;
import Models.Post;
import Models.Profile;
import Repositories.PostRepository;

public class SocialNetwork {
    private ProfileRepository profileRepository;
    private PostRepository postRepository;

    public SocialNetwork(ProfileRepository profileRepository, PostRepository postRepository) {
        this.profileRepository = profileRepository;
        this.postRepository = postRepository;
    }

    public Profile createProfile(String username, String email) {
        // O id sempre vai ser o id do último da lista + 1
        if (profileRepository.getAllProfiles().isEmpty()) {
            return new Profile(1, username, email);

        }
        Integer id = profileRepository.getAllProfiles().getLast().getId() + 1;
        return new Profile(id, username, email);

    }

    public Post createPost(String text, Profile owner) {
        // O id sempre vai ser o id do último da lista + 1
        if (postRepository.getAllPosts().isEmpty()) {
            return new Post(1, text, owner);
        }
        Integer id = postRepository.getAllPosts().getLast().getId() + 1;
        return new Post(id, text, owner);

    }

    public AdvancedPost createAdvancedPost(String text, Profile owner, Integer remainingViews) {
        if (postRepository.getAllPosts().isEmpty()) {
            return new AdvancedPost(1, text, owner, remainingViews);
        }
        Integer id = postRepository.getAllPosts().getLast().getId() + 1;
        return new AdvancedPost(id, text, owner, remainingViews);

    }

    public void removeProfile(Integer id) {
        try {
            var foundedById = profileRepository.findProfileById(id).get();
            postRepository.removePostsFromUser(foundedById);
            profileRepository.removeProfile(id);
        } catch (ProfileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    public void includeProfile(Profile profile) throws ProfileAlreadyExistsException {

        Optional<Profile> foundedById = profileRepository.findProfileById(profile.getId());
        Optional<Profile> foundedByEmail = profileRepository.findProfileByEmail(profile.getEmail());
        Optional<Profile> foundedByName = profileRepository.findProfileByName(profile.getName());

        if (foundedById.isPresent()) {
            throw new ProfileAlreadyExistsException("Profile with this id already exists");
        }
        if (foundedByName.isPresent()) {
            throw new ProfileAlreadyExistsException("Profile with this name already exists");
        }
        if (foundedByEmail.isPresent()) {
            throw new ProfileAlreadyExistsException("Profile with this email already exists");

        }
        profileRepository.addProfile(profile);
    }

    public Profile findProfileById(Integer id) throws ProfileNotFoundException {
        var founded = profileRepository.findProfileById(id);
        if (founded.isPresent()) {
            return founded.get();
        }
        throw new ProfileNotFoundException("Profile with this id not founded!");
    }

    public Profile findProfileByEmail(String email) throws ProfileNotFoundException {
        var founded = profileRepository.findProfileByEmail(email);
        if (founded.isPresent()) {
            return founded.get();
        }
        throw new ProfileNotFoundException("Profile with this email not founded!");
    }

    public Profile findProfileByName(String name) throws ProfileNotFoundException {
        var founded = profileRepository.findProfileByName(name);
        if (founded.isPresent()) {
            return founded.get();
        }
        throw new ProfileNotFoundException("Profile with this name not founded!");
    }

    public List<Post> findPostsbyOwner(Profile owner) throws PostNotFoundException {
        List<Post> postsFounded = postRepository.findPostByOwner(owner);
        if (postsFounded.size() == 0) {
            throw new PostNotFoundException("Posts with this hashtag does not exist");
        }
        return postsFounded;

    }

    public Post findPostsbyId(Integer id) throws PostNotFoundException {
        Optional<Post> postFounded = postRepository.findPostById(id);
        if (postFounded.isEmpty()) {
            throw new PostNotFoundException("This post does not exist");
        }
        return postFounded.get();

    }

    public List<Post> findPostByHashtag(String hashtag) throws PostNotFoundException {
        List<Post> postFounded = postRepository.findPostByHashtag(hashtag);
        if (postFounded.isEmpty()) {
            throw new PostNotFoundException("This post does not exist");
        }
        return postFounded;
    }

    public void like(Integer idPost) throws PostNotFoundException {
        try {
            Post founded = this.findPostsbyId(idPost);
            founded.like();
        } catch (PostNotFoundException e) {
            throw e;
        }

    }

    public void dislike(Integer idPost) throws PostNotFoundException {
        try {
            Post founded = this.findPostsbyId(idPost);
            founded.dislike();
        } catch (PostNotFoundException e) {
            throw e;
        }
    }

    public String formatProfile(Profile profile) {
        String formated = String.format("""
                ===================================
                        <ID: %d> @%s : %s
                ===================================""", profile.getId(), profile.getName(), profile.getEmail());
        return formated;
    }

    

    public void decrementViews(Integer idPost) throws ProfileNotFoundException {
        Optional<Post> founded = postRepository.findPostById(idPost);
        if (founded.isEmpty()) {
            throw new ProfileNotFoundException("This post doesn't exist!");
        }
        Post post = founded.get();
        if (!(post instanceof AdvancedPost)) {
            System.out.println("Post não é uma instância de ADVANCED POST");
            return;
        }
        post.dislike();

    }

    public List<Post> getAllPosts() {
        return postRepository.getAllPosts();

    }

    public void includePost(Post post) {
        postRepository.includePost(post);
    }

   

    public List<Post> findPostByProfile(String searchTerm) throws ProfileNotFoundException {
        List<Post> postsFounded = postRepository.findPostByProfile(searchTerm);
        if (postsFounded.size() == 0) {
            throw new ProfileNotFoundException("Posts with this profile does not exist");
        }
        return postsFounded;
    }

    List<Post> findPostByPhrase(String searchTerm) throws ProfileNotFoundException {
        List<Post> postsFounded = postRepository.findPostByPhrase(searchTerm);
        if (postsFounded.size() == 0) {
            throw new ProfileNotFoundException("Posts with this word in text does not exist");
        }
        return postsFounded;
    }

    public void likePost(Integer idPost) throws ProfileNotFoundException {
        Post post = findPostsbyId(idPost);
        post.like();
        postRepository.updatePost(post);
    }

    public void dislikePost(Integer idPost) throws ProfileNotFoundException {
        Post post = findPostsbyId(idPost);
        post.dislike();
        postRepository.updatePost(post);
    }

    public void showPopularPosts() {
        for (Post post : postRepository.getAllPosts()) {
            if (post.isPopular()) {
                System.out.println(formatPost(post));
            }
        }
    }

    public void showPopularHashtags() {
        List<String> hashtags = postRepository.getHashtags();
        for (String hashtag : hashtags) {
            System.out.println(hashtag);
        }
    }

    public void viewPosts() {
        for (Post post : getAllPosts()) {
            if (post instanceof AdvancedPost) {
                ((AdvancedPost) post).decrementViews();
            }
        }

    }

    public void deletePost(Integer idPost) throws ProfileNotFoundException {
        Optional<Post> founded = postRepository.findPostById(idPost);
        if (founded.isEmpty()) {
            throw new ProfileNotFoundException("Post with this id does not exist");
        }
        postRepository.deletePost(founded.get());
    }

    // Como vem os dados :
    // Post = TIPO;ID;TEXTO;IDODONO;TIME;LIKES;DISLIKES
    // AdvancedPost =
    // TIPO;ID;TEXTO;IDODONO;TIME;LIKES;DISLIKES;REAMAININGVIEWS;HASHTAGS
    public void loadPostsfromFile(String filepath) {
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
                                        findProfileById(Integer.parseInt(data[3])), LocalDateTime.parse(data[4]),
                                        Integer.parseInt(data[5]), Integer.parseInt(data[6])));
                        break;

                    case "AP":
                        // Criando o post a ser adicionado
                        AdvancedPost toBeAdded = new AdvancedPost(Integer.parseInt(data[1]), data[2],
                                findProfileById(Integer.parseInt(data[3])), Integer.parseInt(data[5]),
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
                System.out.println("ERROR: user founded in file not related to any post");
                e.printStackTrace();
            }
        });
    }

    // Como vem os dados: id;name;email
    public void loadProfilesFromFile(String filepath) {
        List<String> lines = IOUtils.readLinesOnFile(filepath);
        Stream<String> linesStream = lines.stream();
        linesStream.forEach(line -> {
            String[] data = line.split(";");
            try {
                includeProfile(new Profile(Integer.parseInt(data[0]), data[1], data[2]));

            } catch (ProfileAlreadyExistsException e) {
                System.err.println("ERROR: Conflict with existing user in memory and in file");
                e.printStackTrace();
            }
        });

    }

    public void saveData(String profilePath, String postPath) {
        profileRepository.writeProfilesinFile(profilePath);
        postRepository.writePostsinFile(postPath);

    }

    public void readData(String profilePath, String postPath) {
        loadProfilesFromFile(profilePath);
        loadPostsfromFile(postPath);
    }
}
