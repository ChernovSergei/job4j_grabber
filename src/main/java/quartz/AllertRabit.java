package quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import java.io.InputStream;
import java.util.Properties;

public class AllertRabit {
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder time = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(getInterval().getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .withSchedule(time)
                    .startNow()
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
        }
    }

    private static Properties getInterval() {
        Properties result = null;
        try (InputStream in = SimpleScheduleBuilder.class.getClassLoader().getResourceAsStream("./rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            result = config;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }
}
