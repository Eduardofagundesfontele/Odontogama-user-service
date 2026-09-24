package com.odontogama.auth.infrastructure.persistence.repository;

import com.odontogama.auth.domain.model.User;
import com.odontogama.auth.domain.repository.UserRepository;
import com.odontogama.auth.infrastructure.persistence.entity.UserEntity;
import com.odontogama.auth.infrastructure.persistence.mapper.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceMapper mapper;
    private final UserJpaRepository repositoryJpa;

    @Override
    public User save(User user) {
        UserEntity userEntity = mapper.toEntity(user);

        UserEntity savedEntity = repositoryJpa.save(userEntity);

      return mapper.toDomain(userEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repositoryJpa.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return repositoryJpa.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repositoryJpa.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repositoryJpa.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {

        repositoryJpa.deleteById(id);
    }
}
