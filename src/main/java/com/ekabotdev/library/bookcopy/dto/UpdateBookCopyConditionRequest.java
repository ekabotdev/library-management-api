package com.ekabotdev.library.bookcopy.dto;

import com.ekabotdev.library.enums.CopyCondition;
import jakarta.validation.constraints.NotNull;

public record UpdateBookCopyConditionRequest
        (
                @NotNull
                CopyCondition conditionStatus
        )
{
}
