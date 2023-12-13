import Repositories.IPostRepository;
import Repositories.IProfileRepository;
import SQLRepositories.DBCom;
import SQLRepositories.SQLPostRepository;
import SQLRepositories.SQLProfileRepository;
import TextRepositories.PostRepository;
import TextRepositories.ProfileRepository;
// Classe auxiliar para chamar o aplicativo
public class Main {
     public static void main(String[] args) {
        // Classe que faz a conexão com o banco de dados
        DBCom DBCom = new DBCom();

        // Repositórios que trabalham com o banco de dados
        IProfileRepository profileRepository = new SQLProfileRepository(DBCom);
        IPostRepository postRepository = new SQLPostRepository(DBCom, profileRepository);

        // Repositórios que só trabalham com os arrays
        IProfileRepository profileRepositoryList = new ProfileRepository();
        IPostRepository postRepositoryList = new PostRepository(profileRepositoryList);
        
        SocialNetwork socialNetwork = new SocialNetwork(profileRepository, postRepository);
        App app = new App(socialNetwork);
        app.run();
    }
    
}
