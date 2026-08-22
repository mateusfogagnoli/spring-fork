package dio.budgeting.infrastructure.http;

import dio.budgeting.application.FinancialAssistant;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/transactions")
@Profile("openai")
public class OpenAiTransactionController {
    private final TranscriptionModel transcriptionModel;
    private final FinancialAssistant financialAssistant;
    private final TextToSpeechModel textToSpeechModel;

    public OpenAiTransactionController(TranscriptionModel transcriptionModel,
                                       FinancialAssistant financialAssistant,
                                       TextToSpeechModel textToSpeechModel) {
        this.transcriptionModel = transcriptionModel;
        this.financialAssistant = financialAssistant;
        this.textToSpeechModel = textToSpeechModel;
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    public ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {
        var userMessage = transcriptionModel.transcribe(file.getResource());
        var result = financialAssistant.process(userMessage);
        var resource = new ByteArrayResource(textToSpeechModel.call(result));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("audio.mp3").build().toString())
                .body(resource);
    }
}
