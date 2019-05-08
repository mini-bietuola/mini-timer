package com.netease.mini.bietuola.config.mybatis;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/28
 */
@Configuration
public class RegisterTypeHandlerConfig implements ConfigurationCustomizer {
    @Override
    public void customize(org.apache.ibatis.session.Configuration configuration) {
        VFS.addImplClass(SpringBootVFS.class);
        String handledTypesPackages = "com.netease.mini.bietuola.constant";
        TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(IntEnum.class), handledTypesPackages);
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        for (Class<?> type: typeSet) {
            registry.register(type, IntEnumTypeHandler.class);
        }
    }
}
