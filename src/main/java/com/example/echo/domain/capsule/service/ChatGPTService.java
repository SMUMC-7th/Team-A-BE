package com.example.echo.domain.capsule.service;

import com.example.echo.domain.capsule.dto.request.ChatGPTMsgDTO;
import com.example.echo.domain.capsule.dto.request.ChatGPTReqDTO;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final ChatGPTConfig chatGPTConfig;
    private final CapsuleRepository capsuleRepository;

    public String generateQuestion(Long capsuleId) {

        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        // AI 요약/질문 기능을 사용할 수 있는, 열람 가능 캡슐 여부 확인
        if (!capsule.isOpened()) { throw new CapsuleException(CapsuleErrorCode.CLOSED_CAPSULE); }

        // System Role 설정 (상의 후 수정 가능, figma 참고함)
        String prompt = """
                "타임캡슐의 내용을 바탕으로 현재의 나에게 질문을 생성해줘.
                질문은 다음과 같은 형식을 따르되, 세부적인 내용은 타임캡슐 내용에 맞게 수정해줘.
                
                1. 시간이 흐른 지금, 어떤 모습으로 살아가고 있나요?
                2. 내가 세웠던 목표들은 이루어졌나요?
                3. 원하는 만큼 성장했고, 건강은 잘 유지하고 있나요?
                4. 혹시 계획대로 되지 않았다면, 그 실패를 어떻게 받아들였나요?
                5. 지금도 어려운 시간을 보내고 있나요, 아니면 새로운 문제에 직면했나요?
                6. 이 글을 읽으며, 과거의 나를 떠올리면 어떤 생각이 드나요?
                """;

        List<ChatGPTMsgDTO> messages = new ArrayList<>();
        messages.add(new ChatGPTMsgDTO("system", prompt)); // System Role(지침)
        messages.add(new ChatGPTMsgDTO("user", capsule.getContent())); // User Role(질문)

        ChatGPTReqDTO requestDTO = new ChatGPTReqDTO("gpt-4", messages);

        // RestTemplate 사용하여 API 호출
        ResponseEntity<String> responseEntity = chatGPTConfig.restTemplate()
                .exchange(
                chatGPTConfig.getApiUrl(),
                HttpMethod.POST,
                new HttpEntity<>(requestDTO, chatGPTConfig.httpHeaders()),
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {

            String result = responseEntity.getBody();
            if (result != null) {
                return result;
            } else {
                throw new ChatGPTException(ChatGPTErrorCode.NULL_RESPONSE);
            }

        } else {
            throw new ChatGPTException(ChatGPTErrorCode.FAILED_TO_CALL_API);
        }
    }
}


