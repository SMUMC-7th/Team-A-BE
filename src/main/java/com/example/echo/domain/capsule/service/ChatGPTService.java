package com.example.echo.domain.capsule.service;

import com.example.echo.domain.capsule.dto.request.ChatGPTMsgDTO;
import com.example.echo.domain.capsule.dto.request.ChatGPTReqDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.exception.code.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.code.ChatGPTErrorCode;
import com.example.echo.domain.capsule.exception.handler.CapsuleException;
import com.example.echo.domain.capsule.exception.handler.ChatGPTException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.global.config.ChatGPTConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final ChatGPTConfig chatGPTConfig;
    private final CapsuleRepository capsuleRepository;

    public String generateQuestion(Long capsuleId) {

        // 캡슐 조회 후 열람 가능 여부 확인
        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        if (!capsule.isOpened()) { throw new CapsuleException(CapsuleErrorCode.CLOSED_CAPSULE); }

        // System Role 설정 (상의 후 수정 가능, figma 참고함)
        String prompt = """
                타임캡슐의 내용을 바탕으로 현재의 나에게 질문을 생성해줘.
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

        try {

            String result = chatGPTConfig.webClient()
                    .post()
                    .uri(chatGPTConfig.getApiUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDTO)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        throw new ChatGPTException(ChatGPTErrorCode.FAILED_TO_CALL_API);
                    })
                    .bodyToMono(String.class)
                    // 동기적으로 구현
                    .block();

            if (result != null && !result.isEmpty()) {

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(result);
                return jsonNode.path("choices").get(0).path("message").path("content").asText();

            } else {
                throw new ChatGPTException(ChatGPTErrorCode.NULL_RESPONSE);
            }
        } catch (JsonProcessingException e) {
            throw new ChatGPTException(ChatGPTErrorCode.FAIL_TO_PARSE);
        } catch (Exception e) {
            throw new ChatGPTException(ChatGPTErrorCode.FAILED_TO_CALL_API);
        }
    }
}
