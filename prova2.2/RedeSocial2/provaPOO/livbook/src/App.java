import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Exceptions.PostException.PostNotFoundException;
import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.AdvancedPost;
import Models.Post;
import Models.Profile;
import Utils.ConsoleColors;
import Utils.IOUtils;

// TODO List em ordem de prioridade:
// TODO: Modularizar app
// TODO: Adcionar tratamento de exceção mais robusto para leitura/escrita em arquivo
// TODO: Adicionar mecânica de visualização de menus em stack
// TODO: Deixar o caminho para os arquivos de persistência relativo e não absoluto dessa forma 
// TODO: Adcionar um atributo tipo para opções, para decidir quais opções pertencem a um menu pelo tipo delas
// TODO (zinho) : implementar um login (Semelhante ao feito no patRoBank 2.0)

public class App {
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

    
    private final String PROFILE_PATH = "/home/ryofac/Works/prova2.2/RedeSocial2/provaPOO/livbook/src/data/profiles.txt";
    private final String POST_PATH = "/home/ryofac/Works/prova2.2/RedeSocial2/provaPOO/livbook/src/data/posts.txt";

   /**
    * Classe utilizada para encapsular a lógica de uma opção do menu
    */
    private class Option {
        String title;
        Runnable callback;
        Supplier<Boolean> canShow;
        
        /**
         * @param title  nome que que representará a opção
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
            new Option("Delete Profile", this::removeProfile, () -> socialNetwork.existsPosts() ),
            new Option("Show Popular Advanced Posts", this::showPopularAPosts, () -> socialNetwork.existsPosts())
    );

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


    private void deletePost() {
        Integer idPost = IOUtils.getInt("Enter the post id: ");
        try {
            socialNetwork.deletePost(idPost);
            System.out.println("Post deleted!");
        } catch (PostNotFoundException e) {
            System.out.println("Post not found!");
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
        } catch (Exception e) {
            System.out.println("An Unexpected Error Ocurred: ");
            e.printStackTrace();
        }
    }

    private void searchProfile() {
        String searchTerm = IOUtils.getTextNormalized("Enter the search term : [email/username] \n> ");
        try {
            Profile foundbyEmail = socialNetwork.findProfileByEmail(searchTerm);
            System.out.println("Found: " + foundbyEmail);
        } catch (ProfileNotFoundException e) {
            try {
                Profile foundbyUsername = socialNetwork.findProfileByName(searchTerm);
                System.out.println("Found: " + foundbyUsername);
            } catch (ProfileNotFoundException err) {
                IOUtils.showErr("User not found!");
            }

        }

    }

    private void removeProfile() {
        showAllProfiles();
        IOUtils.showWarn("The posts related to that person will be removed too!");
        Integer id = IOUtils.getInt("Enter the id: ");
        try{
            socialNetwork.removeProfile(id);
        } catch(ProfileNotFoundException e){
            System.out.println("A profile with this id doesn't exist!");
        }
        
        System.out.println("Profile removed!");
    }

    private void showAllPosts() {
        System.out.println("-=-=-=-=-=- FEED =-=-=-=-=-=-= ");
       viewPosts();
        for (Post post : socialNetwork.getAllPosts()) {
            System.out.println(formatPost(post));
        }
        socialNetwork.removeSeenPosts();
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
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

            if (foundByEmail != foundByName) {
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
        }
    }

    public void run() {
        Integer chosen;
        List<Option> avaliableOptions;

        // readData(PROFILE_PATH, POST_PATH);
    
        while (true) {
            avaliableOptions = options.stream()
                .filter(option -> option.canShow.get()).collect(Collectors.toList());
            showMenu(avaliableOptions);
            // Controla a opção escolhida atual: entrada de dados do programa
            try {
                chosen = IOUtils.getInt("Enter a option: \n> ");
                if (chosen > avaliableOptions.size() || chosen < 0) {
                    System.out.println("Please, digit a valid option number!");
                    continue;
                }             
                if (chosen == 0) { // Opção sair: termina o loop
                    break;
                }
                // Escolhe a opção pelo que foi digitado - 1 (o indice real do array)
                avaliableOptions.get(chosen - 1).callback.run();
                // saveData(PROFILE_PATH, POST_PATH);

            } catch (NumberFormatException e) {
                IOUtils.showWarn("Enter only numbers, please!");

            IOUtils.clearScreen();

        }
    }
        System.out.println("See u soon ! >_<");
        IOUtils.closeScanner(); 

    }
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
        System.out.println("====== Popular Advanced Posts: =======");
        for (Post post : socialNetwork.getAllPosts()) {
            if (post instanceof AdvancedPost && post.isPopular()) {
                System.out.println(formatPost(post));
            }
        }
        viewPosts();
    }

    public void showAllProfiles() {
        System.out.println("================ PROFILES ===============");
        for (Profile profile : socialNetwork.getAllProfiles()) {
            System.out.println(formatProfile(profile));
        }
    }

    public void showPostsPerProfile(Profile owner) throws PostNotFoundException {
        List<Post> postsFound = socialNetwork.findPostsbyOwner(owner);
        System.out.println("==== Found by Profile: ====");
        for (Post actualPost : postsFound) {
            System.out.println(formatPost(actualPost));
        }
    }

    public void showPostsPerHashtag(String hashtag) throws PostNotFoundException {
        List<Post> postsFound = socialNetwork.findPostByHashtag(hashtag);
        System.out.println("==== Found by hashtag: ====");
        for (Post post : postsFound) {
            System.out.println(formatPost(post));
        }
    }

    public void showPostsPerText(String text) throws PostNotFoundException {
        List<Post> postsFoundByText = socialNetwork.findPostByPhrase(text);
        System.out.println("===== Found by text: =====");
        for (Post post : postsFoundByText) {
            System.out.println(formatPost(post));
        }
    }

  

    public String formatProfile(Profile profile) {
        String formated = String.format("""
                ===================================
                        <ID: %d> @%s : %s
                ===================================""", profile.getId(), profile.getName(), profile.getEmail());
        return formated;
    }

    public void showPopularPosts() {
        for (Post post : socialNetwork.getAllPosts()) {
            if (post.isPopular()) {
                System.out.println(formatPost(post));
            }
        }
    }

    // public void showPopularHashtags() {
    //     List<String> hashtags = socialNetwork.();
    //     for (String hashtag : hashtags) {
    //         System.out.println(hashtag);
    //     }
    // }

    public void viewPosts() {
        for (Post post : socialNetwork.getAllPosts()) {
            if (post instanceof AdvancedPost) {
                ((AdvancedPost) post).decrementViews();
            }
        }

    }


    // Como vem os dados :
    // Post = TIPO;ID;TEXTO;IDODONO;TIME;LIKES;DISLIKES
    // AdvancedPost =
    // TIPO;ID;TEXTO;IDODONO;TIME;LIKES;DISLIKES;REAMAININGVIEWS;HASHTAGS
    public void loadPostsfromFile(String filepath) {
        List<String> lines = IOUtils.readLinesOnFile(filepath);
        Stream<String> linesStream = lines.stream();
        linesStream.forEach(line -> {
            String[] data = line.split(";");
            try {
                switch (data[0]) {
                    case "P":
                        // incluindo o post segundo os dados do arquivo
                        socialNetwork.
                        includePost(
                                new Post(Integer.parseInt(data[1]), data[2],
                                        socialNetwork.findProfileById(Integer.parseInt(data[3])), LocalDateTime.parse(data[4]),
                                        Integer.parseInt(data[5]), Integer.parseInt(data[6])));
                        break;

                    case "AP":
                        // Criando o post a ser adicionado
                        AdvancedPost toBeAdded = new AdvancedPost(Integer.parseInt(data[1]), data[2],
                                socialNetwork.findProfileById(Integer.parseInt(data[3])), Integer.parseInt(data[5]),
                                Integer.parseInt(data[6]), LocalDateTime.parse(data[4]), Integer.parseInt(data[7]));

                        // Pegando só as hashtags do arquivo
                        String[] hashtags = data[8].split("-");

                        // Adcionando as hashtags do arquivo ao perfil
                        for (String hashtag : hashtags) {
                            toBeAdded.addHashtag(hashtag);
                        }
                        socialNetwork.includePost(toBeAdded);
                        break;
                }
            } catch (ProfileNotFoundException e) {
                System.out.println("ERROR: user found in file not related to any post");
                e.printStackTrace();
            }
        });
    }

    // Como vem os dados: id;name;email
    public void loadProfilesFromFile(String filepath) {
        List<String> lines = IOUtils.readLinesOnFile(filepath);
        Stream<String> linesStream = lines.stream();
        linesStream.forEach(line -> {
            String[] data = line.split(";");
            try {
                socialNetwork.includeProfile(new Profile(Integer.parseInt(data[0]), data[1], data[2]));

            } catch (ProfileAlreadyExistsException e) {
                System.err.println("ERROR: Conflict with existing user in memory and in file");
                e.printStackTrace();
            }
        });

    }

    // Persitência de dados em arquivos de texto: 
    public void writeProfilesinFile(String filepath) {
        StringBuilder str = new StringBuilder();
        for (Profile profile : socialNetwork.getAllProfiles()) {
            str.append(profile.toString().trim() + "\n");
        }
        IOUtils.writeOnFile(filepath, str.toString());
    }

    public void writePostsinFile(String filepath) {
        StringBuilder str = new StringBuilder();
        for (Post post : socialNetwork.getAllPosts()) {
            str.append(post.toString().trim() + "\n");
        }
        IOUtils.writeOnFile(filepath, str.toString());
    }

    public void saveData(String profilePath, String postPath) {
        writeProfilesinFile(profilePath);
        writePostsinFile(postPath);

    }

    public void readData(String profilePath, String postPath) {
        loadProfilesFromFile(profilePath);
        loadPostsfromFile(postPath);
    }

}
