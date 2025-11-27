# Oracle Finance

![Kotlin](https://img.shields.io/badge/Kotlin-%237F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-%233DDC84?logo=android&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-%232496ED?logo=docker&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-%23000000?logo=ktor&logoColor=white)

## Sobre o Projeto
Oracle Finance é um gerenciador financeiro Full Stack escrito inteiramente em Kotlin: Android (Jetpack Compose) + Backend (Ktor) + PostgreSQL. O app inclui um recurso de análise com IA (endpoint `/ai/analyze`), UI moderna com Material Design 3, arquitetura MVVM e gráficos financeiros usando Vico.

## Arquitetura
```mermaid
flowchart LR
    A[Android App (Jetpack Compose, MVVM, Koin)] <--> B[Ktor API (CIO, Serialization)]
    B <--> C[(PostgreSQL)]
```

## Pré-requisitos
- Docker Desktop
- JDK 17+
- Android Studio (Giraffe ou superior)

## Guia de Instalação

### 1) Subir o banco
No diretório `server/`, utilize:
```bash
docker-compose up -d
```
Certifique-se de que o `docker-compose.yml` provisiona um PostgreSQL acessível em `localhost:5432` (banco `oraclefinance`).

### 2) Configurar variáveis de ambiente do Backend
Crie um arquivo `.env` em `server/` (exemplo):
```
# Porta do servidor Ktor
SERVER_PORT=8080

# Configuração de banco
DATABASE_URL=jdbc:postgresql://localhost:5432/oraclefinance
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres

# JWT
JWT_SECRET=change_me_to_a_strong_secret
JWT_ISSUER=oraclefinance
JWT_AUDIENCE=oraclefinance-users
JWT_REALM=oraclefinance-realm
```
Garanta que o backend Ktor lê estas variáveis e inicializa a conexão com o Postgres e o provedor de tokens JWT.

### 3) Rodar o Backend
No diretório `server/`:
```bash
./gradlew run
```
O serviço ficará disponível em `http://localhost:8080`.

### 4) Rodar o App Android
- Abra o projeto no Android Studio, selecione um emulador Android.
- Para acesso ao backend local pelo emulador, use `http://10.0.2.2:8080`.
- No módulo `app`, o `BuildConfig.BASE_URL` já está definido para `http://10.0.2.2:8080` no perfil `debug`.
- O perfil `debug` também habilita tráfego cleartext para facilitar desenvolvimento local.
- Rode a aplicação.

## Endpoints Principais (Backend)
- `POST /auth/login` — Body: `{ email, password }` → Retorna `{ access_token, refresh_token }`
- `POST /auth/refresh` — Body: `{ refresh_token }` → Retorna `{ access_token, refresh_token }`
- `GET /dashboard/summary` — Retorna `{ income, expense, balance, last7Days: [{ dayLabel, amount }] }`
- `POST /transactions` — Body: `{ amount, description, type, category }` → Retorna `{ id }`
- `POST /ai/analyze` — Sem body obrigatório → Retorna `{ text }`

## Stack Técnica (Resumo)
- Android: Jetpack Compose (Material 3), Navigation Compose, MVVM, Koin
- Networking: Ktor Client (engine CIO) + Kotlinx Serialization
- Segurança: EncryptedSharedPreferences para armazenar `access_token` e `refresh_token`
- Gráficos: Vico Compose
- Backend: Ktor (CIO, Serialization), JWT, PostgreSQL

## Observações
- Em dispositivos físicos, configure o `BASE_URL` para o IP da sua máquina na rede local (ex.: `http://192.168.0.10:8080`).
- O cliente Android injeta `Authorization: Bearer <access_token>` automaticamente e executa `POST /auth/refresh` quando necessário.
- Certifique-se de que o Backend Ktor está ouvindo em `0.0.0.0` (e não `localhost`) para permitir conexões do emulador e containers Docker. Já está configurado em `server/src/main/kotlin/com/oraclefinance/Application.kt:20`.

## Licença
Projeto de uso interno/experimental. Ajuste conforme sua necessidade.
