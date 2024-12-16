package com.example.echo.domain.capsule.service.query;

import com.example.echo.domain.capsule.converter.CapsuleConverter;
import com.example.echo.domain.capsule.dto.response.CapsuleResDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.exception.code.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.handler.CapsuleException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.security.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
@RequiredArgsConstructor
public class CapsuleQueryService {
    private final CapsuleRepository capsuleRepository;

    public List<CapsuleResDTO.CapsulePreviewResDTO> getCapsules(AuthUser authUser){
        List<Capsule> capsules = capsuleRepository.findByUserIdAndDeletedAtIsNullOrderByDeadLineAsc(authUser.getId());
        capsules.forEach(capsule -> capsule.setIsOpened());
        capsuleRepository.saveAll(capsules);
        return CapsuleConverter.fromList(capsules, authUser);
    }

    public CapsuleResDTO.CapsuleDetailResDTO getCapsule(Long capsuleId, AuthUser authUser){
        Capsule capsule = capsuleRepository.findByIdAndDeletedAtIsNull(capsuleId)
                .orElseThrow(() -> new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        return CapsuleConverter.toCapsuleDetailDto(capsule, authUser);
    }

    public Slice<Capsule> getCapsulesWithPagination(AuthUser authUser, Long cursor, Integer offset) {
        Pageable pageable = PageRequest.of(0, offset);
        Slice<Capsule> capsules;

        if(!capsuleRepository.existsByUserIdAndDeletedAtIsNull(authUser.getId())){
            throw new CapsuleException(CapsuleErrorCode.NOT_FOUND);
        }

        if (cursor.equals(0L)) {
            // cursor가 0일 경우, deadline 기준으로 정렬된 결과 반환
            capsules = capsuleRepository.findByUserIdAndDeletedAtIsNullOrderByDeadLineAsc(authUser.getId(), pageable);
        } else {
            Capsule capsule = capsuleRepository.findById(cursor).orElseThrow(()-> new CapsuleException(CapsuleErrorCode.NOT_FOUND));
            // cursor가 0이 아닐 경우, cursorValue와 deadline 기준으로 정렬된 결과 반환
            capsules = capsuleRepository.findByUserIdAndDeletedAtIsNullAndCursorUsingLPAD(authUser.getId(), cursor, capsule.getDeadLine(), pageable);
        }

        // setIsOpened 호출
        capsules.forEach(Capsule::setIsOpened);
        capsuleRepository.saveAll(capsules.getContent());

        // DTO 변환
        return capsules;
    }

}
