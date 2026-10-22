package config;

public class Configuration {

    public static String BASE_URL = "https://todo.ly/";
    public static String USER = "serghiooooooooo@gmail.com";
    public static String PASSWORD = "Serghio.123";
    public static String CREATE_USER_ENDPOINT = "api/user.json";
    public static String TOKEN_ENDPOINT = "api/authentication/token.json";
    public static String UPDATE_USER_ENDPOINT = "api/user/0.json";

    public static String tokenUrl() {
        return BASE_URL + TOKEN_ENDPOINT;
    }
    public static String createUserUrl() {
        return BASE_URL + CREATE_USER_ENDPOINT;
    }
    public static String updateUserUrl() {
        return BASE_URL + UPDATE_USER_ENDPOINT;
    }
}
