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
        long maxId = accountRepository.findMaxId();
        long size = maxId / gridSize + 1;

        Map<String, ExecutionContext> partitionResult = new HashMap<>();

        long cursor = 0;
        long start = 0;
        long end = start + size - 1;

        while (start <= end) {
            ExecutionContext executionContext = new ExecutionContext();
            partitionResult.put("partition" + cursor, executionContext);

            if (end >= maxId) {
                end = maxId;
            }

            start += size;
            end += size;
            cursor++;
        }

        return partitionResult;
    }
}
