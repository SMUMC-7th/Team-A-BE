package com.example.echo.domain.capsule.controller;

import com.example.echo.domain.capsule.converter.CapsuleConverter;
import com.example.echo.domain.capsule.dto.request.CapsuleReqDTO;
import com.example.echo.domain.capsule.dto.response.CapsuleResDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.service.AwsS3Service;
import com.example.echo.domain.capsule.service.ChatGPTService;
import com.example.echo.domain.capsule.service.command.CapsuleCommandService;
import com.example.echo.domain.capsule.service.query.CapsuleQueryService;
import com.example.echo.domain.security.annotation.CurrentUser;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    private final AwsS3Service awsS3Service;

    @PostMapping("")
    @Operation(method = "POST", summary = "타임캡슐 생성 API", description = "타임캡슐을 생성하는 API입니다.")
    public CustomResponse<CapsuleResDTO.CapsuleResponseDTO> createCapsule(
            @RequestBody CapsuleReqDTO.CreateCapsuleReqDTO dto,
            @CurrentUser AuthUser authUser) {

        CapsuleResDTO.CapsuleResponseDTO result = capsuleCommandService.createCapsule(dto, authUser);
        return CustomResponse.onSuccess(HttpStatus.CREATED, result);
    }

    @DeleteMapping("/{timecapsuleId}")
    @Operation(method = "DELETE", summary = "타임캡슐 삭제 API", description = "타임캡슐을 삭제하는 API입니다.")
    public CustomResponse<?> deleteCapsule(
            @PathVariable("timecapsuleId") Long timecapsuleId,
            @CurrentUser AuthUser authUser
    ) {


        capsuleCommandService.deleteCapsule(timecapsuleId);
        return CustomResponse.onSuccess(HttpStatus.NO_CONTENT, "성공적으로 타임캡슐이 삭제되었습니다.");
    }

    @PatchMapping("/{timecapsuleId}")
    @Operation(method = "PATCH", summary = "타임캡슐 복원 API", description = "타임캡슐을 복원하는 API입니다.")
    public CustomResponse<?> restoreCapsule(
            @PathVariable("timecapsuleId") Long timecapsuleId
    ) {
        capsuleCommandService.restoreCapsule(timecapsuleId);
        return CustomResponse.onSuccess(HttpStatus.OK, "성공적으로 타임캡슐이 복원되었습니다.");
    }

    @GetMapping("")
    @Operation(method = "GET", summary = "타임캡슐 조회 API", description = "특정 유저의 타임캡슐을 조회하는 API입니다.")
    public CustomResponse<List<CapsuleResDTO.CapsulePreviewResDTO>> getCapsules(@CurrentUser AuthUser authUser) {
        List<CapsuleResDTO.CapsulePreviewResDTO> result = capsuleQueryService.getCapsules(authUser);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    //커서 기반 페이지네이션
    @GetMapping("/page")
    @Operation(method = "GET", summary = "커서 기반 페이지네이션 API", description = "타임캡슐 커서 기반 페이지네이션 API입니다.")
    @Parameters({
            @Parameter(name = "cursor", description = "커서 값, 처음이면 0"),
            @Parameter(name = "query", description = "쿼리 ID")
    })
    public CustomResponse<CapsuleResDTO.CapsulePagePreviewDTO> getArticlesByCursor(
            @CurrentUser AuthUser authUser,
            @RequestParam(value = "query", defaultValue = "ID") String query,
            @RequestParam("cursor") Long cursor,
            @RequestParam(value = "offset", defaultValue = "8") Integer offset
    ){
        Slice<Capsule> capsules = capsuleQueryService.getCapsulesWithPagination(authUser,cursor, offset);
        CapsuleResDTO.CapsulePagePreviewDTO result = CapsuleConverter.tocapsulePageDTO(capsules, authUser);
        return CustomResponse.onSuccess(result);
    }

    @GetMapping("/{timecapsuleId}")
    @Operation(method = "GET", summary = "타임캡슐 상세 조회 API", description = "1개 타임캡슐 상세 조회하는 API입니다.")
    public CustomResponse<CapsuleResDTO.CapsuleDetailResDTO> getCapsule(
            @PathVariable("timecapsuleId") Long timecapsuleId,
            @CurrentUser AuthUser authUser) {
        CapsuleResDTO.CapsuleDetailResDTO result = capsuleQueryService.getCapsule(timecapsuleId, authUser);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @PostMapping("/{timecapsuleId}/ai")
    @Operation(summary = "타임캡슐 내용 기반 AI 질문 생성 API")
    public Mono<CustomResponse<String>> generateQuestion(@PathVariable("timecapsuleId") Long capsuleId) {

        return chatGPTService.generateQuestion(capsuleId);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "AWS S3 이미지 업로드 API", description = "S3와 DB 모두에 이미지를 생성합니다. 반환값은 DB에 생성된 이미지의 아이디입니다.")
    public CustomResponse<Long> uploadImage(@RequestPart(value = "uploadFiles", required = false) MultipartFile multipartFile) {

        return CustomResponse.onSuccess(awsS3Service.uploadImage(multipartFile));
    }

    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "AWS S3 이미지 삭제 API", description = "S3와 DB 모두에서 이미지를 삭제합니다.")
    public CustomResponse<String> deleteImageFromCapsule(@PathVariable Long imageId) {

        awsS3Service.deleteImage(imageId);
        return CustomResponse.onSuccess("이미지가 성공적으로 삭제되었습니다.");
    }

    @DeleteMapping("/images/delete-orphan")
    @Operation(summary = "S3 업로드 후 24시간이 지나도 캡슐이 할당되지 않은 이미지 삭제 API", description = "로직 테스트용 API입니다. 실제로 사용되지 x")
    public CustomResponse<String> deleteOrphanedImages() {

        return CustomResponse.onSuccess(awsS3Service.deleteOrphanedImages());
    }
}
