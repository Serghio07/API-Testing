package client;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public final class RequestPost implements IRequest {
    @Override
    public Response send(RequestInfo requestInfo) {
        logRequest(requestInfo, "POST", true);
        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(requestInfo.getBody())
                .post(requestInfo.getUrl());
        logResponse(response);
        return response;
    }
}
