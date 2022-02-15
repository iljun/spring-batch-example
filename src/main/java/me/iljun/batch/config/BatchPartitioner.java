package me.iljun.batch.config;

import me.iljun.batch.domain.entity.Account;
import me.iljun.batch.domain.repository.AccountRepository;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchPartitioner implements Partitioner {

    private final AccountRepository accountRepository;
    public BatchPartitioner(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<Account> accounts = accountRepository.findAll();
        Map<String, ExecutionContext> partitionResult = new HashMap<>();

        accounts.stream()
                .forEach(account -> {
                    ExecutionContext executionContext = new ExecutionContext();
                    long key = account.getId() % gridSize;
                    partitionResult.put(String.format("partition%s", key), executionContext);
                });

        return partitionResult;
    }
}
