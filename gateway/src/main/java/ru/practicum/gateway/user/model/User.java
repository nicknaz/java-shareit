package ru.practicum.gateway.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @PositiveOrZero
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}
