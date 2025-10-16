package university.jala.finalProject.UI.service;

import org.springframework.stereotype.Service;
import university.jala.finalProject.springJPA.entity.AppUser;

@Service
public class CurrentUserService {
    private AppUser currentUser;

    public void setCurrentUser(AppUser user) {
        this.currentUser = user;
        System.out.println("Usuario establecido: " + (user != null ? user.getUserName() : "null"));
    }

    public AppUser getCurrentUser() {
        return currentUser;
    }

    public Integer getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUserName() : null;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public void clear() {
        this.currentUser = null;
    }
}