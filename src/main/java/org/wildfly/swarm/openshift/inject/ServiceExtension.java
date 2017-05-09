package org.wildfly.swarm.openshift.inject;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

@Vetoed
public class ServiceExtension implements Extension {

  public ServiceExtension() {
    System.out.println("ServiceExtension");
  }

 /* <X> void processInjectionTarget(@Observes ProcessInjectionTarget<X> pit) {

    final InjectionTarget<X> it = pit.getInjectionTarget();
    final AnnotatedType<X> at = pit.getAnnotatedType();

    InjectionTarget<X> wrapper = new InjectionTarget<X>() {

      @Override
      public X produce(CreationalContext<X> ctx) {
        return it.produce(ctx);
      }

      @Override
      public void dispose(X instance) {
        it.dispose(instance);
      }

      @Override
      public Set<InjectionPoint> getInjectionPoints() {
        return it.getInjectionPoints();
      }

      @Override
      public void inject(X instance, CreationalContext<X> ctx) {
        it.inject(instance, ctx);
        for (Field field : at.getJavaClass().getDeclaredFields()) {
          Service annotation = field.getAnnotation(Service.class);
          if (annotation != null) {
            String key = annotation.value();
            field.setAccessible(true);
            try {
              field.set(instance, getServiceTarget(annotation.value()));
            } catch (IllegalArgumentException | IllegalAccessException e) {
              throw new RuntimeException("Could not resolve service  reference", e);
            }
          }
        }
      }

      @Override
      public void postConstruct(X instance) {
        it.postConstruct(instance);
      }

      @Override
      public void preDestroy(X instance) {
        it.preDestroy(instance);
      }

    };

    pit.setInjectionTarget(wrapper);
  }*/

  public void addConfigView(@Observes BeforeBeanDiscovery bbd, BeanManager beanManager) {
    bbd.addAnnotatedType(beanManager.createAnnotatedType(ServiceValueProducer.class));
  }
}
