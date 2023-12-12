package Repositories;

import java.util.List;

import Exceptions.DBException.DBException;
import Exceptions.PostException.PostNotFoundException;
import Models.Post;
import Models.Profile;

public interface IPostRepository {

    /**
     * Método que inclui uma instância de Post no repositório de Posts
    */
    void includePost(Post post) throws DBException;

    /**
     * Esse método é utilizado para retornar todos os posts armazenados
     * @return todos os posts criados
     * @throws DBException 
     */
    List<Post> getAllPosts() throws DBException;

    /**
     * Esse método é um atalho para o método getAllPosts().size()
     * @return a quantidade total de posts armazenados
     * @throws DBException 
     */
    Integer getPostAmount() throws DBException;
    
    /**
     * Esse método é responsável por remover os posts que já expiraram, se existirem
     * @throws DBException 
     */
    void removeSeenPosts() throws DBException;

    /**
     * Método que busca e retorna um post baseado no id fornecido
     * @param id o id do post a ser buscado
     * @return o post buscado, se encontrado
     * @throws PostNotFoundException caso o post não exista
     * @throws DBException 
     */
    Post findPostById(Integer id) throws PostNotFoundException, DBException;

    /**
     * Método que busca e retorna um post baseado no seu dono (uma instância da classe perfil)
     * @param owner uma instância da classe Perfil, que representa o dono do post
     * @return Uma lista de posts pertencentes ao dono, se existirem posts
     * @throws PostNotFoundException caso não exista nenhum post assossiado a esse dono
     * @throws DBException 
     */
    List<Post> findPostByOwner(Profile owner) throws PostNotFoundException, DBException;

    /**
     * Método que busca e retorna um post baseado em uma hashtag presente
     * @param hashtag a string que representa a hashtag a ser buscada
     * @return Uma lista de posts que utilizam a hashtag fornecida, se existirem
     * @throws PostNotFoundException caso não exista nenhum post assossiado a essa hashtag
     * @throws DBException 
     */
    List<Post> findPostByHashtag(String hashtag) throws PostNotFoundException, DBException;


    /**
     * Método que busca e retorna um post baseado em um trecho do texto do post
     * @param searchTerm a string que representa o trecho a buscado
     * @return Uma lista de posts que utiliza do trecho fornecido, se existirem
     * @throws PostNotFoundException caso não exista nenhum post assossiado a esse trecho
     * @throws DBException 
     */
    List<Post> findPostByPhrase(String searchTerm) throws PostNotFoundException, DBException;

    /**
     * Deleta um post fornecido do repositório de posts
     * @param idPost o id do post a ser deletado
     * @throws PostNotFoundException caso não exista nenhum post associado a esse id
     * @throws DBException 
     */
    void deletePost(int idPost) throws PostNotFoundException, DBException;

    public void removePostsFromUser(Profile profile) throws DBException;


    
}