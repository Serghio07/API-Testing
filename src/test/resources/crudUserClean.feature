@CreateUser @UserCRUD
Feature: Creación de usuario Todo.ly

  Scenario: Crear un usuario mediante API REST

    Given que tengo datos válidos de un usuario nuevo
    When envío una petición POST para crear el usuario

    Then la API debe confirmar la creación del usuario
    And el correo y nombre deben coincidir con los enviados
    And debo obtener un identificador válido

    When solicito el token del usuario creado
    Then la API debe devolver un token válido
    And el token debe pertenecer al usuario creado
    And la respuesta debe incluir la expiración del token
    And guardo el token para las siguientes operaciones
    When verifico el token con consulta GET de solo lectura
    When actualizo el nombre del usuario utilizando el token
    Then la API confirma actualizacion del usuario
    And el nombre actualizado debe coincidir con el enviado
    And el identificador y correo del usuario deben mantenerse
    When elimino el usuario utilizando el token
    Then la API debe confirmar la eliminación del usuario
    And los datos del usuario eliminado deben coincidir con los originales