package dio.budgeting.infrastructure.ai;

import dio.budgeting.application.FinancialAssistant;
import dio.budgeting.application.ListTransactionsByCategoryUseCase;
import dio.budgeting.application.PersistTransactionUseCase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("openai")
public class OpenAiFinancialAssistant implements FinancialAssistant {
    private final ChatClient chatClient;

    public OpenAiFinancialAssistant(ChatClient.Builder chatClientBuilder,
                                    PersistTransactionUseCase persistTransactionUseCase,
                                    ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase) {
        this.chatClient = chatClientBuilder
                .defaultTools(persistTransactionUseCase, listTransactionsByCategoryUseCase)
                .build();
    }

    @Override
    public String process(String userMessage) {
        return chatClient.prompt().user(userMessage).call().content();
    }
}
