# Ponto Eletrônico - README

## Índice

1. [Introdução](#introdução)
2. [Funcionalidades](#funcionalidades)
3. [Tecnologias Utilizadas](#tecnologias-utilizadas)
4. [Instalação](#instalação)
5. [Configuração](#configuração)
6. [Endpoints da API](#endpoints-da-api)
7. [Segurança](#segurança)
8. [Utilização](#utilização)
9. [Estrutura do Projeto](#estrutura-do-projeto)
 

## Introdução

O sistema Ponto Eletrônico é uma aplicação desenvolvida para gerenciar o registro de ponto eletrônico de funcionários de uma empresa. Ele permite a criação, atualização e consulta de registros de ponto, além de oferecer funcionalidades de gestão de funcionários e empresas.

## Funcionalidades

- Registro de check-in e check-out de funcionários.
- Gestão de empresas e seus respectivos funcionários.
- Upload, download e gerenciamento de imagens associadas aos funcionários.
- Validação de permissões para gerentes.
- Busca de endereços via API de CEP.
- Autenticação e autorização com JWT.

## Tecnologias Utilizadas

- **Kotlin**: Linguagem principal de desenvolvimento.
- **Spring Boot**: Framework para construção da aplicação.
- **Spring Security**: Segurança da aplicação com autenticação e autorização.
- **Spring Data JPA**: Persistência de dados.
- **Hibernate**: ORM para mapeamento objeto-relacional.
- **H2 Database**: Banco de dados em memória para desenvolvimento.
- **MySQL**: Banco de dados relacional para produção.
- **Jakarta Validation**: Validação de dados.
- **ModelMapper**: Mapeamento de objetos.
- **Swagger/OpenAPI**: Documentação da API.
- **RestTemplate**: Consumo de APIs REST.
- **Maven**: Gerenciamento de dependências e build da aplicação.

## Instalação

1. Clone o repositório:

```bash
git clone <URL_DO_REPOSITORIO>
cd ponto_eletronico
``````
2. Compile o projeto:

```bash
./mvnw clean install
````

3. Execute a aplicação:
 ```bash
./mvnw spring-boot:run
````
## Configuração

### Banco de Dados

A configuração do banco de dados está localizada no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ponto_eletronico
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
````
## Segurança

As configurações de segurança estão definidas na classe `WebSecurityConfig`. O sistema utiliza JWT para autenticação e autorização.

### ViaCEP

Para a busca de endereços, a aplicação utiliza a API do ViaCEP. Certifique-se de que o serviço de CEP está corretamente configurado na classe `CepService`.

## Endpoints da API

### Autenticação

- **POST /authentication/login**: Autentica um usuário e retorna um token JWT.

### Empresas

- **GET /company/search/all**: Lista todas as empresas cadastradas, informando a quantidade de funcionários no sistema. Requer role ADMIN para acesso.
- **GET /company/search/cnpj**: Retorna dados detalhados da empresa pelo CNPJ. Requer role ADMIN para acesso.
- **GET /company/search/name**: Retorna uma empresa específica pelo nome.
- **POST /company**: Registra uma nova empresa no sistema. Requer role ADMIN para acesso.
- **PUT /company**: Altera dados de uma empresa no sistema. Requer role ADMIN para acesso.
- **DELETE /company**: Exclui uma empresa pelo CNPJ.

### Funcionários

- **GET /employee/search/id**: Usuário tem acesso às suas próprias informações. Requer role USER para acesso.
- **POST /employee/password/reset**: Reseta a senha do funcionário, gerando uma senha provisória enviada ao email do colaborador.
- **PUT /employee/password/update**: Altera a senha do funcionário ao confirmar a senha antiga. Requer role USER para acesso.
- **GET /employee/search/adm/all**: Retorna uma lista com todos os funcionários da empresa. Requer role MANAGER para acesso.
- **GET /employee/search/adm/name**: Retorna um funcionário pelo nome e sobrenome. Requer role MANAGER para acesso.
- **GET /employee/search/adm/cpf**: Retorna um funcionário pelo CPF. Requer role MANAGER para acesso.
- **POST /employee/adm/register**: Cadastra um colaborador na empresa, associando automaticamente ao gerente logado. Requer role MANAGER para acesso.
- **PUT /employee/adm/update**: Atualiza dados de um colaborador. Requer role MANAGER para acesso.
- **DELETE /employee/adm/delete**: Exclui um colaborador pelo CPF. Requer role MANAGER para acesso.

### Imagens

- **POST /files/upload**: Faz o upload de uma imagem.
- **GET /files/search/all**: Lista todas as imagens do funcionário logado.
- **GET /files/download/{imageId}**: Faz o download de uma imagem.
- **PATCH /files/{imageId}**: Atualiza a descrição de uma imagem.
- **DELETE /files/{imageId}**: Deleta uma imagem por ID.
- **GET /files/search/adm/imagens/{cpf}**: Busca todas as imagens do funcionário pelo gerente. Requer role MANAGER para acesso.
- **GET /files/adm/download/{imageId}**: Faz o download de uma imagem pelo gerente. Requer role MANAGER para acesso.
- **PATCH /files/adm/delete/{imageId}**: Altera a descrição de uma imagem pelo gerente. Requer role MANAGER para acesso.
- **DELETE /files/adm/delete/{imageId}**: Deleta uma imagem por ID pelo gerente. Requer role MANAGER para acesso.

### Controle de Horas

- **POST /time/checkin**: Registra a entrada do funcionário.
- **POST /time/checkout**: Registra a saída do funcionário.
- **GET /time/search/report**: Busca o registro de horas do funcionário.
- **GET /time/search/balance**: Busca as horas extras do funcionário.
- **GET /time/search/adm/report**: Administrador busca o registro de horas do funcionário. Requer role MANAGER para acesso.
- **GET /time/search/adm/balance**: Administrador busca as horas extras do funcionário. Requer role MANAGER para acesso.
- **DELETE /time/adm/delete**: Administrador deleta o registro de horas do funcionário. Requer role MANAGER para acesso.
- **PUT /time/adm/update**: Altera o registro do funcionário. Requer role MANAGER para acesso.
- **GET /time/search/adm/report/export**: Gera relatório das horas do funcionário.

## Segurança

A aplicação utiliza JWT (JSON Web Token) para autenticação e autorização. O token JWT é gerado no login e deve ser enviado no header Authorization em todas as requisições subsequentes.

### Configuração do JWT

As configurações do JWT estão localizadas no arquivo `application.properties`:

```properties
jwt.secret=<SECRET_KEY>
jwt.expiration=3600
````
### Middleware de Autenticação

A classe `JwtRequestFilter` intercepta as requisições HTTP para validar o token JWT.

## Utilização

### Swagger

A documentação da API está disponível via Swagger. Para acessá-la, inicie a aplicação e acesse:

```bash
http://localhost:8080/swagger-ui.html
````

## Estrutura do Projeto
```bash
ponto_eletronico
├── src
│   ├── main
│   │   ├── java/br/com/santanna/ponto_eletronico
│   │   │   ├── app
│   │   │   │   └── entrypoint/http
│   │   │   │   └── handler
│   │   │   ├── domain
│   │   │   │   ├── dataprovider
│   │   │   │   ├── dto
│   │   │   │   ├── entity
│   │   │   │   └── service
│   │   │   ├── infrastructure
│   │   │   │   ├── config
│   │   │   │   ├── dataprovider.impl
│   │   │   │   ├── repository
│   │   │   │   └── security
│   └── resources
│       └── application.properties
├── pom.xml
└── README.md
````
