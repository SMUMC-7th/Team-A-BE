package com.example.echo.domain.capsule.service;

import com.example.echo.domain.capsule.dto.request.ChatGPTRequestDTO;
import com.example.echo.domain.capsule.dto.response.ChatGPTResponseDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.exception.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.CapsuleException;
import com.example.echo.domain.capsule.exception.ChatGPTErrorCode;
import com.example.echo.domain.capsule.exception.ChatGPTException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.security.config.ChatGPTConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final ChatGPTConfig chatGPTConfig;
    private final CapsuleRepository capsuleRepository;

    public ChatGPTResponseDTO generateQuestion(Long capsuleId) {

        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        // AI 요약/질문 기능을 사용할 수 있는, 열람 가능한 캡슐인지 확인
        if (!capsule.isOpened()) { throw new CapsuleException(CapsuleErrorCode.CLOSED_TAG); }
        String capsuleContent = capsule.getContent();

        // 임시 질문. 수정 예정
        String prompt = capsuleContent + "\n\n위 텍스트를 바탕으로, 원하는 바를 이루었는지와 같은, 스스로를 돌아볼 수 있는 질문 5가지를 생성해줘.";
        ChatGPTRequestDTO requestDTO = new ChatGPTRequestDTO(prompt);

        // RestTemplate 사용하여 API 호출
        ResponseEntity<ChatGPTResponseDTO> responseEntity = chatGPTConfig.restTemplate()
                .exchange(
                chatGPTConfig.getApiUrl(),
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, chatGPTConfig.httpHeaders()),
                ChatGPTResponseDTO.class
        );

        // 성공적인 응답인지 체크
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new ChatGPTException(ChatGPTErrorCode.FAILED_TO_CALL_API);
        }
    }
}


