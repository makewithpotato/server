package org.dgu.programbook.domain.programbook.service;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.movie.util.S3Util;
import org.dgu.programbook.domain.programbook.dto.request.ProgramBookSaveRequestDTO;
import org.dgu.programbook.domain.programbook.dto.response.ProgramBookPdfResponseDTO;
import org.dgu.programbook.domain.programbook.dto.response.ProgrambookResponseDTO;
import org.dgu.programbook.domain.programbook.entity.Programbook;
import org.dgu.programbook.domain.programbook.repository.ProgrambookRepository;
import org.dgu.programbook.domain.programbook.util.PdfUtil;
import org.dgu.programbook.domain.user.entity.User;
import org.dgu.programbook.domain.user.repository.UserRepository;
import org.dgu.programbook.global.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.dgu.programbook.global.error.ErrorCode.PROGRAMBOOK_NOT_FOUND;
import static org.dgu.programbook.global.error.ErrorCode.USER_NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProgrambookService {

    private final UserRepository userRepository;
    private final ProgrambookRepository programbookRepository;
    private final S3Util s3Util;
    private final PdfUtil pdfUtil;

    public List<ProgrambookResponseDTO> getProgrambookList(Long userId) {
        List<Programbook> programbookList = programbookRepository.findAllByUserId(userId);

        return programbookList.stream()
                .map(ProgrambookResponseDTO::from)
                .toList();
    }

    public ProgramBookPdfResponseDTO getProgrambook(Long programbookId) {
        Programbook programbook = programbookRepository.findById(programbookId)
                .orElseThrow(() -> new EntityNotFoundException(PROGRAMBOOK_NOT_FOUND));
        return ProgramBookPdfResponseDTO.from(programbook);
    }

    @Transactional
    public void saveProgrambook(ProgramBookSaveRequestDTO programBookSaveRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        MultipartFile pdfFile = programBookSaveRequest.pdfFile();
        String pdfUrl = s3Util.upload(pdfFile);

        MultipartFile thumbnail = pdfUtil.extractThumbnail(pdfFile);
        String thumbnailUrl = s3Util.upload(thumbnail);

        Programbook programbook = Programbook.builder()
                .title(programBookSaveRequest.title())
                .pdfUrl(pdfUrl)
                .thumbnailUrl(thumbnailUrl)
                .user(user)
                .build();

        programbookRepository.save(programbook);

    }
}
