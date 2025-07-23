package dao;

import org.example.dao.UserDao;
import org.example.dto.UserDto;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Users.USERS;
import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest
{
    private DSLContext dslFor(MockDataProvider provider)
    {
        return DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
    }

    private UserDto testUser()
    {
        return UserDto.create(1, "Test name", "TestEmail@test.com", LocalDateTime.now());
    }

    private void assertEqualUser(UserDto expected, UserDto actual)
    {
        assertEquals(expected.userId(), actual.userId());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.email(), actual.email());
        assertEquals(expected.createdAt(), actual.createdAt());
    }

    @Test
    public void testFindUsers()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());

            record.set(USERS.USER_ID, user.userId());
            record.set(USERS.NAME, user.name());
            record.set(USERS.EMAIL, user.email());
            record.set(USERS.CREATED_AT, user.createdAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        List<UserDto> resultList = userDao.findUsers();

        assertEquals(1, resultList.size());

        UserDto result = resultList.getFirst();

        assertEqualUser(user, result);
    }

    @Test
    public void testFindUserById()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());

            record.set(USERS.USER_ID, user.userId());
            record.set(USERS.NAME, user.name());
            record.set(USERS.EMAIL, user.email());
            record.set(USERS.CREATED_AT, user.createdAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        Optional<UserDto> optionalResult = userDao.findUserById(user.userId());

        assertTrue(optionalResult.isPresent());

        UserDto result = optionalResult.get();

        assertEqualUser(user, result);
    }

    @Test
    public void testFindNonExistingUserById()
    {
        MockDataProvider dataProvider = ctx ->
        {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[]{new MockResult(0, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        Optional<UserDto> optionalResult = userDao.findUserById(1);

        assertFalse(optionalResult.isPresent());
    }

    @Test
    public void testFindUserByName()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());

            record.set(USERS.USER_ID, user.userId());
            record.set(USERS.NAME, user.name());
            record.set(USERS.EMAIL, user.email());
            record.set(USERS.CREATED_AT, user.createdAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        List<UserDto> resultList = userDao.findUsersByName(user.name());

        assertEquals(1, resultList.size());

        UserDto result = resultList.getFirst();

        assertEqualUser(user, result);
    }

    @Test
    public void testFindNonExistingUserByName()
    {
        MockDataProvider dataProvider = ctx ->
        {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[]{new MockResult(0, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        List<UserDto> resultList = userDao.findUsersByName("Test name");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testFindMultipleUsersByName()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());

            record1.set(USERS.USER_ID, user.userId());
            record1.set(USERS.NAME, user.name());
            record1.set(USERS.EMAIL, user.email());
            record1.set(USERS.CREATED_AT, user.createdAt());

            record2.set(USERS.USER_ID, 2);
            record2.set(USERS.NAME, user.name());
            record2.set(USERS.EMAIL, "abc@abc.com");
            record2.set(USERS.CREATED_AT, user.createdAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        List<UserDto> resultList = userDao.findUsersByName(user.name());

        assertEquals(2, resultList.size());
    }

    @Test
    public void testFindUserByEmail()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());

            record.set(USERS.USER_ID, user.userId());
            record.set(USERS.NAME, user.name());
            record.set(USERS.EMAIL, user.email());
            record.set(USERS.CREATED_AT, user.createdAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        Optional<UserDto> optionalResult = userDao.findUserByEmail(user.email());

        assertTrue(optionalResult.isPresent());

        UserDto result = optionalResult.get();

        assertEqualUser(user, result);
    }

    @Test
    public void testFindNonExistingUserByEmail()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        Optional<UserDto> optionalResult = userDao.findUserByEmail("abc@test.com");

        assertFalse(optionalResult.isPresent());
    }

    @Test
    public void testFindUserByCreationDate()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());

            record.set(USERS.USER_ID, user.userId());
            record.set(USERS.NAME, user.name());
            record.set(USERS.EMAIL, user.email());
            record.set(USERS.CREATED_AT, user.createdAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        List<UserDto> resultList = userDao.findUserByCreationDatetime(user.createdAt().minusDays(1), user.createdAt().plusDays(1));

        assertEquals(1, resultList.size());

        UserDto result = resultList.getFirst();

        assertEqualUser(user, result);
    }

    @Test
    public void testFindNonExistingUserByCreationDate()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        List<UserDto> resultList = userDao.findUserByCreationDatetime(user.createdAt().plusDays(1), user.createdAt().plusDays(1));

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testFindMultipleUsersByCreationDate()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());

            record1.set(USERS.USER_ID, user.userId());
            record1.set(USERS.NAME, user.name());
            record1.set(USERS.EMAIL, user.email());
            record1.set(USERS.CREATED_AT, user.createdAt());

            record2.set(USERS.USER_ID, 5);
            record2.set(USERS.NAME, user.name());
            record2.set(USERS.EMAIL, "test@test.com");
            record2.set(USERS.CREATED_AT, user.createdAt().plusHours(10));

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        UserDao userDao = new UserDao(dslFor(dataProvider));
        List<UserDto> resultList = userDao.findUserByCreationDatetime(user.createdAt().plusDays(1), user.createdAt().plusDays(1));

        assertEquals(2, resultList.size());
    }

    @Test
    public void testAddUser()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());

            record.set(USERS.USER_ID, user.userId());
            record.set(USERS.NAME, user.name());
            record.set(USERS.EMAIL, user.email());
            record.set(USERS.CREATED_AT, user.createdAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        UserDao dao = new UserDao(dslFor(dataProvider));
        Optional<UserDto> result = dao.addUser(user);

        assertTrue(result.isPresent());
    }

    @Test void testAddUserError()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());

            return new MockResult[] {new MockResult(0, result)};
        };

        UserDao dao = new UserDao(dslFor(dataProvider));
        Optional<UserDto> result = dao.addUser(user);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testModifyUser()
    {
        UserDto user = testUser();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(USERS.fields());

            record.set(USERS.USER_ID, user.userId());
            record.set(USERS.NAME, user.name());
            record.set(USERS.EMAIL, user.email());
            record.set(USERS.CREATED_AT, user.createdAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(USERS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        UserDao dao = new UserDao(dslFor(dataProvider));
        Optional<UserDto> result = dao.modifyUser(user);

        assertTrue(result.isPresent());
    }
}
