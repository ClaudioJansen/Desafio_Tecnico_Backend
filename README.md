# 🗳️ Voting System API

API REST para gerenciamento de pautas, sessões de votação e votos, desenvolvida como solução para um desafio técnico no contexto de cooperativismo.

---

## ✅ Objetivo da Solução

O sistema foi projetado para oferecer uma API robusta e escalável capaz de:

- Cadastrar pautas de votação
- Abrir sessões associadas a essas pautas com tempo configurável
- Receber votos validados por CPF
- Retornar o resultado da votação apenas após o término da sessão
- Publicar o resultado no Kafka de forma assíncrona

---

## ⚙️ Decisões de Arquitetura

- **Java + Spring Boot**: estrutura robusta e com excelente suporte à criação de APIs REST.
- **PostgreSQL**: banco de dados relacional com persistência garantida.
- **Kafka (via Confluent Cloud)**: para mensageria assíncrona de resultados.
- **Feign Client**: integração com API externa de validação de CPF.
- **Spring Cache**: cache para CPFs validados e evitar múltiplas chamadas desnecessárias.
- **SLF4J + Logback**: logs centralizados e padronizados.
- **Validações centralizadas**: `DomainValidator` criado para manter regras de negócio reutilizáveis.
- **Testes unitários e automatizados** com cobertura ampla via JUnit e MockMvc.

---

## 📑 Documentação da API

Após iniciar a aplicação, acesse a documentação Swagger em:

🔗 [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)

Principais endpoints:

| Método | Endpoint                      | Descrição                                |
|--------|-------------------------------|-------------------------------------------|
| POST   | `/api/v1/agendas`             | Criar nova pauta                          |
| POST   | `/api/v1/sessions`            | Abrir sessão de votação                   |
| POST   | `/api/v1/votes`               | Votar (CPF + escolha)                     |
| GET    | `/api/v1/votes/result/{id}`   | Buscar resultado da votação da pauta      |

---

## ⚙️ Tecnologias

- Java 17 + Spring Boot
- PostgreSQL
- Apache Kafka
- Feign Client
- Spring Validation
- Swagger (SpringDoc)
- JUnit + Mockito
- Docker + Docker Compose

---

## 🚀 Como executar localmente

### 1. Clonar o repositório

```bash
git clone https://github.com/ClaudioJansen/Desafio_Tecnico_Backend.git
cd Desafio_Tecnico_Backend
cd voting-service
```

### 2. Subir os serviços com Docker

Certifique-se de que você tenha o Docker e o Docker Compose instalados.

Execute o comando abaixo para subir o PostgreSQL e o Kafka:

```bash
docker-compose up -d
```

Este comando irá levantar os containers:
- PostgreSQL (`localhost:5433`)
- Apache Kafka (`localhost:9092`)
- Zookeeper (dependência do Kafka)

> ⚠️ O Kafka está configurado com um broker local e tópico `voting-results`.

### 3. Rodar a aplicação

Com os serviços no ar, execute:

```bash
./mvnw spring-boot:run
```

Ou, se preferir usar Docker:

```bash
./mvnw clean package -DskipTests
docker build -t voting-system .
docker run -p 8080:8080 voting-system
```

---

## 📨 Kafka

Após o encerramento de uma sessão de votação, o resultado é enviado de forma **assíncrona** para o Kafka no tópico:

```
voting-results
```

A mensagem é serializada em JSON e pode ser consumida por qualquer serviço que se inscreva nesse tópico.

---

## 🧪 Testes

### Testes Unitários

Executam validações específicas por camada (controller, service e validators), com cobertura ampla.

```bash
./mvnw test
```



## 🧠 Considerações Finais

Este projeto aplica boas práticas como:

- Clean Code e SOLID
- Validações centralizadas com `DomainValidator`
- Integração com API externa com fallback controlado
- Logging do comportamento e das chamadas externas
- Publicação Kafka assíncrona com `CompletableFuture`
- Testes unitários e automatizados
- Cache com Spring Cache para evitar chamadas externas redundantes

---

## 👨‍💻 Autor

Cláudio Jansen — 2025 
cd voting-system
