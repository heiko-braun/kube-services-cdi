package org.wildfly.swarm.openshift.inject;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.reflect.Type;
import java.util.Optional;

public class ServiceValueProducer {

    @Produces
    @Service("")
    @Dependent
    Optional<String> produceServiceRef(InjectionPoint injectionPoint) {
        Type type = injectionPoint.getAnnotated().getBaseType();
        final Class<String> valueType = String.class;

        String serviceName = injectionPoint.getAnnotated().getAnnotation(Service.class).value();
        return getServiceTarget(serviceName);
    }

    private static int servicePort(String serviceName) {
        String envName = serviceName.replace("-", "_").toUpperCase() + "_SERVICE_PORT";
        String envPort = System.getenv(envName);
        if (envPort == null) {
            return -1;
        }

        return Integer.parseInt(envPort);
    }

    private String serviceHost(String serviceName) {
        String envName = serviceName.replace("-", "_").toUpperCase() + "_SERVICE_HOST";
        return System.getenv(envName);
    }

    private Optional<String> getServiceTarget(String serviceName) {

        if (serviceHost(serviceName) != null) {
            return Optional.of("http://" + serviceHost(serviceName) + ":" + servicePort(serviceName));
        }

        return Optional.empty();
    }
}


