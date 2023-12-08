import java.util.List;

import Exceptions.PostException.PostNotFoundException;
import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.AdvancedPost;
import Models.Post;
import Models.Profile;
import Repositories.IPostRepository;
import Repositories.IProfileRepository;

public class SocialNetwork {
    private IProfileRepository profileRepository;
    private IPostRepository postRepository;

    public SocialNetwork(IProfileRepository profileRepository, IPostRepository postRepository) {
        this.profileRepository = profileRepository;
        this.postRepository = postRepository;
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.getAllProfiles();
    }

    public List<Post> getAllPosts() {
        return postRepository.getAllPosts();
    }

    public boolean existsPosts(){
        return postRepository.getPostAmount() > 0;
    }

    public boolean existsProfiles(){
        return profileRepository.getProfileAmount() > 0;
    }

    /**
     * Método que retorna o próximo id possível do perfil a ser criado
     * @return um inteiro que representa o próximo id do perfil a ser criado
     */
    private int _getNextProfileId(){
         return profileRepository.getAllProfiles().get(profileRepository.getProfileAmount() - 1).getId() + 1;
    }

    /**
     * Método que retorna o próximo id possível do post a ser criado
     * @return um inteiro que representa o próximo id do post a ser criado
     */
    private int _getNextPostId(){
         return postRepository.getAllPosts().get(postRepository.getPostAmount() - 1).getId() + 1;
    }

    /**
     * Método que responsável por encapsular a lógica de criar um perfil
     * @param username o nome de usuário do perfil a ser criado
     * @param email o email do perfil a ser criado
     * @return uma instância de perfil, com o id gerado baseando-se na quantidade de perfis presentes
     */
    public Profile createProfile(String username, String email) {
        // O id sempre vai ser o id do último da lista + 1
        if (profileRepository.getAllProfiles().isEmpty()) {
            return new Profile(1, username, email);

        }
        Integer id = _getNextProfileId();
        return new Profile(id, username, email);

    }

    /**
     * Método que responsável por encapsular a lógica de criar um post
     * @param text o texto do post a ser criado
     * @param owner a instância de perfil que representa o dono da postagem
     * @return uma nova instância de Post, com o id gerado baseado na quantidade de posts presentes
     */
    public Post createPost(String text, Profile owner) {
        // O id sempre vai ser o id do último da lista + 1
        if (postRepository.getAllPosts().isEmpty()) {
            return new Post(1, text, owner);
        }
        Integer id = _getNextPostId();
        return new Post(id, text, owner);

    }

     /**
      * Método que responsável por encapsular a lógica de criar um post avançado
      * @param text texto do post a ser criado
      * @param owner a instância de perfil que representa o dono da postagem
      * @param remainingViews a quantidade de views restantes que o post avançado deverá ter 
      * @return uma nova instância de Advanced Post, com o id gerado baseado na quantidade de posts presentes
      */
    public AdvancedPost createAdvancedPost(String text, Profile owner, Integer remainingViews) {
        if (postRepository.getAllPosts().isEmpty()) {
            return new AdvancedPost(1, text, owner, remainingViews);
        }
        Integer id = _getNextPostId();
        return new AdvancedPost(id, text, owner, remainingViews);

    }
    
    /**
     * Método que implementa o método de remover do repositório de Perfis
     * @param id o id do perfil a ser removido
     * @throws ProfileNotFoundException caso o perfil não seja encontrado
     */
    public void removeProfile(Integer id) throws ProfileNotFoundException {
        var foundById = profileRepository.findProfileById(id);
        postRepository.removePostsFromUser(foundById);
        profileRepository.removeProfile(id);

    }

    public void includeProfile(Profile profile) throws ProfileAlreadyExistsException {
        profileRepository.addProfile(profile);
    }

    public Profile findProfileById(Integer id) throws ProfileNotFoundException {
        var foundProfile = profileRepository.findProfileById(id);
        return foundProfile;
    }

    public Profile findProfileByEmail(String email) throws ProfileNotFoundException {
        var foundProfile = profileRepository.findProfileByEmail(email);
        return foundProfile;
    }

    public Profile findProfileByName(String name) throws ProfileNotFoundException {
        var foundProfile = profileRepository.findProfileByName(name);
        return foundProfile;
    }

    public List<Post> findPostsbyOwner(Profile owner) throws PostNotFoundException {
        List<Post> postsFound = postRepository.findPostByOwner(owner);
        return postsFound;

    }

    public Post findPostsbyId(Integer id) throws PostNotFoundException {
        Post postFound = postRepository.findPostById(id);
        return postFound;

    }

    public List<Post> findPostByHashtag(String hashtag) throws PostNotFoundException {
        List<Post> postsFound = postRepository.findPostByHashtag(hashtag);
        return postsFound;
    }

    public void like(Integer idPost) throws PostNotFoundException {
        Post found = this.findPostsbyId(idPost);
        found.like();
    }

    public void dislike(Integer idPost) throws PostNotFoundException {
        Post found = this.findPostsbyId(idPost);
        found.dislike();
    }

    public void decrementViews(Integer idPost) throws PostNotFoundException {
        Post post = postRepository.findPostById(idPost);
        post.dislike();

    }

    
    

    public void includePost(Post post) {
        postRepository.includePost(post);
    }   

    public List<Post> findPostByProfile(String searchTerm) throws PostNotFoundException {
        List<Post> postsFound = postRepository.findPostByProfile(searchTerm);
        return postsFound;
    }

    List<Post> findPostByPhrase(String searchTerm) throws PostNotFoundException {
        List<Post> postsFound = postRepository.findPostByPhrase(searchTerm);
        return postsFound;
    }

    public void likePost(Integer idPost) throws PostNotFoundException {
        Post post = findPostsbyId(idPost);
        post.like();
    }

    public void dislikePost(Integer idPost) throws PostNotFoundException {
        Post post = findPostsbyId(idPost);
        post.dislike();
    }

    public void deletePost(Integer idPost) throws PostNotFoundException {
        postRepository.deletePost(idPost);
    }

    public void removeSeenPosts(){
        postRepository.removeSeenPosts();
    }

    

    
}
