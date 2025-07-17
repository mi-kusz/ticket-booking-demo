package org.example.dto;

import com.google.auto.value.AutoValue;

import java.time.LocalDateTime;

@AutoValue
public abstract class UserDto
{
    public abstract int userId();
    public abstract String name();
    public abstract String email();
    public abstract LocalDateTime createdAt();

    public static UserDto create(int userId, String name, String email, LocalDateTime createdAt)
    {
        return new AutoValue_UserDto(userId, name, email, createdAt);
    }
}
