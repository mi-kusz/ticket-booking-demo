package org.example.dao;

import org.example.dto.UserDto;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Users.USERS;

public class UserDao
{
    private static final Logger log = LoggerFactory.getLogger(UserDao.class);
    private final DSLContext dsl;

    public UserDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public List<UserDto> findUsers()
    {
        log.info("Fetching all users");

        return dsl.selectFrom(USERS)
                .fetch()
                .map(this::toDto);
    }

    public Optional<UserDto> findUserById(int userId)
    {
        log.info("Fetching user with id: {}", userId);

        Record userRecord =  dsl.selectFrom(USERS)
                .where(USERS.USER_ID.eq(userId))
                .fetchOne();

        return Optional.ofNullable(userRecord)
                .map(this::toDto);
    }

    public List<UserDto> findUsersByName(String name)
    {
        log.info("Fetching users with name: {}", name);

        return dsl.selectFrom(USERS)
                .where(USERS.NAME.eq(name))
                .fetch()
                .map(this::toDto);
    }

    public Optional<UserDto> findUserByEmail(String email)
    {
        log.info("Fetching user with email: {}", email);

        Record userRecord = dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOne();

        return Optional.ofNullable(userRecord)
                .map(this::toDto);
    }

    public List<UserDto> findUserByCreationDatetime(LocalDateTime start, LocalDateTime end)
    {
        log.info("Fetching users with creation date between {} and {}", start, end);

        return dsl.selectFrom(USERS)
                .where(USERS.CREATED_AT.between(start, end))
                .fetch()
                .map(this::toDto);
    }

    public int addUser(UserDto userDto)
    {
        log.info("Adding user");

        try
        {
            return dsl.insertInto(USERS, USERS.NAME, USERS.EMAIL, USERS.CREATED_AT)
                    .values(userDto.name(), userDto.email(), userDto.createdAt())
                    .execute();
        }
        catch (DataAccessException e)
        {
            log.error("Cannot add user", e);
            return 0;
        }
    }

    public int modifyUser(UserDto userDto)
    {
        log.info("Modifying user with id: {}", userDto.userId());

        try
        {
            return dsl.update(USERS)
                    .set(USERS.NAME, userDto.name())
                    .set(USERS.EMAIL, userDto.email())
                    .where(USERS.USER_ID.eq(userDto.userId()))
                    .execute();
        }
        catch (DataAccessException e)
        {
            log.error("Cannot modify user", e);
            return 0;
        }
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
