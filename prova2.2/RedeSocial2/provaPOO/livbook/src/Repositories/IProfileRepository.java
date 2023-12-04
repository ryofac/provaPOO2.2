package Repositories;

import java.util.List;

import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.Profile;

public interface IProfileRepository {

     /**
     * Esse método é utilizado para retornar todos os perfis armazenados
     * @return todos os perfis criados
     */
    List<Profile> getAllProfiles();

    /**
     * Esse método é um atalho para o método getAllProfiles().size()
     * @return a quantidade total de perfis armazenados
     */
    Integer getProfileAmount();

    /**
     * Método que remove um perfil com determinado id
     * @param id o id do perfil a ser buscado
     * @throws ProfileNotFoundException caso o perfil com o id fornecido não for encontrado
     */
    void removeProfile(Integer profileId) throws ProfileNotFoundException;


    /**
     * Método que busca e retorna um perfil baseado no id fornecido
     * @param id o id do perfil a ser buscado
     * @return o perfil buscado, se encontrado
     * @throws ProfileNotFoundException caso o perfil não exista
     */
    Profile findProfileById(Integer id) throws ProfileNotFoundException;

    /**
     * Método que busca e retorna um perfil baseado na string que representa o nome do perfil fornecido
     * @param name o nome do perfil a ser buscado 
     * @return o perfil buscado, se encontrado
     * @throws ProfileNotFoundException caso o perfil não exista
     */
    Profile findProfileByName(String name) throws ProfileNotFoundException;

    /**
     * Método que busca e retorna um perfil baseado na string que representa o nome do perfil fornecido
     * @param email o nome do perfil a ser buscado 
     * @return o perfil buscado, se encontrado
     * @throws ProfileNotFoundException caso o perfil não exista
     */
    Profile findProfileByEmail(String email) throws ProfileNotFoundException;

    /**
     * Método que adciona uma instância de Perfil no repositório de perfis
     * @param profile perfil que se deseja adcionar
     * @throws ProfileAlreadyExistsException caso o perfil já exista no repositório de perfis
     */
    void addProfile(Profile profile) throws ProfileAlreadyExistsException;

}