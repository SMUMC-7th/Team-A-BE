package com.example.echo.domain.capsule.controller;

import com.example.echo.domain.capsule.dto.request.CapsuleReqDTO;
import com.example.echo.domain.capsule.dto.response.CapsuleResDTO;
import com.example.echo.domain.capsule.service.ChatGPTService;
import com.example.echo.domain.capsule.service.command.CapsuleCommandService;
import com.example.echo.domain.capsule.service.query.CapsuleQueryService;
import com.example.echo.domain.security.annotation.CurrentUser;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timecapsules")
@Tag(name = "타입캡슐 API")
public class CapsuleController {

    private final CapsuleCommandService capsuleCommandService;
    private final CapsuleQueryService capsuleQueryService;
    private final ChatGPTService chatGPTService;

    @PostMapping("")
    @Operation(method = "POST", summary = "타임캡슐 생성 API", description = "타임캡슐을 생성하는 API입니다.")
    public CustomResponse<CapsuleResDTO.CapsuleResponseDTO> createCapsule(
            @RequestBody CapsuleReqDTO.CreateCapsuleReqDTO dto,
            @CurrentUser AuthUser authUser){

        CapsuleResDTO.CapsuleResponseDTO result = capsuleCommandService.createCapsule(dto, authUser);
        return CustomResponse.onSuccess(HttpStatus.CREATED, result);
    }

    @DeleteMapping("/{timecapsuleId}")
    @Operation(method = "DELETE", summary = "타임캡슐 삭제 API", description = "타임캡슐을 삭제하는 API입니다.")
    public CustomResponse<?> deleteCapsule(
            @PathVariable("timecapsuleId") Long timecapsuleId,
            @CurrentUser AuthUser authUser
    ){


        capsuleCommandService.deleteCapsule(timecapsuleId);
        return CustomResponse.onSuccess(HttpStatus.NO_CONTENT, "성공적으로 타임캡슐이 삭제되었습니다.");
    }

    @PatchMapping("/{timecapsuleId}")
    @Operation(method = "PATCH", summary = "타임캡슐 복원 API", description = "타임캡슐을 복원하는 API입니다.")
    public CustomResponse<?> restoreCapsule(
            @PathVariable("timecapsuleId") Long timecapsuleId
    ){
        capsuleCommandService.restoreCapsule(timecapsuleId);
        return CustomResponse.onSuccess(HttpStatus.OK, "성공적으로 타임캡슐이 복원되었습니다.");
    }

    @GetMapping("")
    @Operation(method = "GET", summary = "타임캡슐 조회 API", description = "특정 유저의 타임캡슐을 조회하는 API입니다.")
    public CustomResponse<List<CapsuleResDTO.CapsulePreviewResDTO>> getCapsules(@CurrentUser AuthUser authUser){
        List<CapsuleResDTO.CapsulePreviewResDTO> result = capsuleQueryService.getCapsules(authUser);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/{timecapsuleId}")
    @Operation(method = "GET", summary = "타임캡슐 상세 조회 API", description = "1개 타임캡슐 상세 조회하는 API입니다.")
    public CustomResponse<CapsuleResDTO.CapsuleDetailResDTO> getCapsule(
            @PathVariable("timecapsuleId") Long timecapsuleId,
            @CurrentUser AuthUser authUser){
        CapsuleResDTO.CapsuleDetailResDTO result = capsuleQueryService.getCapsule(timecapsuleId,authUser);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @PostMapping("/{timecapsuleId}/ai")
    @Operation(summary = "타임캡슐 내용 기반 AI 질문 생성 API")
    public Mono<CustomResponse<String>> generateQuestion(@PathVariable("timecapsuleId") Long capsuleId) {
        return chatGPTService.generateQuestion(capsuleId);
    }
}
