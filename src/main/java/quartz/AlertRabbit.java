package quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import java.io.InputStream;
import java.sql.DriverManager;
import java.util.Properties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class AlertRabbit {
    private static Connection connection;

    private static void init() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("db/liquibase.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            init();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("storeDB", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder time = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(getInterval().getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .withSchedule(time)
                    .startNow()
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(Integer.parseInt(getInterval().getProperty("rabbit.sleep_interval")));
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Rabbit implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection con = (Connection) context.getJobDetail().getJobDataMap().get("storeDB");
            try (PreparedStatement statement = con.prepareStatement("insert into rabbits(created) values (?)")) {
                statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                System.out.println(statement);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
