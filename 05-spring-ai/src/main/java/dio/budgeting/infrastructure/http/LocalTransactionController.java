package dio.budgeting.infrastructure.http;

import dio.budgeting.application.FinancialAssistant;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@Profile("local")
public class LocalTransactionController {
    private final FinancialAssistant financialAssistant;

    public LocalTransactionController(FinancialAssistant financialAssistant) {
        this.financialAssistant = financialAssistant;
    }

    @PostMapping(value = "/ai", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String process(@RequestParam("message") String message) {
        return financialAssistant.process(message);
    }
}
