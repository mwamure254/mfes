package com.mfano.mfes.auth.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mfes.auth.models.Profile;
import com.mfano.mfes.auth.models.User;
import com.mfano.mfes.auth.repositories.ProfileRepository;
import com.mfano.mfes.auth.repositories.UserRepository;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private Profile profile, existing;
    private final UserRepository userService;

    @Value("${app.upload-dir}")
    private String baseDirectory;
    // private String baseDirectory = "src/main/resources/static/image/profile/";

    public ProfileService(UserRepository userService, ProfileRepository profileRepository) {
        this.userService = userService;
        this.profileRepository = profileRepository;
    }

    // Get All Profiles
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    // Get Profile By Id
    public Profile findById(Long id) {
        return profileRepository.findById(id).orElse(null);
    }

    // Get Profile By Id
    public Profile findByUser(Long user) {
        return profileRepository.findByUser(user);
    }

    // Delete Profile
    public void delete(Long id) {
        profileRepository.deleteById(id);
    }

    // Save Profile
    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    // Update Profile
    public void update(Long id, Profile profile2) {
        existing = findById(id);

        // update only editable fields
        existing.setFin(profile2.getFin());
        existing.setLan(profile2.getLan());
        existing.setOther(profile2.getOther());
        existing.setAbout(profile2.getAbout());
        existing.setGender(profile2.getGender());
        existing.setSN(profile2.getSN());
        existing.setDesignation(profile2.getDesignation());
        existing.setCounty(profile2.getCounty());
        existing.setAddress(profile2.getAddress());
        existing.setPhone(profile2.getPhone());
        existing.setEmail(profile2.getEmail());
        existing.setTwitter(profile2.getTwitter());
        existing.setFacebook(profile2.getFacebook());
        existing.setInstagram(profile2.getInstagram());
        existing.setLinkedin(profile2.getLinkedin());

        save(existing);

    }

    // Update Profile Image
    public void updateProfileImage(Long userid, MultipartFile file, RedirectAttributes red) throws IOException {
        User user = userService.findById(userid).orElse(null);
        try {
            Path path = Paths.get(baseDirectory, "profiles", file.getOriginalFilename())
                    .toAbsolutePath()
                    .normalize();
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            String img = file.getOriginalFilename();
            user.setImage(img);
            userService.save(user);
            red.addFlashAttribute("message", "Profile image updated successsfully");
        } catch (Exception e) {
            red.addFlashAttribute("error", e.getMessage());
        }

    }

    // Update Profile Image
    public void deleteProfileImage(Long userid, RedirectAttributes red) throws IOException {
        User user = userService.findById(userid).orElse(null);
        try {
            String image = user.getImage();
            Path path = Path.of(baseDirectory, "profiles", image)
                    .toAbsolutePath()
                    .normalize();
            Files.delete(path);
            user.setImage(null);
            userService.save(user);
            red.addFlashAttribute("message", "Profile image deleted successfully");
        } catch (Exception e) {
            red.addFlashAttribute("error", e.getMessage());
        }

    }

    // check profile
    public Profile checkProfile(Long userid) {
        profile = findByUser(userid);
        if (profile == null) {
            profile = new Profile();
            profile.setUser(userid);
            save(profile);

        }
        return profile;
    }

}
