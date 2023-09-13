
# spring.factories

- ` spring boot ` 会解析这个文件 `META-INF/spring.factories`，使用`SpringFactoriesLoader`这个类，然后放入缓存
- 然后再加载`META-INF/spring-autoconfigure-metadata.properties`文件，通过里面`ConditionalOnClass`判断是否加载上面的类
- 若想禁用这些类可在`@SpringBootApplication(exclude = {exls.class}`，或者`@EnableAutoConfiguration(exclude={exls.class})`


# @Conditional

- 自定义注解： 如`@EnableSth`可通过`@ConditionalOnMissingBean(annotation=EnableSth.class)`
- 使用`@Import(Sth.class)`，在`Sth.class`加上`@ConditionalOnProperty(name="com.sth",havingValue="true")`可双重判断
