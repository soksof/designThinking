package gr.uoi.dthink.services;

public interface SecurityService {
    String findLoggedInUsername();
    void autoLogin(String email, String password);
}
