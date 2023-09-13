### 安装mybatipse插件失败
> 2023.03版本的eclipse安装mybatipse失败
1. 通过marketplace安装
2. 通过install new solfware
3. 通过离线安装方式 [https://github.com/mybatis/mybatipse/issues/88]
最终通过上面的3下载到content.jar，然后eclipse->help->install new solfware->add->location选择下载的content.jar完成。

### eclipse的mapper.xml一直提示错误
> 2023.03版本的eclipse
eclipse提示的错误是.lemminx\cache\http\mybatis.org\dtd\mybatis-3-mapper.dtd下载了这个文件，其实在本地目录并没有找到，手动创建即可


### mybatis的mapper xml文件不提示
> 重新安装一下`xml`工具, `Eclipse XML Editors and Tools`

### 设置eclipse为英文和文件编码
> eclipse.ini
```
-Duser.language=en
-Dfile.encoding=UTF-8
```

### 补全代码时会把后面的内容给替换(出现一个黄色背景色被选择的区域)
```
windows -> preferences -> editor -> content assit 
insertion这里单选框选择compiletion inserts
```
