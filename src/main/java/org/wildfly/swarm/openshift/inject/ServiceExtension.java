package org.wildfly.swarm.openshift.inject;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.Optional;
import java.util.Set;

public class ServiceExtension implements Extension {

    public <T> void processInjectionTarget(final @Observes ProcessInjectionTarget<T> pit) {

        final InjectionTarget<T> it = pit.getInjectionTarget();
        InjectionTarget<T> wrapped = new InjectionTarget<T>() {
            @Override
            public void inject(T instance, CreationalContext<T> ctx) {

                it.inject(instance, ctx);

                AnnotatedType<T> at = pit.getAnnotatedType();
                for (AnnotatedField field : at.getFields()) {
                    try {

                        if (field.isAnnotationPresent(Service.class)) {

                            Service svc = at.getAnnotation(Service.class);
                            Optional<String> serviceTarget = getServiceTarget(svc.value());

                            Class<?> baseType = field.getJavaMember().getType();

                            if (baseType == Optional.class) {
                                field.getJavaMember().set(instance, serviceTarget);
                            }  else {
                                pit.addDefinitionError(
                                        new InjectionException("Type " + baseType + " of Field " + field.getJavaMember().getName() + " not recognized yet!")
                                );
                            }

                        }

                    } catch (Exception e) {
                        pit.addDefinitionError(new InjectionException(e));
                    }
                }
            }

            @Override
            public void postConstruct(T instance) {
                it.postConstruct(instance);
            }

            @Override
            public void preDestroy(T instance) {
                it.dispose(instance);
            }

            @Override
            public void dispose(T instance) {
                it.dispose(instance);
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return it.getInjectionPoints();
            }

            @Override
            public T produce(CreationalContext<T> ctx) {
                return it.produce(ctx);
            }
        };
        pit.setInjectionTarget(wrapped);
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
