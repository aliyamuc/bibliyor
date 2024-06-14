package ai.datascope.bibliyor.modules.biblio.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.BreakIterator;
import java.util.Locale;

@Component
public class PdfChunker {

    private static final int MAX_TOKENS = 2000; // Adjust based on your model's token limit

    public List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        List<String> sentences = splitIntoSentences(text);

        StringBuilder currentChunk = new StringBuilder();
        int currentTokenCount = 0;

        for (String sentence : sentences) {
            int sentenceTokenCount = countTokens(sentence);

            if (currentTokenCount + sentenceTokenCount > MAX_TOKENS) {
                // Mevcut chunk'ı listeye ekle ve yeni bir chunk başlat
                chunks.add(currentChunk.toString());
                currentChunk.setLength(0);
                currentTokenCount = 0;
            }

            currentChunk.append(sentence).append(" ");
            currentTokenCount += sentenceTokenCount;
        }

        // Son chunk'ı ekle
        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk.toString());
        }

        return chunks;
    }

    private List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        String cleanedText = this.cleanText(text);
        iterator.setText(cleanedText);
        int start = iterator.first();

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            sentences.add(text.substring(start, end).trim());
        }

        return sentences;
    }

    private int countTokens(String text) {
        // Basit bir tokenizer kullanarak kelime sayımı
        // OpenAI tokenizer gibi karmaşık tokenizasyon işlemleri için özel kütüphaneler kullanılabilir
        String regex = "\\w+|\\s+|\\p{Punct}"; // Kelimeleri ve noktalama işaretlerini ayıran regex
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        int tokenCount = 0;
        while (matcher.find()) {
            tokenCount++;
        }
        return tokenCount;
    }

    private String cleanText(String text) {
        // NULL karakterini ve diğer geçersiz karakterleri kaldır
        return text.replaceAll("\\x00", "")
                .replaceAll("[^\\p{Print}\\p{Space}]", "");
    }



}
