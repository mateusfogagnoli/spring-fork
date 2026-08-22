package dio.budgeting;

import dio.budgeting.application.PersistTransactionUseCase;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.infrastructure.ai.FakeFinancialAssistant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FakeFinancialAssistantTest {
    @Test
    void shouldPersistSimulatedTransactionWithoutOpenAi() {
        var repository = mock(TransactionRepository.class);
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var assistant = new FakeFinancialAssistant(new PersistTransactionUseCase(repository));

        var response = assistant.process("Gastei 80 reais no mercado");

        assertThat(response).contains("80 reais", "GROCERIES");
        verify(repository).save(argThat(transaction ->
                transaction.getAmount() == 8000 && transaction.getCategory() == Category.GROCERIES));
    }
}
