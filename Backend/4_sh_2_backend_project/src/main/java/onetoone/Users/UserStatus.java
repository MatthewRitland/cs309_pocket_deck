package onetoone.Users;

public enum UserStatus {
    ONLINE,
    IN_GAME,
    SPECTATING,
    AWAY,
    OFFLINE
}

/*
CURRENTLY GET:
2026-02-26T21:52:21.644-06:00 ERROR 20184 --- [nio-8080-exec-1] o.h.engine.jdbc.spi.SqlExceptionHelper   : Field 'if_active' doesn't have a default value
from main, and from YAAK,

500 internal service error
{
  "timestamp": "2026-02-27T03:56:03.523+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/signup"
}

after creating a post request
{
  "userName" : "testingfeb26",
  "password" : "feb26pass"
}

because now the field 'if_active' doesn't have a default value, and when
the database gets that sign up request with no value for if_active, it gives an error.

 */