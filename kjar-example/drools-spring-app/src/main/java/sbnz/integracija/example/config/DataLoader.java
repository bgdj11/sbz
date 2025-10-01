package sbnz.integracija.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sbnz.integracija.example.entity.Place;
import sbnz.integracija.example.entity.Post;
import sbnz.integracija.example.entity.User;
import sbnz.integracija.example.repository.PlaceRepository;
import sbnz.integracija.example.repository.PostRepository;
import sbnz.integracija.example.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PlaceRepository placeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UserRepository userRepository, PostRepository postRepository, 
                     PlaceRepository placeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.placeRepository = placeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() > 0) {
            return; // Data already loaded
        }

        // Create admin user
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@socialnet.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setCity("Belgrade");
        admin.setAdmin(true);
        userRepository.save(admin);

        // Create regular users
        User user1 = new User();
        user1.setFirstName("Marko");
        user1.setLastName("Petrovic");
        user1.setEmail("marko@example.com");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setCity("Novi Sad");
        userRepository.save(user1);

        User user2 = new User();
        user2.setFirstName("Ana");
        user2.setLastName("Jovanovic");
        user2.setEmail("ana@example.com");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setCity("Nis");
        userRepository.save(user2);

        User user3 = new User();
        user3.setFirstName("Stefan");
        user3.setLastName("Nikolic");
        user3.setEmail("stefan@example.com");
        user3.setPassword(passwordEncoder.encode("password123"));
        user3.setCity("Kragujevac");
        userRepository.save(user3);

        // Create some places
        Place place1 = new Place();
        place1.setName("Kalemegdan Park");
        place1.setCountry("Serbia");
        place1.setCity("Belgrade");
        place1.setDescription("Historic fortress and park in the heart of Belgrade. Beautiful views of Danube and Sava rivers. #historic #park #fortress #belgrade");
        place1.getHashtags().add("historic");
        place1.getHashtags().add("park");
        place1.getHashtags().add("fortress");
        place1.getHashtags().add("belgrade");
        placeRepository.save(place1);

        Place place2 = new Place();
        place2.setName("Petrovaradin Fortress");
        place2.setCountry("Serbia");
        place2.setCity("Novi Sad");
        place2.setDescription("Famous fortress known as the Gibraltar of the Danube. Hosts the EXIT music festival. #fortress #festival #exit #novisad");
        place2.getHashtags().add("fortress");
        place2.getHashtags().add("festival");
        place2.getHashtags().add("exit");
        place2.getHashtags().add("novisad");
        placeRepository.save(place2);

        Place place3 = new Place();
        place3.setName("Nis Fortress");
        place3.setCountry("Serbia");
        place3.setCity("Nis");
        place3.setDescription("Ancient fortress with rich Ottoman heritage. Great for history lovers! #ancient #ottoman #history #nis");
        place3.getHashtags().add("ancient");
        place3.getHashtags().add("ottoman");
        place3.getHashtags().add("history");
        place3.getHashtags().add("nis");
        placeRepository.save(place3);

        // Create some posts
        Post post1 = new Post();
        post1.setContent("Just visited Kalemegdan! Amazing sunset view over the rivers. #kalemegdan #belgrade #sunset #travel");
        post1.setAuthor(user1);
        post1.getHashtags().add("kalemegdan");
        post1.getHashtags().add("belgrade");
        post1.getHashtags().add("sunset");
        post1.getHashtags().add("travel");
        post1.setLikesCount(5);
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setContent("Excited for EXIT festival at Petrovaradin! Who's joining? #exit #festival #music #novisad");
        post2.setAuthor(user2);
        post2.getHashtags().add("exit");
        post2.getHashtags().add("festival");
        post2.getHashtags().add("music");
        post2.getHashtags().add("novisad");
        post2.setLikesCount(12);
        postRepository.save(post2);

        Post post3 = new Post();
        post3.setContent("Exploring the history of Nis Fortress today. Such an interesting place! #history #fortress #nis #culture");
        post3.setAuthor(user3);
        post3.getHashtags().add("history");
        post3.getHashtags().add("fortress");
        post3.getHashtags().add("nis");
        post3.getHashtags().add("culture");
        post3.setLikesCount(3);
        postRepository.save(post3);

        Post post4 = new Post();
        post4.setContent("Welcome to SocialNet! This is our new social platform. Share your experiences and discover new places! #welcome #socialnet #newplatform");
        post4.setAuthor(admin);
        post4.getHashtags().add("welcome");
        post4.getHashtags().add("socialnet");
        post4.getHashtags().add("newplatform");
        post4.setLikesCount(25);
        postRepository.save(post4);

        System.out.println("Sample data loaded successfully!");
        System.out.println("Admin login: admin@socialnet.com / admin123");
        System.out.println("User logins: marko@example.com, ana@example.com, stefan@example.com / password123");
    }
}