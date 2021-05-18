package com.assignment.booksmanagement.utility;

import com.assignment.booksmanagement.exception.DefaultRuntimeException;
import com.assignment.booksmanagement.model.Book;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CsvParserUtility {
    public static final String TYPE = "text/csv";
    static String[] headers = { "ISBN", "TITLE", "AUTHOR", "TAGS" };

    private CsvParserUtility() {
    }

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals((file.getContentType()));
    }

    public static List<Book> readFile(InputStream inputStream) {
        try (
                        BufferedReader fileReader = new BufferedReader(
                                        new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                        CSVParser csvParser = new CSVParser(fileReader,
                                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase()
                                                        .withTrim())
        ) {
            Stream<CSVRecord> csvRecordStream = StreamSupport.stream(csvParser.spliterator(), false);
            return csvRecordStream
                            .parallel()
                            .map(CSVRecord::toMap)
                            .map(Book::new)
                            .collect(Collectors.toList());

        } catch (IOException ioException) {
            throw new DefaultRuntimeException("IOException:fail to parse CSV file:" + ioException.getMessage());
        }
        catch (Exception exception) {
            throw new DefaultRuntimeException("Exception:fail to parse CSV file:" + exception.getMessage());
        }

    }

}
