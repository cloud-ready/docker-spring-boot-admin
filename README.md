
# docker-spring-boot-admin


see: https://codecentric.github.io/spring-boot-admin/1.5.7
see: https://lfvepclr.gitbooks.io/spring-boot-admin-v1-5-0/content/
see: https://yq.aliyun.com/articles/226381


Configuration via environment variables

- SPRING_BOOT_ADMIN_UI_TITLE
> set page-title, default: Spring Boot Admin

- SPRING_SECURITY_ENABLED
> default: true

- SPRING_SECURITY_USER_NAME
> set username, default: admin

- SPRING_SECURITY_USER_PASSWORD
> set password, default: admin_pass


### Build this project

```bash
JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home" \
    ./mvnw -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -DgenerateReports=false \
        help:active-profiles clean package
```
