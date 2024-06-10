package ai.datascope.bibliyor.modules.biblio.datafiller;

import ai.datascope.bibliyor.modules.biblio.model.Biblio;
import ai.datascope.bibliyor.modules.biblio.model.ResearchQuestion;
import ai.datascope.bibliyor.modules.biblio.repository.BiblioRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@Order(10)
public class BiblioDataFiller implements CommandLineRunner {

    @Value("classpath:/slr/scopus.xlsx")
    private Resource biblioResource;

    @Autowired
    private BiblioRepository biblioRepository;

    @Override
    public void run(String... args) throws IOException {
        InputStream inputStream = biblioResource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheetBigData = workbook.getSheet("big data");
        Sheet sheetMachineLearning = workbook.getSheet("machine learning");
        saveSheet(sheetBigData);
        saveSheet(sheetMachineLearning);
    }

    private void saveSheet(Sheet sheet) {
        boolean isFirstRow = true;
        for (Row row : sheet) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }
            Biblio biblio = new Biblio();
            biblio.setAuthors(row.getCell(0).getStringCellValue());
            biblio.setAuthorFullNames(row.getCell(1).getStringCellValue());
            Cell authorsID = row.getCell(2);
            String authorsIDString = authorsID.getCellType() == CellType.STRING ? authorsID.getStringCellValue() :
                    String.valueOf((int) authorsID.getNumericCellValue());
            biblio.setAuthorsID(authorsIDString);
            biblio.setTitle(row.getCell(3).getStringCellValue());
            biblio.setYear((int) row.getCell(4).getNumericCellValue());
            biblio.setDOI(row.getCell(5).getStringCellValue());
            biblio.setSourceTitle(row.getCell(6).getStringCellValue());
            Cell cellVolume = row.getCell(7);
            String volumeString = cellVolume.getCellType() == CellType.STRING ? cellVolume.getStringCellValue() :
                    String.valueOf((int) cellVolume.getNumericCellValue());
            biblio.setVolume(volumeString);
            Cell cellIssue = row.getCell(8);
            String issueString = cellIssue.getCellType() == CellType.STRING ? cellIssue.getStringCellValue() :
                    String.valueOf((int) cellIssue.getNumericCellValue());
            biblio.setIssue(issueString);
            Cell cellArtNo = row.getCell(9);
            String artNoString = cellArtNo.getCellType() == CellType.STRING ? cellArtNo.getStringCellValue() :
                    String.valueOf((int) cellArtNo.getNumericCellValue());
            biblio.setArtNo(artNoString);
            biblio.setPageStart((int) row.getCell(10).getNumericCellValue());
            biblio.setPageEnd((int) row.getCell(11).getNumericCellValue());
            biblio.setPageCount((int) row.getCell(12).getNumericCellValue());
            biblio.setCitedBy((int) row.getCell(13).getNumericCellValue());
            biblio.setLink(row.getCell(14).getStringCellValue());
            biblio.setDocumentType(row.getCell(15).getStringCellValue());
            biblio.setPublicationStage(row.getCell(16).getStringCellValue());
            biblio.setOpenAccess(row.getCell(17).getStringCellValue());
            biblio.setSource(row.getCell(18).getStringCellValue());
            biblio.setEID(row.getCell(19).getStringCellValue());

            Biblio updatedBiblio = biblioRepository.findByDOI(biblio.getDOI()).orElse(null);
            if(updatedBiblio != null) {
                updatedBiblio.setRqs(List.of(
                        ResearchQuestion.valueOf(ResearchQuestion.BIG_DATA.name()),
                        ResearchQuestion.valueOf(ResearchQuestion.MACHINE_LEARNING.name())
                ));
                biblioRepository.save(updatedBiblio);
            }else{
                biblio.setRqs(List.of(ResearchQuestion.valueOf(ResearchQuestion.BIG_DATA.name())));
                biblioRepository.save(biblio);
            }
        }
    }

}
