package kg.apc.jmeter.config.redis;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.beans.PropertyDescriptor;

/**
 * @author 林杰炜 linjiewei
 * @Title:TODO 类描述
 * @Description:TODO 详细描述
 * @date: 2018/4/25 23:06
 */
public class RedisPostProcessorBeanInfo extends BeanInfoSupport{

    private static final Logger LOGGER = LoggingManager.getLoggerForClass();

    private static final String VARIABLE_NAMES = "variableNames";
    private static final String DELIMITER = "delimiter";
    private static final String REDIS_KEY = "redisKey";
    private static final String PARAMETER = "parameter";
    private static final String GET_MODE = "getMode";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String TIMEOUT = "timeout";
    private static final String PASSWORD = "password";
    private static final String DATABASE = "database";

    private static final String MAX_ACTIVE = "maxActive";
    private static final String MAX_IDLE = "maxIdle";
    private static final String MAX_WAIT = "maxWait";
    private static final String MIN_IDLE = "minIdle";
    private static final String WHEN_EXHAUSTED_ACTION = "whenExhaustedAction";
    private static final String TEST_WHILE_IDLE = "testWhileIdle";
    private static final String TEST_ON_BORROW = "testOnBorrow";
    private static final String TEST_ON_RETURN = "testOnReturn";
    private static final String TIME_BETWEEN_EVICTION_RUNS_MS = "timeBetweenEvictionRunsMillis";
    private static final String SOFT_MIN_EVICTABLE_IDLE_TIME_MS = "softMinEvictableIdleTimeMillis";
    private static final String NUM_TEST_PER_EVICTION_RUN = "numTestsPerEvictionRun";
    private static final String MIN_EVICTABLE_IDLE_TIME_MS = "minEvictableIdleTimeMillis";

    public RedisPostProcessorBeanInfo(){
        super(RedisPostProcessor.class);
        try {
            createPropertyGroup("redis_data",
                    new String[] { REDIS_KEY, VARIABLE_NAMES, DELIMITER, GET_MODE, PARAMETER });

            PropertyDescriptor p = property(REDIS_KEY);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(VARIABLE_NAMES);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(DELIMITER);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, ",");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            //        p = property(GET_MODE, TypeEditor.ComboStringEditor);
            //        p.setValue(RESOURCE_BUNDLE, getBeanDescriptor().getValue(RESOURCE_BUNDLE));
            //        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            //        p.setValue(DEFAULT, GET_MODE_TAGS[GET_MODE_REMOVE]);
            //        p.setValue(NOT_OTHER, Boolean.FALSE);
            //        p.setValue(NOT_EXPRESSION, Boolean.FALSE);
            //        p.setValue(TAGS, GET_MODE_TAGS);

            p = property(GET_MODE, RedisPostProcessor.GetMode.class);
            p.setValue(DEFAULT, RedisPostProcessor.GetMode.RANDOM_HGETALL.ordinal());
            p.setValue(NOT_UNDEFINED, Boolean.TRUE); // must be defined

            p = property(PARAMETER);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);


            createPropertyGroup("redis_connection",
                    new String[] { HOST, PORT, TIMEOUT, PASSWORD, DATABASE });

            p = property(HOST);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(PORT);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, RedisPostProcessor.DEFAULT_PORT);
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(TIMEOUT);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, RedisPostProcessor.DEFAULT_TIMEOUT);
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(PASSWORD);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(DATABASE);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, RedisPostProcessor.DEFAULT_DATABASE);
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);


            createPropertyGroup("pool_config",
                    new String[] { MIN_IDLE, MAX_IDLE, MAX_ACTIVE, MAX_WAIT, WHEN_EXHAUSTED_ACTION,
                            TEST_ON_BORROW, TEST_ON_RETURN, TEST_WHILE_IDLE, TIME_BETWEEN_EVICTION_RUNS_MS,
                            NUM_TEST_PER_EVICTION_RUN, MIN_EVICTABLE_IDLE_TIME_MS, SOFT_MIN_EVICTABLE_IDLE_TIME_MS});


            p = property(MIN_IDLE);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "0");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(MAX_IDLE);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "10");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(MAX_ACTIVE);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "20");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(MAX_WAIT);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "30000");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(WHEN_EXHAUSTED_ACTION, RedisPostProcessor.WhenExhaustedAction.class);
            p.setValue(DEFAULT, RedisPostProcessor.WhenExhaustedAction.GROW.ordinal());
            p.setValue(NOT_UNDEFINED, Boolean.TRUE); // must be defined

            p = property(TEST_ON_BORROW);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, Boolean.FALSE);
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);
            p.setValue(NOT_OTHER, Boolean.TRUE);

            p = property(TEST_ON_RETURN);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, Boolean.FALSE);
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);
            p.setValue(NOT_OTHER, Boolean.TRUE);

            p = property(TEST_WHILE_IDLE);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, Boolean.FALSE);
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);
            p.setValue(NOT_OTHER, Boolean.TRUE);


            p = property(TIME_BETWEEN_EVICTION_RUNS_MS);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "30000");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(NUM_TEST_PER_EVICTION_RUN);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "0");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(MIN_EVICTABLE_IDLE_TIME_MS);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "60000");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);

            p = property(SOFT_MIN_EVICTABLE_IDLE_TIME_MS);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "60000");
            p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        } catch (NoSuchMethodError e) {
            LOGGER.error("Error initializing component GraphGeneratorListener due to missing method, if your version is lower than 2.10, this" +
                    "is expected to fail, if not check project dependencies");
        }
    }
}
