package TextRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.Profile;
import Repositories.IProfileRepository;

public class ProfileRepository implements IProfileRepository {
    private List<Profile> profiles = new ArrayList<Profile>();

    @Override
    public List<Profile> getAllProfiles() {
        return profiles;
    }

    @Override
    public Integer getProfileAmount() {
        return profiles.size();
    }

    @Override
    public void removeProfile(Integer profileId) throws ProfileNotFoundException {
        Profile found = findProfileById(profileId);
        profiles.remove(found);

    }

    @Override
    public Profile findProfileById(Integer id) throws ProfileNotFoundException {
        Optional<Profile> profileFound = profiles
        .stream()
        .filter(profile -> profile.getId() == id)
        .findFirst();

        return profileFound.orElseThrow(() -> new ProfileNotFoundException("A profile with this id doesn't exists!"));

    }

    @Override
    public Profile findProfileByName(String name) throws ProfileNotFoundException {
        Optional<Profile> profileFound = profiles.stream()
        .filter(profile -> profile.getName() == name)
        .findFirst();
        return profileFound.orElseThrow(() -> new ProfileNotFoundException("A profile with this name doesn't exists!"));
    }

    @Override
    public Profile findProfileByEmail(String email) throws ProfileNotFoundException {
        Optional<Profile> profileFound = profiles.stream()
        .filter(profile -> profile.getEmail() == email)
        .findFirst();
        return profileFound.orElseThrow(() -> new ProfileNotFoundException("A profile with this name doesn't exists!"));


    }

    @Override
    public void addProfile(Profile profile) throws ProfileAlreadyExistsException {
        boolean exists = profiles.stream().anyMatch(pr -> pr.getId() == profile.getId() || pr.getName() == profile.getName());
        if(exists) throw new ProfileAlreadyExistsException("A profile with this name or id already exists!");
        profiles.add(profile);
    }

}
