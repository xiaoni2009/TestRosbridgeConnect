package cn.muye;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.batch.ScheduledHandle;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
@EnableScheduling
@EnableTransactionManagement
@MapperScan("cn.muye.**.mapper")
public class Application {
	private static Logger logger = Logger.getLogger(Application.class);

	@Value("${sub.name}")
	private String subName;

	@Value("${local.robot.SN}")
	private String localRobotSN;

	@Value("${lsub.name}")
	private String lSubName;

	@Value(TopicConstants.TOPIC_HEARTBEAT_COMMAND)
	private String topicCommandAndHeartbeatSN;

	@Value("${local.robot.fileCachePath}")
	private String fileCachePath;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return new org.apache.tomcat.jdbc.pool.DataSource();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {

		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		//分页插件
		PageHelper pageHelper = new PageHelper();
		Properties props = new Properties();
		props.setProperty("reasonable", "true");
		props.setProperty("supportMethodsArguments", "true");
		props.setProperty("returnPageInfo", "check");
		props.setProperty("params", "count=countSql");
		pageHelper.setProperties(props);
		//添加插件
		sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis*//**//*.xml"));

		return sqlSessionFactoryBean.getObject();

	}

	@Bean
	public String subName(){
		String[] arraySubName = subName.split(",");
		for(String subName : arraySubName){
			CacheInfoManager.setNameSubCache(subName);
		}
		return subName;
	}

	@Bean
	public String lSubName(){
		String[] arraySubName = lSubName.split(",");
		for(String subName : arraySubName){
			CacheInfoManager.setNameLSubCache(subName);
		}
		return lSubName;
	}

	@Bean
	public String localRobotSN(){
		return localRobotSN;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public ScheduledExecutorService scheduledHandle() {
		ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(15, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Application scheduledHandle");
			}
		});
		new ScheduledHandle(scheduledExecutor, topicCommandAndHeartbeatSN);
		return scheduledExecutor;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		// Do any additional configuration here
		return builder.build();
	}

	/**
	 * Start
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		logger.info("SpringBoot Start Success");
	}

}
