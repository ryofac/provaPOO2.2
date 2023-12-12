import java.util.List;

import Exceptions.DBException.DBException;
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
    /**s
     * Método responsável por retornar todos os perfis dod repositório de perfis
     * @return todos os perfis disponíveis
     * @throws DBException 
     */
    public List<Profile> getAllProfiles() throws DBException {
        return profileRepository.getAllProfiles();
    }

    /**
     * Método responsável por retornar todos os posts do respositório de posts
     * @return todos os posts disponíveis
     * @throws DBException 
     */
    public List<Post> getAllPosts() throws DBException {
        return postRepository.getAllPosts();
    }
    
    /**
     * Verifica se existem posts
     * @return true se a quantidade de posts disponível é maior que 0
     * @throws DBException 
     */
    public boolean existsPosts() {
        try	{
            return postRepository.getPostAmount() > 0;
        } catch (DBException e) {
            return false;
        }
    }

     /**
     * Verifica se existem perfis
     * @return true se a quantidade de perfis disponível é maior que 0
     * @throws DBException 
     */
    public boolean existsProfiles(){
        try{
            return profileRepository.getProfileAmount() > 0;
        } catch (DBException e) {
            return false;
        }
        
    }

    /**
     * Método que retorna o próximo id possível do perfil a ser criado
     * @return um inteiro que representa o próximo id do perfil a ser criado
     * @throws DBException 
     */
    private int _getNextProfileId() throws DBException{
         return profileRepository.getAllProfiles().get(profileRepository.getProfileAmount() - 1).getId() + 1;
    }

    /**
     * Método que retorna o próximo id possível do post a ser criado
     * @return um inteiro que representa o próximo id do post a ser criado
     * @throws DBException 
     */
    private int _getNextPostId() throws DBException{
         return postRepository.getAllPosts().get(postRepository.getPostAmount() - 1).getId() + 1;
    }

    /**
     * Método que responsável por encapsular a lógica de criar um perfil
     * @param username o nome de usuário do perfil a ser criado
     * @param email o email do perfil a ser criado
     * @return uma instância de perfil, com o id gerado baseando-se na quantidade de perfis presentes
     * @throws DBException 
     */
    public Profile createProfile(String username, String email) throws DBException {
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
     * @throws DBException 
     */
    public Post createPost(String text, Profile owner) throws DBException {
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
     * @throws DBException 
      */
    public AdvancedPost createAdvancedPost(String text, Profile owner, Integer remainingViews) throws DBException {
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
     * @throws DBException 
     */
    public void removeProfile(Integer id) throws ProfileNotFoundException, DBException {
        var foundById = profileRepository.findProfileById(id);
        postRepository.removePostsFromUser(foundById);
        profileRepository.removeProfile(id);

    }

    public void includeProfile(Profile profile) throws ProfileAlreadyExistsException, DBException {
        try{
            findProfileByName(profile.getName());
            throw new ProfileAlreadyExistsException("Profile already exists");
        } catch (ProfileNotFoundException e) {
            try{
                findProfileByEmail(profile.getEmail());
                throw new ProfileAlreadyExistsException("Profile already exists");
            } catch (ProfileNotFoundException ex) {
                profileRepository.addProfile(profile);
            }
        }
        
    }
    
    public Profile findProfileById(Integer id) throws ProfileNotFoundException, DBException {
        var foundProfile = profileRepository.findProfileById(id);
        return foundProfile;
    }

    public Profile findProfileByEmail(String email) throws ProfileNotFoundException, DBException {
        var foundProfile = profileRepository.findProfileByEmail(email);
        return foundProfile;
    }

    public Profile findProfileByName(String name) throws ProfileNotFoundException, DBException {
        var foundProfile = profileRepository.findProfileByName(name);
        return foundProfile;
    }

    public List<Post> findPostsbyOwner(Profile owner) throws PostNotFoundException, DBException {
        List<Post> postsFound = postRepository.findPostByOwner(owner);
        return postsFound;

    }

    public Post findPostsbyId(Integer id) throws PostNotFoundException, DBException {
        Post postFound = postRepository.findPostById(id);
        return postFound;

    }

    public List<Post> findPostByHashtag(String hashtag) throws PostNotFoundException, DBException {
        List<Post> postsFound = postRepository.findPostByHashtag(hashtag);
        return postsFound;
    }

    public void like(Integer idPost) throws PostNotFoundException, DBException {
        postRepository.addDisklike(idPost);
    }

    public void dislike(Integer idPost) throws PostNotFoundException, DBException {
        postRepository.addDisklike(idPost);
    }

    public void decrementViews(Integer idPost) throws PostNotFoundException, DBException {
        postRepository.decrementViews(idPost);

    }
    
    public void includePost(Post post) throws DBException {
        postRepository.includePost(post);
    }  

    List<Post> findPostByPhrase(String searchTerm) throws PostNotFoundException, DBException {
        List<Post> postsFound = postRepository.findPostByPhrase(searchTerm);
        return postsFound;
    }

    public void likePost(Integer idPost) throws PostNotFoundException, DBException {
        postRepository.addLike(idPost);
    }

    public void dislikePost(Integer idPost) throws PostNotFoundException, DBException {
        postRepository.addDisklike(idPost);
    }

    public void deletePost(Integer idPost) throws PostNotFoundException, DBException {
        postRepository.deletePost(idPost);
    }

    public void seePost(int idPost) throws DBException, PostNotFoundException {
        postRepository.decrementViews(idPost);
    }

    public void removeSeenPosts() throws DBException{
       postRepository.removeSeenPosts();
    }
    
}
