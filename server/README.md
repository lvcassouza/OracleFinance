# Oracle Finance Server

- Stack: Ktor + Netty, Exposed, PostgreSQL, HikariCP, Koin, JWT, Kotlinx Serialization.

## Variáveis de ambiente
- `DB_URL` exemplo `jdbc:postgresql://localhost:5432/oraclefinance`
- `DB_USER` exemplo `oracle_user`
- `DB_PASSWORD` exemplo `oracle_pass`
- `JWT_SECRET` segredo HMAC256
- `JWT_ISSUER` exemplo `oraclefinance`
- `JWT_AUDIENCE` exemplo `oraclefinance-client`
- `JWT_REALM` exemplo `oraclefinance`
- `PORT` porta do servidor, padrão `8080`

## Banco via Docker
- `docker-compose up -d`

## Executar servidor
- Requer Gradle 8+ e JDK 17
- `cd server`
- `gradle run` ou `./gradlew run`

## Endpoints
- `POST /auth/register` body `{"email":"a@b.com","password":"123"}`
- `POST /auth/login` body `{"email":"a@b.com","password":"123"}` retorna `{accessToken,refreshToken}`
- `GET /transactions` header `Authorization: Bearer <token>`
- `POST /transactions` body `{description,amount,type,category,date}`
- `GET /dashboard` header `Authorization: Bearer <token>`
