package com.example.messagebackend.logs;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisLogRepository extends CrudRepository<RedisLog, String> {
}
