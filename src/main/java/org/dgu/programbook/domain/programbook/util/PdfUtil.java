package org.dgu.programbook.domain.programbook.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.dgu.programbook.global.error.exception.BusinessException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.dgu.programbook.global.error.ErrorCode.FILE_UPLOAD_FAILED;

@Component
public class PdfUtil {

    //PDF 파일에서 썸네일 추출
    public MultipartFile extractThumbnail(MultipartFile pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile.getInputStream())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            //PDF 첫 페이지를 PNG 이미지로 렌더링
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 150, ImageType.RGB);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bim, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            return new MockMultipartFile(
                    "thumbnail",
                    "thumbnail.png",
                    "image/png",
                    imageBytes
            );
        } catch (IOException e) {
            throw new BusinessException(FILE_UPLOAD_FAILED);
        }
    }

}
