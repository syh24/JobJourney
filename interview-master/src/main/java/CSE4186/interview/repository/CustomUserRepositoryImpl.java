package CSE4186.interview.repository;

import CSE4186.interview.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import static CSE4186.interview.entity.QUser.user;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findByNameAndPassword(String name, String password) {
        User matchingUser=jpaQueryFactory
                .selectFrom(user)
                .where(user.name.eq(name)
                        .and(user.password.eq(password)))
                .fetchOne();
        return Optional.ofNullable(matchingUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User matchingUser=jpaQueryFactory
                .selectFrom(user)
                .where(user.email.eq(email))
                .fetchOne();
        return Optional.ofNullable(matchingUser);
    }
}
