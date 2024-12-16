package com.example.echo.domain.reply.service.command;

import com.example.echo.domain.reply.converter.ReplyConverter;
import com.example.echo.domain.reply.dto.request.ReplyRequestDto;
import com.example.echo.domain.reply.dto.response.ReplyResponseDto;
import com.example.echo.domain.reply.entity.Reply;
import com.example.echo.domain.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyCommandService {

    private final ReplyRepository replyRepository;
    private final ReplyConverter replyConverter;

    /**
     * 댓글 작성 (원댓글 또는 대댓글)
     */
    public ReplyResponseDto.ReplyPreviewDto addReply(ReplyRequestDto.CreateReplyReqDto requestDto) {
        if (requestDto.parentId() == null) {
            return saveOriginalReply(requestDto);
        } else {
            return saveChildReply(requestDto);
        }
    }

    /**
     * 원댓글 저장
     */
    private ReplyResponseDto.ReplyPreviewDto saveOriginalReply(ReplyRequestDto.CreateReplyReqDto requestDto) {
        // 같은 게시글 내 refOrder의 최대값 조회
        Long maxRefOrder = replyRepository.findMaxRefOrderByRef(null);
        Long newRefOrder = (maxRefOrder == null) ? 1L : maxRefOrder + 1;

        // 댓글 생성 (ref는 일단 null)
        Reply reply = replyConverter.toEntity(requestDto, null, null, newRefOrder, 0L);
        replyRepository.save(reply);

        // 저장된 댓글의 ID를 REF로 업데이트
        reply = reply.toBuilder()
                .ref(reply.getId()) // ref = 자기 자신의 id
                .build();
        replyRepository.save(reply);

        return replyConverter.toPreviewDto(reply);
    }

    /**
     * 대댓글 저장
     */
    private ReplyResponseDto.ReplyPreviewDto saveChildReply(ReplyRequestDto.CreateReplyReqDto requestDto) {
        // 부모 댓글 조회
        Reply parent = replyRepository.findById(requestDto.parentId())
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

        // 부모 refOrder 확인
        System.out.println("부모 refOrder: " + parent.getRefOrder());

        // 부모 refOrder 이후의 댓글들을 밀어준다
        replyRepository.incrementRefOrderAfter(parent.getRef(), parent.getRefOrder());

        // 로그 확인
        System.out.println("incrementRefOrderAfter 실행 완료!");

        // 대댓글 생성
        Reply reply = replyConverter.toEntity(requestDto, parent, parent.getRef(),
                parent.getRefOrder() + 1, parent.getStep() + 1);
        replyRepository.save(reply);

        System.out.println("대댓글 refOrder: " + reply.getRefOrder());

        return replyConverter.toPreviewDto(reply);
    }
}
