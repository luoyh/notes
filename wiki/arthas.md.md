```shell

# get classloader
> classloader -l

# get bean proxy on spring
> ognl -c 31cefde0 '#ctx=@com.ofllibnnb.arthas.helper.Springs@ctx,#ctx.geteBean("TestBean")'

# get static field, -x extends level
> getstatic com.ofllibnnb.arthas.service.TestBean testVariables -x 2

# get menu variables 
> getstatic com.ofllibnnb.arthas.enums.TestEnums RED filedMap -x 2

# execute method, call static method
> ognl '@java.lang.System@currentTimeMillis()'
> ognl '#c=@com.ofllibnnb.arthas.TestService@class,#f=#c.getDeclaredField("field0"),#f.setAccessible(true),#f.set(#c,123)'

# watch method invoke, -b before invoke
> watch com.ofllibnnb.arthas.service.TestBean runMethod '{params,returnObj}' -b

# execute spring method
> ognl -c 31cefde0 '#ctx=xxx.Springs@ctx,#i=#ctx.getBean("xxService"),#i.runMethod("param0",123,"param2")'

# monitor method
> tt -t com.ofllibnnb.arthas.TestService runMethod

# not expose spring ApplicationContext how to get bean
> tt -t org.springframework.web.servlet.mvc.method.annotation.requestMappingHandlerAdapter invokeHandlerMethod
> tt -i 1000 -w 'target.getApplicationContext().getBean("testService")'
> tt -i 1000 -w 'target.getApplicationContext().getBean("testService").fieldOne'

```