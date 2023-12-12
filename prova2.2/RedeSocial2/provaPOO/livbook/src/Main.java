import Repositories.IPostRepository;
import Repositories.IProfileRepository;
import SQLRepositories.DBCom;
import SQLRepositories.SQLPostRepository;
import SQLRepositories.SQLProfileRepository;
// Classe auxiliar para chamar o aplicativo
public class Main {
     public static void main(String[] args) {
        DBCom DBCom = new DBCom();
        IProfileRepository profileRepository = new SQLProfileRepository(DBCom);
        IPostRepository postRepository = new SQLPostRepository(DBCom, profileRepository);
        SocialNetwork socialNetwork = new SocialNetwork(profileRepository, postRepository);
        App app = new App(socialNetwork);
        app.run();
    }
    
}
