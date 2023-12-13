package TextRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.Profile;
import Repositories.IProfileRepository;
import Utils.IOUtils;

public class ProfileRepository implements IProfileRepository {
    private List<Profile> profiles = new ArrayList<Profile>();

    public ProfileRepository() {
        loadProfilesFromFile("src/data/profiles.txt");
    }

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
        .filter(profile -> profile.getName().equals(name))
        .findFirst();
        return profileFound.orElseThrow(() -> new ProfileNotFoundException("A profile with this name doesn't exists!"));
    }

    @Override
    public Profile findProfileByEmail(String email) throws ProfileNotFoundException {
        Optional<Profile> profileFound = profiles.stream()
        .filter(profile -> profile.getEmail().equals(email))
        .findFirst();
        return profileFound.orElseThrow(() -> new ProfileNotFoundException("A profile with this name doesn't exists!"));


    }

    @Override
    public void addProfile(Profile profile) throws ProfileAlreadyExistsException {
        boolean exists = profiles.stream().anyMatch(pr -> pr.getId().equals(profile.getId()) || pr.getName().equals(profile.getName()));
        if(exists) throw new ProfileAlreadyExistsException("A profile with this name or id already exists!");
        profiles.add(profile);
    }

    public void loadProfilesFromFile(String filepath) {
          // Formato em que os dados são lidos para cada perfil: id;name;email
        List<String> lines = IOUtils.readLinesOnFile(filepath);
        Stream<String> linesStream = lines.stream();
        linesStream.forEach(line -> {
            String[] data = line.split(";");
            try {
                addProfile(new Profile(Integer.parseInt(data[0]), data[1], data[2]));
                
            // Alguns prints são feitos mas são feitos para mapear o erro, não necessariamente o usuário precisa saber
            } catch (ProfileAlreadyExistsException e) {
                System.err.println("DB ERROR: Conflict with existing user in memory and in file");
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println(filepath + " : invalid data in file");
            }
        });

    }

    

}
