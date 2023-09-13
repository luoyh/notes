
## spring 定时任务资源回收,日志追踪,多实例部署加锁

```
// ReleasedTask
@Slf4j
public class ReleasedTask implements Runnable {

    private Runnable delegate;    // 原始任务
    private KeyLocked keyLocked;  // 基于键的分布式锁
    private final String id;      // id, 用于加锁

    public ReleasedTask(Runnable delegate, KeyLocked keyLocked) {
        this.delegate = delegate;
        this.keyLocked = keyLocked;
        this.id = "tl::" + delegate.toString();
        log.info("new timer task: {}", id);
    }

    @Override
    public void run() {
        try (LogTracer lt = LogTracer.i()) { // 日志追踪
            try (KeyLocked.Lock lock = keyLocked.get(id)) { 
                if (!lock.locked()) { // 未获取锁
                    return;
                }

                log.info("start schedule: {}", id);
                try {
                    delegate.run();
                } catch (Throwable ex) {
                    log.error("releasd task error", ex);
                    throw ex;
                }
                Releaser.releaseAll(); // 释放资源, 可放在LogTracer里面处理
                log.info("end schedule: {}", id);
            }
        }
    }
}

# ReleasedTaskScheduler
public class ReleasedTaskSchduler extends ThreadPoolTaskScheduler {

    private static final long serialVersionUID = 1L;
    private KeyLocked keyLocked;

    public ReleasedTaskSchduler(KeyLocked keyLocked) {
        super();
        this.keyLocked = keyLocked;
    }

    @Override
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return super.schedule(new ReleasedTask(task, keyLocked), trigger);
    }

}

# conifg
@Bean
public TaskScheduler taskScheduler(KeyLocked keyLocked) {
    ThreadPoolTaskScheduler taskScheduler = new ReleasedTaskSchduler(keyLocked);
    taskScheduler.setPoolSize(10);
    taskScheduler.setAwaitTerminationSeconds(60);
    taskScheduler.setThreadNamePrefix("ofllibnnb-timer-");
    taskScheduler.initialize();
    return taskScheduler;
}
```