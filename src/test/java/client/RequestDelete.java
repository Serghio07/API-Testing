package client;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public final class RequestDelete implements IRequest {
    @Override
    public Response send(RequestInfo requestInfo) {
        logRequest(requestInfo, "DELETE", false);
        Response response = given()
                .header("Token", requestInfo.getTokenString())
                .accept(JSON)
                .when()
                .delete(requestInfo.getUrl());
        logResponse(response);
        return response;
    }
}
