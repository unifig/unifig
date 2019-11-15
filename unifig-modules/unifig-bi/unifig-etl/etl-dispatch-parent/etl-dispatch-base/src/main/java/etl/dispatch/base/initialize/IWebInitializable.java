package etl.dispatch.base.initialize;

/**
 * 如果需要容器启动时进行一系列的操作可实现此接口，在ApplicationInitialization类中 容器启动时会自动调用initialize和printInitLog两个方法，同时也提供了setInitDepend
 * 方法，该方法主要是为了实现依赖关系，如果有多个初始化操作并且彼此之间是有着执行顺序的关系，只需要在 该方法中返回依赖的class即可。
 * 
 *
 *
 */
public interface IWebInitializable {

    /**
     * 容器初始化执行的方法。<br/> 详细描述：容器启动时会自动调用所有实现该接口initialize方法的类。<br/> 使用方式：调用者需要实现该接口并且实现initialize方法体，完成里面的具体操作即可。
     */
    void initialize();

    /**
     * 容器初始化执行的方法，该方法是为了进行日志输出。<br/> 详细描述：容器启动时会自动调用所有实现该接口printInitLog方法的，该方法的目的只是为了进行日志的输出操作。<br/>
     * 使用方式：调用者需要实现该接口并且实现printInitLog方法体进行日志输出操作。
     */
    String initLog();

    void destroy();

    String destroyLog();

    /**
     * 该方法是为了实现类的依赖关系，在初始化时形成调用顺序。<br/> 详细描述：容器启动时会根据该方法依赖关系，先执行setInitDepend方法设置的class。在执行自己的初始化方法。<br/>
     * 使用方式：在该方法中返回其他也实现InitializableDao接口的类。
     * 
     * @return 实现InitializableDao接口的类，实现依赖关系。
     */
    Class<? extends IWebInitializable> setInitDepend();
}