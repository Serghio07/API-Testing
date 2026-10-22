package runner;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.response.Response;

public final class Runner {
    private static final ThreadLocal<Runner> CURRENT = new ThreadLocal<>();

    private String email;
    private String password;
    private String fullName;
    private Response response;
    private Long createdUserId;
    private String tokenString;

    @Before
    public void startScenario() {
        CURRENT.set(new Runner());
    }

    @After
    public void endScenario() {
        CURRENT.remove();
    }

    public static Runner current() {
        Runner context = CURRENT.get();
        if (context == null) {
            throw new IllegalStateException("El contexto del escenario no está inicializado.");
        }
        return context;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }
    public Long getCreatedUserId() { return createdUserId; }
    public void setCreatedUserId(Long createdUserId) { this.createdUserId = createdUserId; }
    public String getTokenString() { return tokenString; }
    public void setTokenString(String tokenString) { this.tokenString = tokenString; }
}