package etl.dispatch.base.datasource.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 该类是注解,用来表示mybatis的数据源,将该注解定义在使用者的接口上,接口里的所有方法都会使用spring中配置的dim数据源,目的是为了支持 项目多数据源的功能,在接口上面配置@DimRepository。
 *
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.PARAMETER })
public @interface SpapDimRepository{
}