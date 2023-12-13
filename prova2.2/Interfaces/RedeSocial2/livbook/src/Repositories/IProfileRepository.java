package Repositories;

import java.util.List;

import Exceptions.DBException.DBException;
import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.Profile;

public interface IProfileRepository {

     /**
     * Esse método é utilizado para retornar todos os perfis armazenados
     * @return todos os perfis criados
     * @throws DBException 
     */
    List<Profile> getAllProfiles() throws DBException;

    /**
     * Esse método é um atalho para o método getAllProfiles().size()
     * @return a quantidade total de perfis armazenados
     * @throws DBException 
     */
    Integer getProfileAmount() throws DBException;

    /**
     * Método que remove um perfil com determinado id
     * @param id o id do perfil a ser buscado
     * @throws ProfileNotFoundException caso o perfil com o id fornecido não for encontrado
     * @throws DBException 
     */
    void removeProfile(Integer profileId) throws ProfileNotFoundException, DBException;


    /**
     * Método que busca e retorna um perfil baseado no id fornecido
     * @param id o id do perfil a ser buscado
     * @return o perfil buscado, se encontrado
     * @throws ProfileNotFoundException caso o perfil não exista
     * @throws DBException 
     */
    Profile findProfileById(Integer id) throws ProfileNotFoundException, DBException;

    /**
     * Método que busca e retorna um perfil baseado na string que representa o nome do perfil fornecido
     * @param name o nome do perfil a ser buscado 
     * @return o perfil buscado, se encontrado
     * @throws ProfileNotFoundException caso o perfil não exista
     * @throws DBException 
     */
    Profile findProfileByName(String name) throws ProfileNotFoundException, DBException;

    /**
     * Método que busca e retorna um perfil baseado na string que representa o nome do perfil fornecido
     * @param email o nome do perfil a ser buscado 
     * @return o perfil buscado, se encontrado
     * @throws ProfileNotFoundException caso o perfil não exista
     * @throws DBException 
     */
    Profile findProfileByEmail(String email) throws ProfileNotFoundException, DBException;

    /**
     * Método que adciona uma instância de Perfil no repositório de perfis
     * @param profile perfil que se deseja adcionar
     * @throws ProfileAlreadyExistsException caso o perfil já exista no repositório de perfis
     * @throws DBException 
     */
    void addProfile(Profile profile) throws ProfileAlreadyExistsException, DBException;

}