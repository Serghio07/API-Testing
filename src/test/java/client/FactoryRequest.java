package client;

import java.util.Locale;

public final class FactoryRequest {
    private FactoryRequest() { }

    public static IRequest create(RequestInfo requestInfo) {
        String method = requestInfo.getMethod().toUpperCase(Locale.ROOT);
        if ("POST".equals(method)) {
            return new RequestPost();
        }
        if ("GET".equals(method)) {
            return new RequestGet();
        }
        if ("PUT".equals(method)) {
            return new RequestPut();
        }
        if ("DELETE".equals(method)) {
            return new RequestDelete();
        }
        throw new UnsupportedOperationException("Método HTTP aún no implementado.");
    }
}
