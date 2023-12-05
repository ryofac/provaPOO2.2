import Repositories.PostRepository;
import Repositories.ProfileRepository;

public class Main {
     public static void main(String[] args) {
        SocialNetwork socialNetwork = new SocialNetwork(new ProfileRepository(), new PostRepository());
        App app = new App(socialNetwork);
        app.run();
    }
    
}
