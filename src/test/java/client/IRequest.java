package client;

import io.restassured.response.Response;

import java.util.Map;

public interface IRequest {
    Response send(RequestInfo requestInfo);

    default void logRequest(RequestInfo requestInfo, String method, boolean hasJsonBody) {
        System.out.println("\nPetición HTTP:");
        System.out.println("Método: " + method);
        System.out.println("URI: " + requestInfo.getUrl());
        System.out.println("Encabezados:");
        if (hasJsonBody) {
            System.out.println("    Content-Type: application/json");
        }
        System.out.println("    Accept: application/json");

        Object requestBody = requestInfo.getBody();
        if (!hasJsonBody || !(requestBody instanceof Map<?, ?>)) {
            System.out.println("Cuerpo: vacío");
            return;
        }

        Map<?, ?> fields = (Map<?, ?>) requestBody;
        System.out.println("Cuerpo:");
        System.out.println("{");
        boolean hasPrintedField = false;
        hasPrintedField = printSafeField(fields, "Email", false, hasPrintedField);
        hasPrintedField = printSafeField(fields, "Password", true, hasPrintedField);
        printSafeField(fields, "FullName", false, hasPrintedField);
        System.out.println("}");
    }

    default void logResponse(Response response) {
        System.out.println("Estado de respuesta: " + response.statusCode());
        try {
            Object id = response.jsonPath().get("Id");
            if (id != null) {
                System.out.println("ID de usuario: " + id);
            }
        } catch (RuntimeException ignored) {
            // La respuesta puede no ser JSON o no contener un ID.
        }
        System.out.println();
    }

    private static boolean printSafeField(Map<?, ?> fields, String name,
                                          boolean redact, boolean previousFieldPrinted) {
        if (!fields.containsKey(name)) {
            return previousFieldPrinted;
        }
        if (previousFieldPrinted) {
            System.out.print(",\n");
        }
        String value = redact ? "[REDACTED]" : escapeJson(String.valueOf(fields.get(name)));
        System.out.print("    \"" + name + "\": \"" + value + "\"");
        return true;
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t");
    }
}
