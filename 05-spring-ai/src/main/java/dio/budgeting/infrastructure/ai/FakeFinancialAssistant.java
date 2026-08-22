package dio.budgeting.infrastructure.ai;

import dio.budgeting.application.FinancialAssistant;
import dio.budgeting.application.PersistTransactionUseCase;
import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.domain.Category;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class FakeFinancialAssistant implements FinancialAssistant {
    private final PersistTransactionUseCase persistTransactionUseCase;

    public FakeFinancialAssistant(PersistTransactionUseCase persistTransactionUseCase) {
        this.persistTransactionUseCase = persistTransactionUseCase;
    }

    @Override
    public String process(String userMessage) {
        persistTransactionUseCase.execute(
                new PersistTransactionInput("Compra simulada no mercado", 8000, Category.GROCERIES));

        return "Transação simulada de 80 reais registrada na categoria GROCERIES.";
    }
}
