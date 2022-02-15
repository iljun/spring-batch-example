package me.iljun.batch.config;

import me.iljun.batch.domain.entity.Account;
import me.iljun.batch.domain.repository.AccountRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class Config {

    private static final int THREAD_POOL_SIZE = 5;
    private static final int CHUNK_SIZE = 100;

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final AccountRepository accountRepository;
    public Config(StepBuilderFactory stepBuilderFactory,
                  JobBuilderFactory jobBuilderFactory,
                  AccountRepository accountRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.accountRepository = accountRepository;
    }


    @Bean(name = "partitionHandler")
    public TaskExecutorPartitionHandler partitionHandler() {
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setStep(step());                       // worker로 실행될 Step
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());       // MultiThread로 실행 될 TaskExecutor
        taskExecutorPartitionHandler.setGridSize(THREAD_POOL_SIZE);         // Thread의 개수와 graidSize를 맞추기 위해서
        return taskExecutorPartitionHandler;
    }

    @Bean(name = "taskPool")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(THREAD_POOL_SIZE);
        executor.setMaxPoolSize(THREAD_POOL_SIZE);
        executor.setThreadNamePrefix("partition_thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }

    @Bean(name = "job")
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(stepManager())
                .preventRestart()
                .build();
    }

    @Bean(name = "stepManager")
    public Step stepManager() {
        return stepBuilderFactory.get("stepManager")
                .partitioner("step", batchPartitioner())              // step에 사용될 partitioner
                .step(step())                                                       // 파티셔닝을 하기 위한 Step
                .partitionHandler(partitionHandler())                               // 사용할 partitionHandler
                .build();
    }

    @Bean(name = "step")
    public Step step() {
        return stepBuilderFactory.get("step")
                .<Account, Account>chunk(CHUNK_SIZE)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean(name = "partitioner")
    public BatchPartitioner batchPartitioner() {
        return new BatchPartitioner(accountRepository);
    }

    @Bean(name = "reader")
    public ItemReader<Account> reader() {
        // TODO reader Config
        return null;
    }

    @Bean(name = "writer")
    public ItemWriter<Account> writer() {
        // TODO writer Config
        return null;
    }

}
