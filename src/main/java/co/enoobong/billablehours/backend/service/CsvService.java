package co.enoobong.billablehours.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CsvService {

  private static final Logger log = LoggerFactory.getLogger(CsvService.class);

  private final CSVFormat csvFormat;

  public CsvService(CSVFormat csvFormat) {
    this.csvFormat = csvFormat;
  }

  List<CSVRecord> read(InputStream stream) throws IOException {
    log.info("parsing .csv file");
    try (final InputStreamReader inputStreamReader =
                 new InputStreamReader(stream, StandardCharsets.UTF_8);
         final CSVParser parser = csvFormat.parse(inputStreamReader)) {
      return parser.getRecords();
    }
  }
}
