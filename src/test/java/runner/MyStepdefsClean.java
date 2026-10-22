package runner;

import client.FactoryRequest;
import client.IRequest;
import client.RequestInfo;
import config.Configuration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public final class MyStepdefsClean {
    @Given("que tengo datos válidos de un usuario nuevo")
    public void haveValidNewUserData() {
        String email = Configuration.USER;
        String password = Configuration.PASSWORD;
        assertTrue(email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"),
                "TODO_LY_TEST_EMAIL debe contener una dirección válida.");
        assertFalse(password.trim().isEmpty(), "TODO_LY_TEST_PASSWORD no puede estar vacío.");

        Runner context = Runner.current();
        context.setEmail(email);
        context.setPassword(password);
        context.setFullName("SERGIO QA USER");
    }

    @When("envío una petición POST para crear el usuario")
    public void sendCreateUserPost() {
        Runner context = Runner.current();
        Map<String, String> body = new LinkedHashMap<>();
        body.put("Email", context.getEmail());
        body.put("Password", context.getPassword());
        body.put("FullName", context.getFullName());

        RequestInfo requestInfo = new RequestInfo("POST", Configuration.createUserUrl(), body);
        IRequest request = FactoryRequest.create(requestInfo);
        context.setResponse(request.send(requestInfo));
    }

    @Then("la API debe confirmar la creación del usuario")
    public void apiMustConfirmCreation() {
        Response response = requireResponse();
        int statusCode = response.statusCode();
        assertTrue(statusCode >= 200 && statusCode < 300,
                "La respuesta HTTP no indica éxito (HTTP " + statusCode + ").");

        Map<String, Object> responseBody;
        try {
            responseBody = response.jsonPath().getMap("$");
        } catch (RuntimeException exception) {
            fail("Todo.ly respondió con un cuerpo que no es JSON válido.");
            return;
        }
        assertNotNull(responseBody, "Todo.ly no respondió con un objeto JSON.");
        assertFalse(responseBody.isEmpty(), "Todo.ly respondió con un objeto JSON vacío.");
        assertFalse(containsBusinessError(responseBody), "La respuesta contiene un error de negocio.");
    }

    @Then("el correo y nombre deben coincidir con los enviados")
    public void responseDataMustMatchRequest() {
        Map<String, Object> body = requireResponseBody();
        Runner context = Runner.current();
        assertEquals(context.getEmail(), body.get("Email"), "El correo de respuesta no coincide.");
        assertEquals(context.getFullName(), body.get("FullName"), "El nombre de respuesta no coincide.");
    }

    @Then("debo obtener un identificador válido")
    public void mustObtainValidId() {
        Object idValue = requireResponseBody().get("Id");
        assertNotNull(idValue, "La respuesta no contiene Id.");
        assertTrue(idValue instanceof Number, "Id debe ser numérico.");
        long id = ((Number) idValue).longValue();
        assertTrue(id > 0, "Id debe ser positivo.");
        Runner.current().setCreatedUserId(id);
    }

    @When("solicito el token del usuario creado")
    public void requestTokenForCreatedUser() {
        Runner context = Runner.current();
        RequestInfo requestInfo = new RequestInfo(
                "GET", Configuration.tokenUrl(), null, context.getEmail(), context.getPassword());
        IRequest request = FactoryRequest.create(requestInfo);
        context.setResponse(request.send(requestInfo));
    }

    @Then("la API debe devolver un token válido")
    public void apiMustReturnValidToken() {
        Response response = requireResponse();
        int statusCode = response.statusCode();
        assertTrue(statusCode >= 200 && statusCode < 300,
                "La respuesta HTTP no indica éxito (HTTP " + statusCode + ").");
        Map<String, Object> body = response.jsonPath().getMap("$");
        assertNotNull(body, "Todo.ly no respondió con un objeto JSON.");
        assertFalse(body.isEmpty(), "Todo.ly respondió con un objeto JSON vacío.");
        assertFalse(containsTokenAuthenticationError(body),
                "Todo.ly devolvió un error de autenticación.");
        Object tokenValue = body.get("TokenString");
        assertTrue(tokenValue instanceof String && !((String) tokenValue).trim().isEmpty(),
                "TokenString no puede ser nulo ni vacío.");
    }

    @Then("el token debe pertenecer al usuario creado")
    public void tokenMustBelongToCreatedUser() {
        Map<String, Object> body = requireResponseBody();
        assertEquals(Runner.current().getEmail(), body.get("UserEmail"),
                "El token no pertenece al correo creado.");
    }

    @Then("la respuesta debe incluir la expiración del token")
    public void tokenExpirationMustBePresent() {
        Object expiration = requireResponseBody().get("ExpirationTime");
        assertNotNull(expiration, "La respuesta no contiene ExpirationTime.");
        assertFalse(expiration.toString().trim().isEmpty(), "ExpirationTime no puede estar vacío.");
    }

    @Then("guardo el token para las siguientes operaciones")
    public void saveTokenForNextOperations() {
        Object tokenValue = requireResponseBody().get("TokenString");
        assertTrue(tokenValue instanceof String && !((String) tokenValue).trim().isEmpty(),
                "No se puede guardar un token vacío.");
        Runner.current().setTokenString((String) tokenValue);
    }

    @When("verifico el token con consulta GET de solo lectura")
    public void verifyTokenWithReadOnlyGet() {
        Runner context = Runner.current();
        RequestInfo requestInfo = new RequestInfo(
                "GET", Configuration.updateUserUrl(), null, context.getTokenString());
        IRequest request = FactoryRequest.create(requestInfo);
        context.setResponse(request.send(requestInfo));

        Response response = requireResponse();
        int statusCode = response.statusCode();
        assertTrue(statusCode >= 200 && statusCode < 300,
                "La autenticacion con cabecera Token fallo (HTTP " + statusCode + ").");
        Map<String, Object> body = requireResponseBody();
        assertFalse(containsTokenAuthenticationError(body),
                "La consulta autenticada devolvio un error de negocio.");
        assertEquals(context.getCreatedUserId(), numberValue(body.get("Id")),
                "La consulta autenticada no corresponde al usuario creado.");
        assertEquals(context.getEmail(), body.get("Email"),
                "La consulta autenticada no corresponde al correo creado.");
    }

    @When("actualizo el nombre del usuario utilizando el token")
    public void updateUserNameWithToken() {
        Runner context = Runner.current();
        Map<String, String> body = new LinkedHashMap<>();
        body.put("FullName", "Sergio QA Subido");

        RequestInfo requestInfo = new RequestInfo(
                "PUT", Configuration.updateUserUrl(), body, context.getTokenString());
        IRequest request = FactoryRequest.create(requestInfo);
        context.setResponse(request.send(requestInfo));
    }

    @Then("la API confirma actualizacion del usuario")
    public void apiMustConfirmUserUpdate() {
        Response response = requireResponse();
        int statusCode = response.statusCode();
        assertTrue(statusCode >= 200 && statusCode < 300,
                "La respuesta HTTP no indica exito (HTTP " + statusCode + ").");
        Map<String, Object> body = requireResponseBody();
        assertFalse(containsTokenAuthenticationError(body),
                "La respuesta contiene un error de negocio.");
    }

    @Then("el nombre actualizado debe coincidir con el enviado")
    public void updatedNameMustMatch() {
        assertEquals("Sergio QA Subido", requireResponseBody().get("FullName"),
                "El nombre actualizado no coincide.");
    }

    @Then("el identificador y correo del usuario deben mantenerse")
    public void userIdAndEmailMustRemainUnchanged() {
        Map<String, Object> body = requireResponseBody();
        Runner context = Runner.current();
        assertEquals(context.getCreatedUserId(), numberValue(body.get("Id")),
                "El Id del usuario cambio tras la actualizacion.");
        assertEquals(context.getEmail(), body.get("Email"),
                "El correo del usuario cambio tras la actualizacion.");
    }

    @When("elimino el usuario utilizando el token")
    public void deleteUserWithToken() {
        Runner context = Runner.current();
        RequestInfo requestInfo = new RequestInfo(
                "DELETE", Configuration.updateUserUrl(), null, context.getTokenString());
        IRequest request = FactoryRequest.create(requestInfo);
        context.setResponse(request.send(requestInfo));
    }

    @Then("la API debe confirmar la eliminación del usuario")
    public void apiMustConfirmUserDeletion() {
        Response response = requireResponse();
        int statusCode = response.statusCode();
        assertTrue(statusCode >= 200 && statusCode < 300,
                "La respuesta HTTP no indica exito (HTTP " + statusCode + ").");
        Map<String, Object> body = requireResponseBody();
        assertFalse(containsTokenAuthenticationError(body),
                "La respuesta de eliminacion contiene un error de negocio.");
    }

    @Then("los datos del usuario eliminado deben coincidir con los originales")
    public void deletedUserDataMustMatchExpected() {
        Map<String, Object> body = requireResponseBody();
        Runner context = Runner.current();
        assertEquals(context.getCreatedUserId(), numberValue(body.get("Id")),
                "El Id devuelto no coincide con el usuario creado.");
        assertEquals(context.getEmail(), body.get("Email"),
                "El correo devuelto no coincide con el usuario creado.");
        assertEquals("Sergio QA Subido", body.get("FullName"),
                "El FullName devuelto no coincide con el nombre actualizado.");
    }
    private Long numberValue(Object value) {
        assertTrue(value instanceof Number, "Id debe ser numerico.");
        return ((Number) value).longValue();
    }
    private boolean containsTokenAuthenticationError(Map<String, Object> body) {
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            String key = entry.getKey().toLowerCase(Locale.ROOT);
            Object value = entry.getValue();
            if ("errorcode".equals(key)) {
                if (value instanceof Number) {
                    if (((Number) value).doubleValue() != 0.0d) return true;
                } else if (value != null) {
                    String code = value.toString().trim();
                    if (!code.isEmpty() && !"0".equals(code)) return true;
                }
            } else if (key.contains("error") && value != null
                    && !value.toString().trim().isEmpty()
                    && !Boolean.FALSE.equals(value)) {
                return true;
            }
        }
        return false;
    }
    private Response requireResponse() {
        Response response = Runner.current().getResponse();
        assertNotNull(response, "No se recibió respuesta de Todo.ly.");
        return response;
    }

    private Map<String, Object> requireResponseBody() {
        Response response = requireResponse();
        Map<String, Object> body = response.jsonPath().getMap("$");
        assertNotNull(body, "La respuesta no contiene un objeto JSON.");
        return body;
    }

    private boolean containsBusinessError(Map<String, Object> body) {
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            if (entry.getKey().toLowerCase(Locale.ROOT).contains("error")
                    && entry.getValue() != null
                    && !entry.getValue().toString().trim().isEmpty()
                    && !Boolean.FALSE.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }
}