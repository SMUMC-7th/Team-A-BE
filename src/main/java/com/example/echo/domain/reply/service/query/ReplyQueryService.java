package com.example.echo.domain.reply.service.query;

import com.example.echo.domain.reply.converter.ReplyConverter;
import com.example.echo.domain.reply.dto.response.ReplyResponseDto;
import com.example.echo.domain.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyQueryService {

    private final ReplyRepository replyRepository;
    private final ReplyConverter replyConverter;

    public List<ReplyResponseDto.ReplyPreviewDto> getReplies(Long capsuleId) {
        return replyRepository.findByCapsule_IdOrderByRefAscRefOrderAsc(capsuleId)
                .stream()
                .map(replyConverter::toPreviewDto)
                .collect(Collectors.toList());
    }
}
