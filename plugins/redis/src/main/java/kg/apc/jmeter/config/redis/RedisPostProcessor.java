package kg.apc.jmeter.config.redis;

import net.sf.json.util.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.gui.GenericTestBeanCustomizer;
import org.apache.jmeter.testelement.AbstractScopedTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JMeterStopThreadException;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * @author 林杰炜 linjiewei
 * @Title: TODO 类描述
 * @Description: TODO 详细描述
 * @date: 2018/4/25 23:07
 */
public class RedisPostProcessor extends AbstractScopedTestElement implements TestBean, PostProcessor, Serializable, TestStateListener {

    public enum WhenExhaustedAction {
        /**
         * 1
         */
        FAIL(GenericObjectPool.WHEN_EXHAUSTED_FAIL),
        BLOCK(GenericObjectPool.WHEN_EXHAUSTED_BLOCK),
        GROW(GenericObjectPool.WHEN_EXHAUSTED_GROW);
        private byte value;

        private WhenExhaustedAction(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    public enum GetMode {
        /**
         * 方法
         */
        RANDOM_HGETALL((byte) 0),
        RANDOM_TTL((byte) 1),
        RANDOM_EXPIRE((byte) 2),
        RANDOM_GET((byte) 3),
        RANDOM_DEL((byte) 4),
        RANDOM_HGET((byte) 5),
        RANDOM_EXISTS((byte) 6),
        RANDOM_SET((byte) 7);
        private byte value;

        private GetMode(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    private static final Logger log = LoggingManager.getLoggerForClass();

    public static final Integer DEFAULT_PORT = Protocol.DEFAULT_PORT;
    public static final Integer DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;
    public static final Integer DEFAULT_DATABASE = Protocol.DEFAULT_DATABASE;

    private String host;
    private String port;
    private String timeout;
    private String password;
    private String database;

    private String redisKey;
    private String variableNames;
    private String delimiter;
    private GetMode getMode;
    private String parameter;

    private int maxIdle;
    private int minIdle;
    private int maxActive;
    private long maxWait;
    private WhenExhaustedAction whenExhaustedAction;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean testWhileIdle;
    private long timeBetweenEvictionRunsMillis;
    private int numTestsPerEvictionRun;
    private long minEvictableIdleTimeMillis;
    private long softMinEvictableIdleTimeMillis;
    private transient String[] vars;

    private transient JedisPool pool;

    @Override
    public void process() {
        final JMeterContext context = getThreadContext();
        SampleResult previousResult = context.getPreviousResult();
        if (previousResult == null) {
            return;
        }
        log.debug("RedisPostProcessor processing result");
        JMeterVariables threadVars = context.getVariables();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String line = null;
            log.info(redisKey);
            if (getMode.equals(GetMode.RANDOM_HGETALL)) {
                line = JSONUtils.valueToString(jedis.hgetAll(this.redisKey));
            } else if (getMode.equals(GetMode.RANDOM_TTL)) {
                line = String.valueOf(jedis.ttl(redisKey));
            } else if (getMode.equals(GetMode.RANDOM_EXPIRE)) {
                jedis.expire(redisKey, Integer.parseInt(parameter));
                line = String.valueOf(jedis.ttl(redisKey));
            } else if (getMode.equals(GetMode.RANDOM_GET)) {
                line = String.valueOf(jedis.get(redisKey));
            } else if (getMode.equals(GetMode.RANDOM_DEL)) {
                line = String.valueOf(jedis.del(redisKey));
            } else if (getMode.equals(GetMode.RANDOM_HGET)) {
                line = String.valueOf(jedis.hget(redisKey, parameter));
            } else if (getMode.equals(GetMode.RANDOM_EXISTS)) {
                line = String.valueOf(jedis.exists(redisKey));
            } else if (getMode.equals(GetMode.RANDOM_SET)) {
                line = String.valueOf(jedis.set(redisKey, parameter));
            }
            if (line == null) {
                throw new JMeterStopThreadException("End of redis data");
            }
            final String names = variableNames;
            if (vars == null) {
                vars = JOrphanUtils.split(names, ",");
            }
            String[] values = {};
            if (StringUtils.isNotBlank(delimiter)) {
                values = JOrphanUtils.split(line, delimiter, false);
            }
            if (values.length > 0) {
                for (int a = 0; a < vars.length && a < values.length; a++) {
                    threadVars.put(vars[a], values[a]);
                }
            } else {
                threadVars.put(vars[0], line);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            pool.returnResourceObject(jedis);
        }
    }

    @Override
    public void testStarted() {
        testStarted("");
    }

    @Override
    public void testEnded() {
        testEnded("");
    }

    @Override
    public void testEnded(String host) {
        pool.destroy();
    }

    @Override
    public Object clone() {
        RedisPostProcessor clonedElement = (RedisPostProcessor) super.clone();
        clonedElement.pool = this.pool;
        return clonedElement;
    }

    /**
     * Override the setProperty method in order to convert
     * the original String calcMode property.
     * This used the locale-dependent display value, so caused
     * problems when the language was changed.
     * Note that the calcMode StringProperty is replaced with an IntegerProperty
     * so the conversion only needs to happen once.
     */
    @Override
    public void setProperty(JMeterProperty property) {
        if (property instanceof StringProperty) {
            final String pn = property.getName();
            if (pn.equals("getMode")) {
                final Object objectValue = property.getObjectValue();
                try {
                    final BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
                    final ResourceBundle rb = (ResourceBundle) beanInfo.getBeanDescriptor().getValue(GenericTestBeanCustomizer.RESOURCE_BUNDLE);
                    for (Enum<RedisDataSet.GetMode> e : RedisDataSet.GetMode.values()) {
                        final String propName = e.toString();
                        if (objectValue.equals(rb.getObject(propName))) {
                            final int tmpMode = e.ordinal();
                            if (log.isDebugEnabled()) {
                                log.debug("Converted " + pn + "=" + objectValue + " to mode=" + tmpMode + " using Locale: " + rb.getLocale());
                            }
                            super.setProperty(pn, tmpMode);
                            return;
                        }
                    }
                    log.warn("Could not convert " + pn + "=" + objectValue + " using Locale: " + rb.getLocale());
                } catch (IntrospectionException e) {
                    log.error("Could not find BeanInfo", e);
                }
            } else if (pn.equals("whenExhaustedAction")) {
                final Object objectValue = property.getObjectValue();
                try {
                    final BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
                    final ResourceBundle rb = (ResourceBundle) beanInfo.getBeanDescriptor().getValue(GenericTestBeanCustomizer.RESOURCE_BUNDLE);
                    for (Enum<RedisDataSet.WhenExhaustedAction> e : RedisDataSet.WhenExhaustedAction.values()) {
                        final String propName = e.toString();
                        if (objectValue.equals(rb.getObject(propName))) {
                            final int tmpMode = e.ordinal();
                            if (log.isDebugEnabled()) {
                                log.debug("Converted " + pn + "=" + objectValue + " to mode=" + tmpMode + " using Locale: " + rb.getLocale());
                            }
                            super.setProperty(pn, tmpMode);
                            return;
                        }
                    }
                    log.warn("Could not convert " + pn + "=" + objectValue + " using Locale: " + rb.getLocale());
                } catch (IntrospectionException e) {
                    log.error("Could not find BeanInfo", e);
                }
            }
        }
        super.setProperty(property);
    }

    @Override
    public void testStarted(String distributedHost) {
        JedisPoolConfig config = new JedisPoolConfig();
        //config.setMaxActive(getMaxActive());
        /*config.setMaxTotal(getMaxActive());
        config.setMaxIdle(getMaxIdle());
        config.setMinIdle(getMinIdle());
        *//*config.setMaxWait(getMaxWait());
        config.setWhenExhaustedAction((byte)getWhenExhaustedAction());*//*
        config.setMaxWaitMillis(getMaxWait());
        config.setTestOnBorrow(getTestOnBorrow());
        config.setTestOnReturn(getTestOnReturn());
        config.setTestWhileIdle(getTestWhileIdle());
        config.setTimeBetweenEvictionRunsMillis(getTimeBetweenEvictionRunsMillis());
        config.setNumTestsPerEvictionRun(getNumTestsPerEvictionRun());
        config.setMinEvictableIdleTimeMillis(getMinEvictableIdleTimeMillis());
        config.setSoftMinEvictableIdleTimeMillis(getSoftMinEvictableIdleTimeMillis());*/
        config.setMaxTotal(getMaxActive());
        config.setMaxWaitMillis(getMaxWait());
        config.setMaxIdle(getMaxIdle());
        config.setMinIdle(getMinIdle());
        config.setTestOnBorrow(false);
        config.setTestOnCreate(false);
        config.setTestWhileIdle(true);

        int port = Protocol.DEFAULT_PORT;
        if (!JOrphanUtils.isBlank(this.port)) {
            port = Integer.parseInt(this.port);
        }
        int timeout = Protocol.DEFAULT_TIMEOUT;
        if (!JOrphanUtils.isBlank(this.timeout)) {
            timeout = Integer.parseInt(this.timeout);
        }
        int database = Protocol.DEFAULT_DATABASE;
        if (!JOrphanUtils.isBlank(this.database)) {
            database = Integer.parseInt(this.database);
        }
        String password = null;
        if (!JOrphanUtils.isBlank(this.password)) {
            password = this.password;
        }
        this.pool = new JedisPool(config, this.host, port, timeout, password, database);


        /*redisProperties.setHost(this.host);
        redisProperties.setPassword(password);
        redisProperties.setPort(port);
        redisProperties.setDatabase(database);*/
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return the timeout
     */
    public String getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the redisKey
     */
    public String getRedisKey() {
        return redisKey;
    }

    /**
     * @param redisKey the redisKey to set
     */
    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    /**
     * @return the variableNames
     */
    public String getVariableNames() {
        return variableNames;
    }

    /**
     * @param variableNames the variableNames to set
     */
    public void setVariableNames(String variableNames) {
        this.variableNames = variableNames;
    }

    /**
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @return the maxIdle
     */
    public int getMaxIdle() {
        return maxIdle;
    }

    /**
     * @param maxIdle the maxIdle to set
     */
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    /**
     * @return the minIdle
     */
    public int getMinIdle() {
        return minIdle;
    }

    /**
     * @param minIdle the minIdle to set
     */
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    /**
     * @return the maxActive
     */
    public int getMaxActive() {
        return maxActive;
    }

    /**
     * @param maxActive the maxActive to set
     */
    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    /**
     * @return the maxWait
     */
    public long getMaxWait() {
        return maxWait;
    }

    /**
     * @param maxWait the maxWait to set
     */
    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    /**
     * @return the whenExhaustedAction
     */
    public int getWhenExhaustedAction() {
        return whenExhaustedAction.ordinal();
    }

    /**
     * @param whenExhaustedAction the whenExhaustedAction to set
     */
    public void setWhenExhaustedAction(int whenExhaustedAction) {
        this.whenExhaustedAction = WhenExhaustedAction.values()[whenExhaustedAction];
    }

    /**
     * @return the testOnBorrow
     */
    public boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    /**
     * @param testOnBorrow the testOnBorrow to set
     */
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    /**
     * @return the testOnReturn
     */
    public boolean getTestOnReturn() {
        return testOnReturn;
    }

    /**
     * @param testOnReturn the testOnReturn to set
     */
    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    /**
     * @return the testWhileIdle
     */
    public boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    /**
     * @param testWhileIdle the testWhileIdle to set
     */
    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    /**
     * @return the timeBetweenEvictionRunsMillis
     */
    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    /**
     * @param timeBetweenEvictionRunsMillis the timeBetweenEvictionRunsMillis to set
     */
    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    /**
     * @return the numTestsPerEvictionRun
     */
    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    /**
     * @param numTestsPerEvictionRun the numTestsPerEvictionRun to set
     */
    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    /**
     * @return the minEvictableIdleTimeMillis
     */
    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    /**
     * @param minEvictableIdleTimeMillis the minEvictableIdleTimeMillis to set
     */
    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    /**
     * @return the softMinEvictableIdleTimeMillis
     */
    public long getSoftMinEvictableIdleTimeMillis() {
        return softMinEvictableIdleTimeMillis;
    }

    /**
     * @param softMinEvictableIdleTimeMillis the softMinEvictableIdleTimeMillis to set
     */
    public void setSoftMinEvictableIdleTimeMillis(
            long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public int getGetMode() {
        return getMode.ordinal();
    }

    public void setGetMode(int mode) {
        this.getMode = GetMode.values()[mode];
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
