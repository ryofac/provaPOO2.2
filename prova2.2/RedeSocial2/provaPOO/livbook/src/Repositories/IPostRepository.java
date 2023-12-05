package Repositories;

import java.util.List;

import Exceptions.PostException.PostNotFoundException;
import Models.Post;
import Models.Profile;

public interface IPostRepository {

    /**
     * Método que inclui uma instância de Post no repositório de Posts
    */
    void includePost(Post post);

    /**
     * Esse método é utilizado para retornar todos os posts armazenados
     * @return todos os posts criados
     */
    List<Post> getAllPosts();

    /**
     * Esse método é um atalho para o método getAllPosts().size()
     * @return a quantidade total de posts armazenados
     */
    Integer getPostAmount();
    
    /**
     * Esse método é responsável por remover os posts que já expiraram, se existirem
     */
    void removeSeenPosts();
    
    /**
     * Esse método remove todos os posts assossiados a um usuário especificado
     * @param profile o perfil assossiado aos posts que devem ser removidos
     */
    void removePostsFromUser(Profile profile);

    /**
     * Método que busca e retorna um post baseado no id fornecido
     * @param id o id do post a ser buscado
     * @return o post buscado, se encontrado
     * @throws PostNotFoundException caso o post não exista
     */
    Post findPostById(Integer id) throws PostNotFoundException;

    /**
     * Método que busca e retorna um post baseado no seu dono (uma instância da classe perfil)
     * @param owner uma instância da classe Perfil, que representa o dono do post
     * @return Uma lista de posts pertencentes ao dono, se existirem posts
     * @throws PostNotFoundException caso não exista nenhum post assossiado a esse dono
     */
    List<Post> findPostByOwner(Profile owner) throws PostNotFoundException;

    /**
     * Método que busca e retorna um post baseado em uma hashtag presente
     * @param hashtag a string que representa a hashtag a ser buscada
     * @return Uma lista de posts que utilizam a hashtag fornecida, se existirem
     * @throws PostNotFoundException caso não exista nenhum post assossiado a essa hashtag
     */
    List<Post> findPostByHashtag(String hashtag) throws PostNotFoundException;

    /**
     * Método que busca e retorna um post baseado no nome do Perfil do dono
     * @param searchTerm a string que representa o nome do perfil a ser buscado
     * @return Uma lista de posts que utilizam a hashtag fornecida, se existirem
     * @throws PostNotFoundException caso não exista nenhum post assossiado a essa hashtag
     */
    List<Post> findPostByProfile(String searchTerm) throws PostNotFoundException;

    /**
     * Método que busca e retorna um post baseado em um trecho do texto do post
     * @param searchTerm a string que representa o trecho a buscado
     * @return Uma lista de posts que utiliza do trecho fornecido, se existirem
     * @throws PostNotFoundException caso não exista nenhum post assossiado a esse trecho
     */
    List<Post> findPostByPhrase(String searchTerm) throws PostNotFoundException;

    /**
     * Deleta um post fornecido de todos os 
     * @param postFound
     * @throws PostNotFoundException
     */
    void deletePost(int idPost) throws PostNotFoundException;


    
}