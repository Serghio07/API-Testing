package client;

public final class RequestInfo {
    private final String method;
    private final String url;
    private final Object body;
    private final String username;
    private final String password;
    private final String tokenString;

    public RequestInfo(String method, String url, Object body) {
        this(method, url, body, null, null);
    }

    public RequestInfo(String method, String url, Object body, String username, String password) {
        this(method, url, body, username, password, null);
    }

    public RequestInfo(String method, String url, Object body, String tokenString) {
        this(method, url, body, null, null, tokenString);
    }

    private RequestInfo(String method, String url, Object body, String username, String password,
                        String tokenString) {
        this.method = method;
        this.url = url;
        this.body = body;
        this.username = username;
        this.password = password;
        this.tokenString = tokenString;
    }

    public String getMethod() { return method; }
    public String getUrl() { return url; }
    public Object getBody() { return body; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getTokenString() { return tokenString; }
}
