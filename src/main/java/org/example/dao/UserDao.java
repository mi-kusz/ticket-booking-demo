package org.example.dao;

import org.example.dto.UserDto;
import org.example.jooq.generated.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.example.jooq.generated.tables.Users.USERS;

public class UserDao
{
    private final DSLContext dsl;

    public UserDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public Optional<UserDto> findUserById(int userId)
    {
        Record userRecord =  dsl.selectFrom(USERS)
                .where(USERS.USER_ID.eq(userId))
                .fetchOne();

        return Optional.ofNullable(userRecord)
                .map(this::toDto);
    }

    public List<UserDto> findUsersByName(String name)
    {
        return dsl.selectFrom(USERS)
                .where(USERS.NAME.eq(name))
                .fetch()
                .map(this::toDto);
    }

    public Optional<UserDto> findUserByEmail(String email)
    {
        Record userRecord = dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOne();

        return Optional.ofNullable(userRecord)
                .map(this::toDto);
    }

    public List<UserDto> findUserByCreationDatetime(LocalDateTime start, LocalDateTime end)
    {
        return dsl.selectFrom(USERS)
                .where(USERS.CREATED_AT.between(start, end))
                .fetch()
                .map(this::toDto);
    }

    public int addUser(UserDto userDto)
    {
        return dsl.insertInto(USERS, USERS.NAME, USERS.EMAIL, USERS.CREATED_AT)
                .values(userDto.name(), userDto.email(), userDto.createdAt())
                .execute();
    }

    public int modifyUser(UserDto userDto)
    {
        return dsl.update(USERS)
                .set(USERS.NAME, userDto.name())
                .set(USERS.EMAIL, userDto.email())
                .where(USERS.USER_ID.eq(userDto.userId()))
                .execute();
    }

    private UserDto toDto(Record r)
    {
        return UserDto.create(
                r.get(USERS.USER_ID),
                r.get(USERS.NAME),
                r.get(USERS.EMAIL),
                r.get(USERS.CREATED_AT)
        );
    }
}
