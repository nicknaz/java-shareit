package ru.practicum.shareit.user.dto;

import lombok.*;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private long id;

    private String name;

    private String email;
}