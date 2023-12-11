package SQLRepositories;

import java.util.List;

import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.Profile;
import Repositories.IProfileRepository;

public class SQLProfileRepository implements IProfileRepository {

    @Override
    public List<Profile> getAllProfiles() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllProfiles'");
    }

    @Override
    public Integer getProfileAmount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProfileAmount'");
    }

    @Override
    public void removeProfile(Integer profileId) throws ProfileNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeProfile'");
    }

    @Override
    public Profile findProfileById(Integer id) throws ProfileNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findProfileById'");
    }

    @Override
    public Profile findProfileByName(String name) throws ProfileNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findProfileByName'");
    }

    @Override
    public Profile findProfileByEmail(String email) throws ProfileNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findProfileByEmail'");
    }

    @Override
    public void addProfile(Profile profile) throws ProfileAlreadyExistsException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addProfile'");
    }
    
}
