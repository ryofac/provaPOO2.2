import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import Exceptions.PostException.PostNotFoundException;
import Exceptions.ProfileException.ProfileAlreadyExistsException;
import Exceptions.ProfileException.ProfileNotFoundException;
import Models.AdvancedPost;
import Models.Post;
import Models.Profile;
import Utils.ConsoleColors;
import Utils.IOUtils;

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

    private final String PROFILE_PATH = "/home/ryan-dev/Works/Faculdade/Second Period/ads-poo/prova/provaPOO/livbook/src/data/profiles.txt";
    private final String POST_PATH = "/home/ryan-dev/Works/Faculdade/Second Period/ads-poo/prova/provaPOO/livbook/src/data/posts.txt";

    // This class is used for menu options
    private class Option {
        String title; // Nome que será mostrado
        Runnable callback; // função que será chamada pela opcao

        Option(String title, Runnable callback) {
            this.title = title;
            this.callback = callback;
        }

        @Override
        public String toString() {
            return title;
        }

    }

    private Option[] options = {
            new Option("Create profile", this::includeProfile),
            new Option("Create Post", this::createPost),
            new Option("Show Feed", this::showAllPosts),
            new Option("Show users", this::showAllProfiles),
            new Option("Search Profile", this::searchProfile),
            new Option("Search Post", this::searchPost),
            new Option("Like Post", this::likePost),
            new Option("Dislike Post", this::dislikePost),
            new Option("Delete Post", this::deletePost),
            new Option("Delete Profile", this::removeProfile),
            new Option("Show Popular Advanced Posts", this::showPopularAPosts),
        };

    private void showMenu(Option... options) {
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
            System.out.println("Post not founded!");
        }
    }

    private void includeProfile() {
        try {
            String name = IOUtils.getText("Enter the profile username: ").trim();
            String email = IOUtils.getTextNormalized("Enter profile email: ").trim();
            socialNetwork.includeProfile(socialNetwork.createProfile(name, email));
            System.out.println("User created!");
        } catch (ProfileAlreadyExistsException e) {
            System.out.println(ConsoleColors.RED + "CANNOT CREATE USER: " + e.getMessage() + ConsoleColors.RESET);
            return;
        } catch (Exception e) {
            System.out.println("Ocorreu um erro...");
            e.printStackTrace();
        }
    }

    private void searchProfile() {
        String searchTerm = IOUtils.getTextNormalized("Enter the search term : [email/username] \n> ");
        try {
            Profile foundedbyEmail = socialNetwork.findProfileByEmail(searchTerm);
            System.out.println("Founded: " + foundedbyEmail);
        } catch (ProfileNotFoundException e) {
            try {
                Profile foundedbyUsername = socialNetwork.findProfileByName(searchTerm);
                System.out.println("Founded: " + foundedbyUsername);
            } catch (ProfileNotFoundException err) {
                System.out.println(ConsoleColors.RED + "User not founded!" + ConsoleColors.RESET);
            }

        }

    }

    private void removeProfile() {
        showAllProfiles();
        System.out.println(
                ConsoleColors.RED + "The posts related to that person will be removed too!" + ConsoleColors.RESET);
        Integer id = IOUtils.getInt("Enter the id: ");
        removeProfile(id);
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
            Profile foundedByEmail = socialNetwork.findProfileByEmail(email);
            Profile foundedByName = socialNetwork.findProfileByName(name);
            if (foundedByEmail != foundedByName) {
                System.out.println(ConsoleColors.RED + "Autentication failed!" + ConsoleColors.RESET);
                return;
            }
            String text = IOUtils.getText("What do you want to share with world today? >_<\n > ").replace(";", "*");

            List<String> hashtagsFounded = findHashtagInText(text);
            if (hashtagsFounded.size() > 0)
                System.out.println(ConsoleColors.YELLOW
                        + "Warn: you can only embed hashtags in a advanced post" + ConsoleColors.RESET);
            Boolean isAdvanced = IOUtils.getChoice("Do you want to turn this into a advanced post? ");
            Post created;
            if (isAdvanced) {
                Integer remainingViews = IOUtils.getInt("Set the max views: ");
                created = socialNetwork.createAdvancedPost(text, foundedByEmail, remainingViews);
            } else {
                created = socialNetwork.createPost(text, foundedByEmail);

            }
            // hashtags vão ser adcionadas a medida que são encontradas no próprio texto
            for (String hashtag : hashtagsFounded) {
                if (created instanceof AdvancedPost) {
                    ((AdvancedPost) created).addHashtag(hashtag);
                } else {
                    text = text.replace(hashtag, " ");
                    System.out.println(ConsoleColors.RED + "Hashtag " + hashtag
                            + " removed: you have to create an advanced post" + ConsoleColors.RESET);
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
            Profile userFoundedByName = socialNetwork.findProfileByName(searchTerm);
            socialNetwork.showPostsPerProfile(userFoundedByName);
        } catch (ProfileNotFoundException e) {
            System.out.println("No posts founded by user");
        }
        try {
            socialNetwork.showPostsPerText(searchTerm);
        } catch (ProfileNotFoundException e) {
            System.out.println("No posts founded by text");
        }
        try {
            socialNetwork.showPostsPerHashtag(searchTerm);
        } catch (ProfileNotFoundException err) {
            System.out.println("No posts founded by hashtag");
        }
    }

    private void likePost() {
        showAllPosts();
        Integer idPost = IOUtils.getInt("Enter the post id: ");
        try {
            socialNetwork.likePost(idPost);
            Post founded = socialNetwork.findPostsbyId(idPost);
            System.out.println("Post from " + founded.getOwner().getName() + "liked!");
        } catch (ProfileNotFoundException e) {
            System.out.println("Post not founded!");
        }
    }

    private void dislikePost() {
        Integer idPost = IOUtils.getInt("Enter the post id: ");
        try {
            socialNetwork.dislikePost(idPost);
            Post founded = socialNetwork.findPostsbyId(idPost);
            System.out.println("Post disliked!");
            System.out.println("Post from " + founded.getOwner().getName() + "disliked!");
        } catch (ProfileNotFoundException e) {
            System.out.println("Post not founded!");
        }
    }

    public void run() {
        Integer chosen;
        socialNetwork.readData(PROFILE_PATH, POST_PATH);
        while (true) {
            showMenu(options);
            // Controla a opção escolhida atual: entrada de dados do programa
            try {
                chosen = IOUtils.getInt("Enter a option: \n> ");

                // verifica se é maior ou menor que o número de conteúdos da lista, senão for,
                // continua...
                if (chosen > options.length || chosen < 0) {
                    System.out.println("Please, digit a valid option number!");
                    continue;
                }
                if (chosen == 0) { // Opção sair: termina o loop
                    break;
                }
                // Escolhe a opção pelo que foi digitado - 1 (o indice real do array)
                options[chosen - 1].callback.accept(null);
                socialNetwork.saveData(PROFILE_PATH, POST_PATH);
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "Enter only numbers, please!" + ConsoleColors.RESET);
            }

            IOUtils.clearScreen();

        }

        IOUtils.closeScanner(); // TODO: Achar um método mais "bonito" de fazer isso"

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
        // TODO: Método getAdvancedPosts in SocialNetwork
        for (Post post : socialNetwork.getAllPosts()) {
            if (post instanceof AdvancedPost && post.isPopular()) {
                System.out.println(formatPost(post));
            }
        }
        viewPosts();
    }

    public void showAllProfiles() {
        // TODO: Método getAllProfiles in SocialNetwork
        System.out.println("================ PROFILES ===============");
        for (Profile profile : socialNetwork.getAllProfiles()) {
            System.out.println(formatProfile(profile));
        }
    }

    public void showPostsPerProfile(Profile owner) throws ProfileNotFoundException {
        List<Post> postsFounded = socialNetwork.findPostsbyOwner(owner);
        if (postsFounded.size() == 0) {
            throw new ProfileNotFoundException("Posts with this owner does not exist");
        }
        System.out.println("==== Founded by Profile: ====");
        for (Post actualPost : postsFounded) {
            System.out.println(formatPost(actualPost));
        }
    }

    public void showPostsPerHashtag(String hashtag) throws ProfileNotFoundException {
        List<Post> postsFounded = postRepository.findPostByHashtag(hashtag);
        if (postsFounded.size() == 0) {
            throw new ProfileNotFoundException("Posts with this hashtag does not exist");
        }
        System.out.println("==== Founded by hashtag: ====");
        for (Post post : postsFounded) {
            System.out.println(formatPost(post));
        }
    }

    public void showPostsPerText(String text) throws ProfileNotFoundException {
        List<Post> postsFoundedByText = findPostByPhrase(text);
        System.out.println("===== Founded by text: =====");
        for (Post post : postsFoundedByText) {
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

    public void showPopularHashtags() {
        List<String> hashtags = socialNetwork.get();
        for (String hashtag : hashtags) {
            System.out.println(hashtag);
        }
    }

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
                System.out.println("ERROR: user founded in file not related to any post");
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
