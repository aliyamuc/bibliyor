package ai.datascope.bibliyor.modules.etl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Transactional(readOnly = true)
@Service
public class ExcelService {

    @Transactional
    public Boolean loadExcelFile() throws IOException {
        List<List<String>> excelData = new ArrayList<>();
        ClassPathResource classPathResource = new ClassPathResource("slr/scopus.xlsx");

        try (InputStream inputStream = classPathResource.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                List<String> rowData = getStrings(row);
                excelData.add(rowData);
            }
        }catch(IOException e){
            log.error(e.getMessage());
        }
        log.info(excelData.toString());
        return Boolean.TRUE;
    }

    private static List<String> getStrings(Row row) {
        List<String> rowData = new ArrayList<>();
        for (Cell cell : row) {
            switch (cell.getCellType()) {
                case STRING:
                    rowData.add(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    rowData.add(String.valueOf(cell.getNumericCellValue()));
                    break;
                case BOOLEAN:
                    rowData.add(String.valueOf(cell.getBooleanCellValue()));
                    break;
                default:
                    rowData.add("");
            }
        }
        return rowData;
    }

}
