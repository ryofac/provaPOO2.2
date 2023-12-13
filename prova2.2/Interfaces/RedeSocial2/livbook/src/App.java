import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import Exceptions.DBException.DBException;
import Exceptions.PostException.PostNotFoundException;
import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.AdvancedPost;
import Models.Post;
import Models.Profile;
import Utils.ConsoleColors;
import Utils.IOUtils;

// TODO List em ordem de prioridade:
// DESCOBRIR como lidar com as exceptions do banco de dados de uma forma melhor

public class App {

    Stack<Runnable> viewStack = new Stack<>();
    
    private SocialNetwork socialNetwork;

    public App(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    private final String MENU_TITLE = """
             __       __  ____    ____ .______     ______     ______    __  ___
            |  |     |  | \\   \\  /   / |   _  \\   /  __  \\   /  __  \\  |  |/  /
            |  |     |  |  \\   \\/   /  |  |_)  | |  |  |  | |  |  |  | |  '  /
            |  |     |  |   \\      /   |   _  <  |  |  |  | |  |  |  | |    <
            |  `----.|  |    \\    /    |  |_)  | |  `--'  | |  `--'  | |  .  \\
            |_______||__|     \\__/     |______/   \\______/   \\______/  |__|\\__\\

            """;

    
    private final String PROFILE_PATH = System.getProperty("user.dir") + "/src/data/profiles.txt";
    private final String POST_PATH = System.getProperty("user.dir") + "/src/data/posts.txt";

   /**
    * Classe utilizada para encapsular a lógica de uma opção do menu
    */
    private class Option {
        String title;
        Runnable callback;
        Supplier<Boolean> canShow;
        
        /**
         * @param title nome que que representará a opção
         * @param callback função que será executada ao chamar a opção
         * @param canShow função booleana que controla quando essa opção poderá ser exibida
         */
        Option(String title, Runnable callback, Supplier<Boolean> canShow) {
            this.title = title;
            this.callback = callback;
            this.canShow = canShow;
        }

        @Override
        public String toString() {
            return title;
        }

    }

    // A criação de um "menu lógico", isto é, um array de opções
    private List<Option> options = List.of(
            new Option("Create profile", this::includeProfile, () -> true ),
            new Option("Create Post", this::createPost, () -> socialNetwork.existsProfiles()),
            new Option("Show users", this::showAllProfiles, () -> socialNetwork.existsProfiles() ),
            new Option("Search Profile", this::searchProfile, () -> socialNetwork.existsProfiles() ),
            new Option("Search Post", this::searchPost, () -> socialNetwork.existsPosts() ),
            new Option("Show Feed", this::showAllPosts, () -> socialNetwork.existsPosts() ),
            new Option("Like Post", this::likePost, () -> socialNetwork.existsPosts() ),
            new Option("Dislike Post", this::dislikePost, () -> socialNetwork.existsPosts() ),
            new Option("Delete Post", this::deletePost, () -> socialNetwork.existsPosts() ),
            new Option("Delete Profile", this::removeProfile, () -> socialNetwork.existsProfiles() ),
            new Option("Show Popular Advanced Posts", this::showPopularAPosts, () -> socialNetwork.existsPosts())
    );

    // ----------------- Funções de implementação da camada de negócios --------------------
    
    private void deletePost() {
        showAllPosts();
        Integer idPost = IOUtils.getInt("Enter the post id: ");
        try {
            socialNetwork.deletePost(idPost);
            System.out.println("Post deleted!");
        } catch (PostNotFoundException e) {
            System.out.println("Post not found!");
        } catch (DBException e){
            System.out.println("DB ERROR: " + e.getMessage());
        }
    }

    private void includeProfile() {
        try {
            String name = IOUtils.getTextNormalized("Enter the profile username: ");
            String email = IOUtils.getTextNormalized("Enter profile email: ");
            socialNetwork.includeProfile(socialNetwork.createProfile(name, email));
            System.out.println("User created!");
        } catch (ProfileAlreadyExistsException e) {
            IOUtils.showErr( "CANNOT CREATE USER: " + e.getMessage());
            return;
        } catch (DBException e){
            IOUtils.showErr("DB ERROR: " + e.getMessage());
            return;
        }
    }
    

    private void searchProfile() {
        String searchTerm = IOUtils.getTextNormalized("Enter the search term : [email/username] \n> ");
        try {
            Profile foundbyEmail = socialNetwork.findProfileByEmail(searchTerm);
            System.out.println("Found email:  \n" + formatProfile(foundbyEmail));
        } catch (ProfileNotFoundException e) {
            try {
                Profile foundbyUsername = socialNetwork.findProfileByName(searchTerm);
                System.out.println("Found username: \n" + formatProfile(foundbyUsername));
            } catch (ProfileNotFoundException err) {
                IOUtils.showErr("User not found!");
            } catch (DBException ex){
                IOUtils.showErr("DB ERROR: " + ex.getMessage());
            }

        } catch (DBException e) {
            IOUtils.showErr("DB ERROR: " + e.getMessage());
        }

    }

    private void removeProfile() {
        showAllProfiles();
        IOUtils.showWarn("The posts related to that person will be removed too!");
        Integer id = IOUtils.getInt("Enter the id: ");
        try{
            socialNetwork.removeProfile(id);
            System.out.println("Profile removed!");
        } catch(ProfileNotFoundException e){
            System.out.println("A profile with this id doesn't exist!");
        } catch (DBException e){
            System.out.println("DB ERROR: " + e.getMessage());
            e.printStackTrace();
        }
       
    }


    // Função acessória para achar hashtags diretamente o texto
    private List<String> findHashtagInText(String text) {
        List<String> result = new ArrayList<>();
        boolean coletting = false;
        String actualHashtag = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '#' || coletting) {
                actualHashtag += text.charAt(i);
                coletting = true;
                if (text.charAt(i) == ' ' || text.charAt(i) == '\n' || i + 1 == text.length()) {
                    result.add(actualHashtag);
                    coletting = false;
                    actualHashtag = "";
                }
            }
        }
        return result;

    }

    private void createPost() {
        System.out.println("Autenticate...");
        String name = IOUtils.getTextNormalized("Enter your username: ");
        String email = IOUtils.getTextNormalized("Enter your email: ");
        try {
            Profile foundByEmail = socialNetwork.findProfileByEmail(email);
            Profile foundByName = socialNetwork.findProfileByName(name);

            // Se os nomes e email forem digitados de forma diferente, a autenticação falha
            if (! (foundByEmail.getName().equals(foundByName.getName()) && 
                foundByEmail.getEmail().equals(foundByEmail.getEmail()))) {
                IOUtils.showErr("Autentication failed!");
                return;
            }
            
            String text = IOUtils.getText("What do you want to share with world today? >_<\n > ").replace(";", "*");

            List<String> hashtagsFound = findHashtagInText(text);
            if (hashtagsFound.size() > 0)
                IOUtils.showWarn("Warning: you can only embed hashtags in a advanced post \\o/");
            Boolean isAdvanced = IOUtils.getChoice("Do you want to turn this into a advanced post? ");
            Post created;
            if (isAdvanced) {
                Integer remainingViews = IOUtils.getInt("Set the max views: ");
                created = socialNetwork.createAdvancedPost(text, foundByEmail, remainingViews);
            } else {
                created = socialNetwork.createPost(text, foundByEmail);

            }
            // hashtags vão ser adcionadas a medida que são encontradas no próprio texto
            for (String hashtag : hashtagsFound) {
                if (created instanceof AdvancedPost) {
                    ((AdvancedPost) created).addHashtag(hashtag);
                } else {
                    text = text.replace(hashtag, " ");
                    IOUtils.showWarn("Hashtag " + hashtag + " removed: you have to create an advanced post");
                }

            }
            created.setText(text);
            socialNetwork.includePost(created);
            System.out.println("Post added to feed!");
        } catch (ProfileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DBException e) {
            System.out.println("DB ERROR: " + e.getMessage());
        }
    }

    private void searchPost() {
        String searchTerm = IOUtils
                .getTextNormalized("Enter the search parameter: [profile username/phrase/hashtag]\n > ");
        try {
            Profile userFoundByName = socialNetwork.findProfileByName(searchTerm);
            showPostsPerProfile(userFoundByName);
        } catch (ProfileNotFoundException e) {
            System.out.println("Profile not found");
        } catch(PostNotFoundException e){
            System.out.println("No posts found by this user");
        } catch (DBException e) {
            System.out.println("DB ERROR: " + e.getMessage());
        }
        try {
            showPostsPerText(searchTerm);
        } catch (PostNotFoundException e) {
            System.out.println("No posts found by text");
        }
        try {
            showPostsPerHashtag(searchTerm);
        } catch (PostNotFoundException err) {
            System.out.println("No posts found by hashtag");
        } 
    }

    private void likePost() {
        showAllPosts();
        Integer idPost = IOUtils.getInt("Enter the post id: ");
        try {
            socialNetwork.likePost(idPost);
            Post found = socialNetwork.findPostsbyId(idPost);
            System.out.println("Post from " + found.getOwner().getName() + "liked!");
        } catch (PostNotFoundException e) {
            System.out.println("Post not found!");
        } catch (DBException e) {
            System.out.println("DB ERROR: " + e.getMessage());
        }
    }

    private void dislikePost() {
        Integer idPost = IOUtils.getInt("Enter the post id: ");
        try {
            socialNetwork.dislikePost(idPost);
            Post found = socialNetwork.findPostsbyId(idPost);
            System.out.println("Post disliked!");
            System.out.println("Post from " + found.getOwner().getName() + "disliked!");
        } catch (PostNotFoundException e) {
            System.out.println("Post not found!");
        } catch (DBException e){
            System.out.println("DB ERROR: " + e.getMessage());
        }
    }

    // ----------------- Funções de vizualização --------------------
    // Usado para formatar os posts no formato adequado
    public String formatPost(Post post) {
        String postText = post.getText();
        if (post instanceof AdvancedPost) {
            List<String> hashtags = ((AdvancedPost) post).getHashtags();
            for (String hashtag : hashtags) {
                postText = postText.replace(hashtag.trim(), ConsoleColors.BLUE_BRIGHT + hashtag + ConsoleColors.RESET);

            }
        }
        String formated = String.format("""
                -----------------------------
                <ID - %d> %s - at %s
                %s
                ------------------------------
                %d - likes %d - dislikes
                """,
                post.getId(), post.getOwner().getName(),
                post.getCreatedTime().format(DateTimeFormatter.ofPattern("dd/MM (EE): HH:mm")),
                postText, post.getLikes(), post.getDislikes());
        if (post.isPopular()) {
            formated += ConsoleColors.YELLOW_BRIGHT + "\t< ✦POPULAR✦ >\n" + ConsoleColors.RESET;
        }
        if (post instanceof AdvancedPost) {
            formated += String.format("(%d - views remaining)\n hashtags:", ((AdvancedPost) post).getRemainingViews());
            for (String hashtag : ((AdvancedPost) post).getHashtags()) {
                formated += " " + hashtag;
            }
        }
        formated += "\n"; // mais espaço no fim
        return formated;
    }

    public void showPopularAPosts() {
        try {
            System.out.println("====== Popular Advanced Posts: =======");
            for (Post post : socialNetwork.getAllPosts()) {
                if (post instanceof AdvancedPost && post.isPopular()) {
                    System.out.println(formatPost(post));
                }
        }
        viewPosts();
        } catch (DBException e){
            System.out.println("DB ERROR: " + e.getMessage());
        }
        
    }

    public void showAllProfiles() {
        try{
            System.out.println("================ PROFILES ===============");
            for (Profile profile : socialNetwork.getAllProfiles()) {
                System.out.println(formatProfile(profile));
            }
        } catch (DBException e){
            System.out.println("DB ERROR: " + e.getMessage());
        }

        
    }

    public void showPostsPerProfile(Profile owner) throws PostNotFoundException {
        List<Post> postsFound;
        try {
            postsFound = socialNetwork.findPostsbyOwner(owner);
            System.out.println("==== Found by Profile: ====");
            for (Post actualPost : postsFound) {
                System.out.println(formatPost(actualPost));
            }
        } catch (PostNotFoundException e) {
            System.out.println("No posts found by this user");
        } catch (DBException e) {
            System.out.println("DB ERROR: " + e.getMessage());
        }
    }

    public void showPostsPerHashtag(String hashtag) throws PostNotFoundException {
        List<Post> postsFound;
        try {
            postsFound = socialNetwork.findPostByHashtag(hashtag);
        } catch (PostNotFoundException e) {
            System.out.println("Posts not found by this hashtag");
            return;
        } catch (DBException e) {
           System.out.println("DB ERROR: " + e.getMessage());
           return;
        }
        System.out.println("==== Found by hashtag: ====");
        for (Post post : postsFound) {
            System.out.println(formatPost(post));
        }
    }

    public void showPostsPerText(String text) throws PostNotFoundException {
        List<Post> postsFoundByText;
        try {
            postsFoundByText = socialNetwork.findPostByPhrase(text);
        } catch (PostNotFoundException e) {
           System.out.println("Posts not found by this text");
           return;
        } catch (DBException e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return;
        }
        System.out.println("===== Found by text: =====");
        for (Post post : postsFoundByText) {
            System.out.println(formatPost(post));
        }
    }
     private void showAllPosts() {
        try{
            System.out.println("-=-=-=-=-=- FEED =-=-=-=-=-=-= ");
            viewPosts();
            for (Post post : socialNetwork.getAllPosts()) {
                System.out.println(formatPost(post));
            }
            socialNetwork.removeSeenPosts();
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

        } catch (DBException e){
            System.out.println("DB ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
    }

  

    public String formatProfile(Profile profile) {
        String formated = String.format("""
                ===================================
                        <ID: %d> @%s : %s
                ===================================""", profile.getId(), profile.getName(), profile.getEmail());
        return formated;
    }

    public void showPopularPosts() throws DBException {
        for (Post post : socialNetwork.getAllPosts()) {
            if (post.isPopular()) {
                System.out.println(formatPost(post));
            }
        }
    }

    public void viewPosts() throws DBException {
        try{
            for (Post post : socialNetwork.getAllPosts()) {
                if (post instanceof AdvancedPost) {
                    socialNetwork.seePost(post.getId());
                }
        }

        }catch (PostNotFoundException e){
            System.out.println("ERROR: Tried to see a post that doesn't exist");
        }
        

    }

    // ----------------- Funções de persistência em Arquivo --------------------    

    public void writeProfilesinFile(String filepath) {
        try {
            StringBuilder str = new StringBuilder();
            for (Profile profile : socialNetwork.getAllProfiles()) {
                str.append(profile.toString().trim() + "\n");
            }
        IOUtils.writeOnFile(filepath, str.toString());

        } catch (DBException e){
            System.out.println("DB ERROR: " + e.getMessage());
            System.out.println("Data not saved!");
        }
        
    }

    public void writePostsinFile(String filepath) {
        StringBuilder str = new StringBuilder();
        try {
            for (Post post : socialNetwork.getAllPosts()) {
                str.append(post.toString().trim() + "\n");
            }
            IOUtils.writeOnFile(filepath, str.toString());
        } catch (DBException e) {
            System.out.println("DB ERROR: " + e.getMessage());
            System.out.println("Data not saved!");
        }
    }

    public void saveData(String profilePath, String postPath) {
        writeProfilesinFile(profilePath);
        writePostsinFile(postPath);

    }

    /* ======================= Menus ====================== */

    private void showMenu(List<Option> options) {
        String title = MENU_TITLE; 
        Integer optionNumber = 0;
        System.out.println(title);
        for (Option option : options) {
            System.out.println(ConsoleColors.YELLOW_BRIGHT + "+> " + ConsoleColors.GREEN + ++optionNumber + "-" + option
                    + ConsoleColors.RESET);
        }
        System.out.println(String.format(ConsoleColors.RED_BRIGHT + "+> %d - %s", 0, "Exit" + ConsoleColors.RESET));
    }

    private void mainMenu() throws NumberFormatException, NoSuchElementException {
        List<Option> avaliableOptions = options.stream()
                .filter(option -> option.canShow.get()).collect(Collectors.toList());
        showMenu(avaliableOptions);
        int chosen = IOUtils.getInt("Enter a option: \n> ");
        if (chosen > avaliableOptions.size() || chosen < 0) {
            System.out.println("Please, digit a valid option number!");
            return;
        }             
        if (chosen == 0) { // Opção sair: termina o loop
            viewStack.pop();
            return;
        }

        // Escolhe a opção pelo que foi digitado - 1 (o indice real do array)
        avaliableOptions.get(chosen - 1).callback.run();
    }

    
    private void saveMenu(){
        Boolean choice = IOUtils.getChoice("Do you want to save the data to file? ");
            if(choice)
                saveData(PROFILE_PATH, POST_PATH);
        viewStack.pop();
       

    }


    // ----------------- Função principal --------------------
    public void run() {
        // Colocando o menu principal no topo da pilha de visualização
        viewStack.push(this::saveMenu);
        viewStack.push(this::mainMenu);

        // Loop principal do programa
        while (!viewStack.isEmpty()) {
            try{
                viewStack.peek().run();
                IOUtils.clearScreen();
            } catch (NumberFormatException e) {
                IOUtils.showWarn("Enter only numbers, please!");
                IOUtils.clearScreen();
            } catch (NoSuchElementException e){
                IOUtils.showErr("Action canceled by user!");
                viewStack.pop();
            }
           
        }

        System.out.println("See u soon ! >_<" + "\n".repeat(10));
        IOUtils.closeScanner();

    }

}
