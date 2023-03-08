package com.example.demo.domain.event.asyncevents;

import com.example.demo.core.exception.OpenAIResponseUnprocessableException;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
@Log4j2
public class TextAnalyzer {
    private ResponseTypes getResposeTypeForString(String response) {
        if (Pattern.compile("[0-9].*").matcher(response).find()) {
            return ResponseTypes.NUMBERED_LIST;
        } else {
            System.out.println("Response that isn't numbered list: " + response);
        }
        if (Pattern.compile("[a-zA-Z]+,").matcher(response).find()) {
            return ResponseTypes.COMMA_SEPERATED;
        } else {
            System.out.println("Response that isn't comma-seperated list: " + response);
        }
        return ResponseTypes.NUMBERED_LIST;
    }

    private List<String> getLabelsForResponse(String response) throws OpenAIResponseUnprocessableException {
        ResponseTypes responseType = getResposeTypeForString(response);

        switch (responseType) {
            case NUMBERED_LIST -> {
                return getLabelsForNumberedListResponse(response);
            }
            case COMMA_SEPERATED -> {
                return getLabelsForCommaSeperatedResponse(response);
            }
        }
        throw new OpenAIResponseUnprocessableException("Unable to process OpenAi response: " + response + "\n. Response wasn't a numbered list, nor a comma-delimited one");
    }

    private List<String> getLabelsForCommaSeperatedResponse(String response) {
        return Arrays.stream(response.split(",")).map(String::trim).toList();
    }

    private List<String> getLabelsForNumberedListResponse(String response) {
        return Arrays.stream(response.split("\n"))
                .map(label -> label.replaceFirst("\\d. ", ""))
                .filter(label -> Pattern.compile("[a-zA-Z]").matcher(label).find())
                .toList();
    }

    private String getOpenAiTextCompletion(String query) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        classLoader.getResourceAsStream("apikey");
        byte[] bytes = IOUtils.toByteArray(Objects.requireNonNull(classLoader.getResourceAsStream("apikey")));

        log.debug("Calling OpenAI with query: " + query);
        OpenAiService service = new OpenAiService(new String(bytes, StandardCharsets.UTF_8));
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(query)
                .model("text-davinci-001")
                .temperature(0.0)
                .build();
        List<String> responses = service.createCompletion(completionRequest).getChoices()
                .stream()
                .map(CompletionChoice::getText)
                .toList();

        return responses.get(0);
    }

    /**
     * Get the labels for the provided text. This method is a wrapper around <a href="https://fasttext.cc/"/>.
     *
     * @param text           Text for which the labels should be analyzed
     * @param numberOfLabels How many labels should be predicted?
     * @return This returns a list of pairs. Each pair consists of a label and the confidence of said label.
     */
    public List<String> getLabelsForText(String text, int numberOfLabels) throws OpenAIResponseUnprocessableException, IOException {
        String query = String.format("Create %d one-word labels for this text: \"%s\". Don't provide any description:", numberOfLabels, text);
        String response = getOpenAiTextCompletion(query);
        return getLabelsForResponse(response);
    }
}

enum ResponseTypes {
    NUMBERED_LIST,
    COMMA_SEPERATED
}
