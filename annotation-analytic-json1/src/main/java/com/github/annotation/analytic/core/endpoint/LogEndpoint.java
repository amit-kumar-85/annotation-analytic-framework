/**
 * 
 */
package com.github.annotation.analytic.core.endpoint;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * @author shivam
 *
 */
public class LogEndpoint implements Endpoint
{
    private static final Logger LOG = LoggerFactory.getLogger(LogEndpoint.class);
    
    private static final String PROPERTY_FILENAME = "analytic.properties";
    private static final String LOG_ENDPOINT_LOGGER_NAME = "log-endpoint-logger-name";
    private static final String DEFAULT_LOG_ENDPOINT_LOGGER_NAME = "analyticLogger";

    private static final String ENV_LOGGER_BASE_ENDPOINT = "analytic.logger.base.endpoint";
    
    private Map<String,Logger> loggers = new HashMap<String, Logger>();

    private String loggerBaseEndpoint = null;

    @Override
    public void init()
    {
        String envLoggerBaseEndpoint = System.getProperty(ENV_LOGGER_BASE_ENDPOINT);
        loggerBaseEndpoint = StringUtils.isEmpty(envLoggerBaseEndpoint) ? "" : envLoggerBaseEndpoint;
    }

    @Override
    public void invoke(String transactionName, Map<String, Object> endpointData)
    {
        StringBuilder loggerNameBuilder = new StringBuilder(loggerBaseEndpoint);
        if (loggerNameBuilder.length() > 0) {
            loggerNameBuilder.append('.');
        }
        loggerNameBuilder.append(transactionName);
        String logger = loggerNameBuilder.toString();

        Logger logg = loggers.get(logger);
        if (logg == null) {
            logg = LoggerFactory.getLogger(logger);
            loggers.put(logger, logg);
        }
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            logg.info(mapper.writeValueAsString(endpointData));
        }
        catch (Exception e)
        {
            LOG.trace(e.getMessage(), e);
        }
    }

}
