package ru.practicum.server.request.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private String description;
}
