程序代码是由两部分组成的，一个部分是web模块，一个部分是api模块，装上idea，依次启动这两个模块，然后再从浏览器中访问即可。

若要获取最新代码，可访问：
https://github.com/StormPhoenix/TrajectorMapProj

１． 项目架构
=

整个项目采用Spring boot搭建，通过http接口的方式对外提供服务。项目中代码只有两个模块：
> trajector-map-web: 用于向用户展示地图，用spring boot + thymeleaf框架写的

> trajector-map-api: 向其他trajector-map-web模块提供数据服务，这个模块所有的数据都会传到web模块进行展示。

２．程序运行
=
代码还没有打包成jar包，直接从IDE中启动。IDE建议选择IDEA，不要用eclipse，否则绝对会出问题。

依次启动每个模块，启动方式很简单，直接运行每个模块的main方法就可以了，启动的顺序最好是先启动trajector-map-api，然后再启动trajector-map-web，当然这个没有严格的规定，但是我推荐这么做。

３．效果展示
=
在浏览器中访问 http://localhost:8081/[路径]，方括号的路径是根据你要使用的功能而决定的，这个在trajector-map-web模块的MapController.java文件中可找到。
