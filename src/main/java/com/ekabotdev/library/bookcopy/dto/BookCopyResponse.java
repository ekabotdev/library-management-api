package com.ekabotdev.library.bookcopy.dto;

import com.ekabotdev.library.enums.CopyCondition;
import com.ekabotdev.library.enums.CopyStatus;

public record BookCopyResponse(
        Long id,
        Long bookId,
        CopyStatus status,
        CopyCondition conditionStatus
) {
}
