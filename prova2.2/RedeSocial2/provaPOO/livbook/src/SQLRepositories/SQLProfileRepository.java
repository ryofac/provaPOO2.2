package SQLRepositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Exceptions.DBException.DBException;
import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.Profile;
import Repositories.IProfileRepository;

public class SQLProfileRepository implements IProfileRepository {
    DBCom com;

    public SQLProfileRepository(DBCom com) {
        this.com = com;

    }


    @Override
    public List<Profile> getAllProfiles() throws DBException {
        try {
            com.connect();
            List<Profile> allProfiles = new ArrayList<>();
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT * FROM PROFILE"
            );
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                allProfiles.add(new Profile(
                    rs.getInt("ID"),
                    rs.getString("NAME"),
                    rs.getString("EMAIL")
                ));
            }
            return allProfiles;
        } catch (SQLException e) {
            throw new DBException("ERROR while getting all profiles", e);
        } finally {
            com.close();
        }
    }

    @Override
    public Integer getProfileAmount() throws DBException {
        try{
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT COUNT(*) FROM PROFILE"
            );
            ResultSet rs = psmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DBException("ERROR while getting profile amount", e);
        } finally {
            com.close();
        }
    }

    @Override
    public void removeProfile(Integer profileId) throws ProfileNotFoundException, DBException {
        try{
            com.connect();
            PreparedStatement psmt;
            // Deletando o perfil
            psmt = com.con.prepareStatement(
                "DELETE FROM PROFILE WHERE ID = ?"
            );
            psmt.setInt(1, profileId);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("ERROR while removing profile", e);
        } finally {
            com.close();
        }
    }

    @Override
    public Profile findProfileById(Integer id) throws ProfileNotFoundException, DBException {
       try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT * FROM PROFILE WHERE ID = ?"
            );
            psmt.setInt(1, id);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return new Profile(
                    rs.getInt("ID"),
                    rs.getString("NAME"),
                    rs.getString("EMAIL")
                );
            } else {
                throw new ProfileNotFoundException("Profile with id " + id + " not found");
            }
        } catch (SQLException e) {
            throw new DBException("ERROR while finding profile by id", e);
        } finally {
            com.close();
       }
    }

    @Override
    public Profile findProfileByName(String name) throws ProfileNotFoundException, DBException {
       try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT * FROM PROFILE WHERE NAME = ?"
            );
            psmt.setString(1, name);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return new Profile(
                    rs.getInt("ID"),
                    rs.getString("NAME"),
                    rs.getString("EMAIL")
                );
            } else {
                throw new ProfileNotFoundException("Profile with name " + name + " not found");
            }
       } catch(SQLException e){
            throw new DBException("ERROR while finding profile by name", e);
        } finally {
            com.close();
       }
    }

    @Override
    public Profile findProfileByEmail(String email) throws ProfileNotFoundException, DBException {
        try {
            com.connect();
            PreparedStatement psmt = com.con.prepareStatement(
                "SELECT * FROM PROFILE WHERE EMAIL = ?"
            );
            psmt.setString(1, email);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return new Profile(
                    rs.getInt("ID"),
                    rs.getString("NAME"),
                    rs.getString("EMAIL")
                );
            } else {
                throw new ProfileNotFoundException("Profile with email " + email + " not found");
            }
        } catch (SQLException e) {
            throw new DBException("ERROR while finding profile by email", e);
        } finally {
            com.close();
        }
    }

    @Override
    public void addProfile(Profile profile) throws ProfileAlreadyExistsException, DBException {
        try {
            findProfileByName(profile.getName());
            findProfileByEmail(profile.getEmail());
            throw new ProfileAlreadyExistsException("Profile already exists");
        } catch (ProfileNotFoundException e) {
            try{
            PreparedStatement psmt = com.con.prepareStatement(
                "INSERT INTO PROFILE (ID, NAME, EMAIL) VALUES (?, ?, ?)"
            );
            psmt.setInt(1, profile.getId());
            psmt.setString(2, profile.getName());
            psmt.setString(3, profile.getEmail());
            psmt.executeUpdate();
            } catch (SQLException ex) {
                throw new DBException("ERROR while adding profile" + ex.getNextException(), ex);
            }
        } finally {
            com.close();
        }
    }
    
}
