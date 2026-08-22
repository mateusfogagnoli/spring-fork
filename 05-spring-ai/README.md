> Este projeto parte do módulo 05 (Spring AI) da trilha
> [DIO Spring Boot Learning Track](https://github.com/digitalinnovationone/dio-spring-boot-learning-track),
> via fork. A arquitetura original (camadas de domain/application/infrastructure
> e o fluxo de tool calling) segue o material do curso. A seção
> **"Alterações realizadas"**, abaixo, detalha minha contribuição específica.

# DIO Spring Boot - Final Project 05: Spring AI (budgeting)

## Introduction

This final module applies Spring AI in a budgeting API while preserving the same layered architecture used across the track.

The goal is to integrate AI capabilities without bypassing domain and use case boundaries.

## Code Context

The project processes voice commands to create and query financial transactions.

Primary flow:

1. Client uploads an audio file.
2. Audio is transcribed into text.
3. The model selects an application tool/use case.
4. The use case persists or queries transaction data.
5. The final response is converted to audio.

## Project Structure

- `src/main/java/dio/budgeting/domain`
  - Domain model and repository contract.
- `src/main/java/dio/budgeting/application`
  - Use cases used by both REST and AI tool calling.
- `src/main/java/dio/budgeting/infrastructure`
  - HTTP adapters, JPA adapters, and integration glue.

## Module-Specific Topics

### Speech-to-text

- Uses `TranscriptionModel` for audio transcription.
- Model settings are configured in `application-openai.properties`.

### Tool calling

- `ChatClient` registers use-case tools.
- `@Tool` methods expose business capabilities to the model.

### Text-to-speech

- `TextToSpeechModel` produces MP3 output from final text.
- AI endpoint returns generated audio.

## Spring AI Documentation

- Spring AI Reference: https://docs.spring.io/spring-ai/reference/index.html
- ChatModel API: https://docs.spring.io/spring-ai/reference/api/chatmodel.html
- ChatClient API: https://docs.spring.io/spring-ai/reference/api/chatclient.html
- Tools API: https://docs.spring.io/spring-ai/reference/api/tools.html
- Audio Transcriptions API: https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html
- Audio Speech API: https://docs.spring.io/spring-ai/reference/api/audio/speech.html

## Shared Architecture References

Common architecture concepts are documented in the root README:

- [DDD layers](../README.md#ddd-layered-architecture)
- [Class vs record](../README.md#java-class-vs-java-record-in-domain-modeling)
- [Strong typed identifiers](../README.md#strong-typed-identifiers)
- [Repository pattern](../README.md#repository-pattern)
- [Use cases and Clean Architecture](../README.md#use-cases-and-clean-architecture)
- [Docker Compose support](../README.md#docker-compose-support-in-development)

---

## Alterações realizadas

### Objetivo

Foi criado um modo local para executar e testar o projeto sem uma chave da
OpenAI. A integração real com a OpenAI continua disponível por meio de um
perfil separado. A motivação foi poder testar e demonstrar o projeto sem
depender de créditos pagos da OpenAI, mantendo a integração real intacta
e escondida atrás de uma interface.

### Principais alterações

#### 1. Abstração do assistente financeiro

Foi criada a interface `FinancialAssistant`:

```text
src/main/java/dio/budgeting/application/FinancialAssistant.java
```

Ela desacopla o fluxo da aplicação da implementação específica do provedor de
inteligência artificial.

#### 2. Implementação real com OpenAI

Foi criada a classe:

```text
infrastructure/ai/OpenAiFinancialAssistant.java
```

Essa implementação utiliza `ChatClient` e registra como ferramentas os casos
de uso de:

- Persistência de transações;
- Consulta de transações por categoria.

Ela é ativada somente com o perfil `openai`.

#### 3. Implementação falsa para o modo local

Foi criada a classe:

```text
infrastructure/ai/FakeFinancialAssistant.java
```

Essa implementação não acessa nenhum serviço externo. Ela simula o
processamento de uma mensagem e persiste uma transação de teste de 80 reais na
categoria `GROCERIES`.

Ela é ativada com o perfil `local`.

#### 4. Separação dos controllers

O `TransactionController` ficou responsável apenas pelos endpoints REST de
transações:

```text
POST /transactions
GET  /transactions/{category}
```

Foi criado o `OpenAiTransactionController`, que mantém o fluxo real:

```text
POST /transactions/ai
```

Esse endpoint recebe um áudio, realiza a transcrição, utiliza o modelo de chat
e retorna um arquivo MP3.

Também foi criado o `LocalTransactionController`. No modo local, o endpoint
aceita uma mensagem textual:

```text
POST /transactions/ai
```

#### 5. Separação das configurações

O arquivo `application.properties` agora contém apenas as configurações
comuns e define o perfil padrão:

```properties
spring.profiles.default=local
```

As configurações específicas da OpenAI foram movidas para:

```text
src/main/resources/application-openai.properties
```

A chave é obtida pela variável de ambiente `OPENAI_API_KEY`.

#### 6. Ajuste dos testes

Os testes que dependem da OpenAI receberam:

```java
// @ActiveProfiles("openai")
```

Eles continuam condicionados à existência da variável `OPENAI_API_KEY`.

Também foi adicionado o teste unitário:

```text
FakeFinancialAssistantTest.java
```

Esse teste valida o fluxo do assistente falso sem realizar chamadas externas.

### Observação sobre a validação

A execução dos testes depende da versão de Java definida no
`build.gradle`. O projeto exige Java 25; portanto, é necessário ter um JDK 25
instalado para executar o build normalmente.

---

## Como executar

### Sem chave da OpenAI (modo local, padrão)

No diretório `05-spring-ai`, inicie o banco:

```bash
docker compose up -d
```

Execute a aplicação:

```bash
./gradlew bootRun
```

No Windows:

```powershell
.\gradlew.bat bootRun
```

O perfil padrão é `local`, portanto a aplicação já sobe sem credenciais
externas. Nesse modo, o endpoint de IA aceita uma mensagem textual e usa um
assistente falso determinístico:

```bash
curl -X POST http://localhost:8080/transactions/ai \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "message=Gastei 80 reais no mercado"
```

O modo local permite testar o cadastro e a consulta de transações, além do
endpoint simulado de IA. O endpoint real de áudio fica disponível apenas no
perfil `openai`.

### Com OpenAI

Configure a chave (crie a variável de ambiente pelo PowerShell caso esteja
no Windows):

```bash
export OPENAI_API_KEY="sua_chave_aqui"
```

No PowerShell:

```powershell
$env:OPENAI_API_KEY="sua_chave_aqui"
```

Execute usando o perfil `openai`:

```bash
./gradlew bootRun --args="--spring.profiles.active=openai"
./gradlew test
```

Nesse perfil, o endpoint `/transactions/ai` recebe arquivos de áudio e
retorna respostas em MP3.

## Notes

- Educational final project focused on AI plus architectural discipline.
- Os testes unitários e o modo `local` não precisam de chave.
- Os testes de integração com a OpenAI precisam de `OPENAI_API_KEY`.