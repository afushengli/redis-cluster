package com.fsl.redis.com.fsl.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: chenrj
 * @Date: 2019/9/3 09:52
 * @Description:
 */

@Component
public class RedisConfig {
    @Value("${redis.maxIdle}")
    private Integer maxIdle;

    @Value("${redis.maxTotal}")
    private Integer maxTotal;

    @Value("${redis.maxWaitMillis}")
    private Integer maxWaitMillis;

    @Value("${redis.minEvictableIdleTimeMillis}")
    private Integer minEvictableIdleTimeMillis;

    @Value("${redis.numTestsPerEvictionRun}")
    private Integer numTestsPerEvictionRun;

    @Value("${redis.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;

    @Value("${redis.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${redis.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${redis.cluster.max-redirects}")
    private Integer mmaxRedirectsac;


/*    @Value("${spring.redis.cluster.nodes}")  //如果是redisluster spring.redis.cluster.nodes
    private String clusterNodes;*/

    @Value("${redis.password}")
    private String redispwd;

    /**
     * JedisPoolConfig 连接池
     *
     * @return
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲数
        jedisPoolConfig.setMaxIdle(maxIdle);
        // 连接池的最大数据库连接数
        jedisPoolConfig.setMaxTotal(maxTotal);
        // 最大建立连接等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        // 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        // 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        // 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        // 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        // 在空闲时检查有效性, 默认false
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        return jedisPoolConfig;
    }


    /*@Bean  //如果是redisluster 需要添加
    public RedisClusterConfiguration clusterConfig() {
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
        clusterConfig.setMaxRedirects(mmaxRedirectsac);

        List<RedisNode> list = new ArrayList<>();
        String[] nodes = clusterNodes.split(",");
        for(String node : nodes){
            String[] host =  node.split(":");
            RedisNode redis = new RedisNode(host[0], Integer.parseInt(host[1]));
            list.add(redis);
           // config.addClusterNode(redis);
        }

        clusterConfig.setClusterNodes(list);
        return clusterConfig;
    }
*/




    /**
     * 配置工厂  //如果是redisluster 需要添加RedisClusterConfiguration
     */
    @Bean
    public JedisConnectionFactory JedisConnectionFactory(/*RedisClusterConfiguration clusterConfig,*/JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory JedisConnectionFactory = new JedisConnectionFactory(/*clusterConfig,*/jedisPoolConfig);

        /*
         *    设置密码，如果为空，则不设置，如果不为空则设置
         *  可在 redis.conf 文件中设置: requirepass 密码
         */

        if (redispwd != null || redispwd.length() != 0) {
            JedisConnectionFactory.setPassword(redispwd);
        }


        return JedisConnectionFactory;
    }

    /**
     * 实例化 RedisTemplate 对象
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> functionDomainRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        initDomainRedisTemplate(redisTemplate, redisConnectionFactory);
        return redisTemplate;
    }



    /**
     * 设置数据存入 redis 的序列化方式,并开启事务
     *
     * @param redisTemplate
     * @param factory
     */
    private void initDomainRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory factory) {
        /*
         * 设置 序列化器 .
         * 如果不设置，那么在用实体类(未序列化)进行存储的时候，会提示错误: Failed to serialize object using DefaultSerializer;
         */
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 开启事务
        redisTemplate.setEnableTransactionSupport(true);
        // 将连接工厂设置到模板类中
        redisTemplate.setConnectionFactory(factory);
    }

    /**
     * 注入封装RedisTemplate
     */
 /*   @Bean(name = "redisUtil")
    public RedisUtil redisUtil(RedisTemplate<String, Object> redisTemplate) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(redisTemplate);
        return redisUtil;
    }
*/

}
