package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.frontvo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类
        List<CategoryEntity> categoryAllList = baseMapper.selectList(null);

        //2.组装成父子的树形结构
        List<CategoryEntity> level1Menus = categoryAllList.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map((menu) -> {
                    menu.setChildren(getChildrenList(menu, categoryAllList));
                    return menu;
                }).sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }).collect(Collectors.toList());

        return level1Menus;
    }

    /*
        递归查找所有菜单的子菜单
     */
    public List<CategoryEntity> getChildrenList(CategoryEntity category, List<CategoryEntity> categoryAllList) {
        List<CategoryEntity> children = categoryAllList.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == category.getCatId();
        }).map(categoryEntity -> {
            //1.找到子菜单
            categoryEntity.setChildren(getChildrenList(categoryEntity, categoryAllList));
            return categoryEntity;
        }).sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort())
        ).collect(Collectors.toList());

        return children;
    }


    /**
     * delete
     *
     * @param asList
     */
    @Override
    public void removeMenusByIds(List<Long> asList) {
        //TODO  1.检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 找到catelogId的完整路径
     *
     * @param catelogId
     * @return 完整路径[]
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);    //逆转集合顺序
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);   //收集当前节点id，递归收集

        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0) {
            findParentPath(category.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 级联更新所有关联的数据
     *
     * @CacheEvict :失效模式
     * category: 存在哪个区域中
     * 1.@Caching 同时进行多种缓存操作
     * 2.指定删除某个分区下的所有数据 allEntries = true
     * 3.存储统一类型的数据，都可以指定成同一个分区。分区名默认就是缓存的前缀。
     */
//    @Caching(evict = {
//            @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'"),
//            @CacheEvict(value = {"category"}, key = "'getCatalogJson'")
//    })
    @CacheEvict(value = {"category"}, allEntries = true)
//    @CachePut  //双写模式使用此注解
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        baseMapper.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        //同时修改缓存中的数据
        //redis.del("catalogJSON"); 等待下次查询主动更新
    }

    /**
     * 1.每一个需要缓存的数据我们都要指定要放到哪个名字的缓存。【缓存的分区(按照业务类型分)】
     * 2.@Cacheable ({"category"})
     * 代表当前方法的结果需要缓存，如果缓存中有，方法不用调用。如果缓存中没有，会调用方法，最后将方法的结果放入缓存
     * 3.默认行为
     * 1）、如果缓存中有，方法不用调用
     * 2）、key默认自动生成的：缓存的名字::SimpleKey [] 自主生成的key值
     * 3）、缓存的value的值，默认使用jdk序列化机制，将序列化后的数据存到redis
     * 4）、默认ttl时间 -1
     * 4.自定义：
     * 1）、指定生成的缓存使用的key：  key属性指定，接收一个SpEL表达式 key = "#root.method.name":指定方法名作为key
     * 2）、指定缓存的数据的存活时间：配置文件中修改TTL
     * 3）、将数据保存为json格式
     */
    //@Cacheable(value = {"category"},key = "'Level1Categorys'")
    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * 加入Redis缓存逻辑
     * TODO 产生对外内存溢出异常:OutOfDirectMemoryError
     * 1）、SpringBoot2.0以后默认使用lettuce作为操作redis的客户端，它使用netty进行网络通信。
     * 2）、lettuce的bug导致堆外内存溢出，netty如果没有指定堆外内存，默认使用配置的内存 -Xmx100m
     * 可以通过-Dio.netty.maxDirectMemory进行设置
     * 解决方案：不能使用-Dio.netty.maxDirectMemory调大内存。
     * 1）、升级lettuce客户端  2）、切换使用Jedis客户端
     * 2）、lettuce、Jedis都是操作redis的底层客户端，Spring再次封装redisTemplate;
     */

    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1.查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2.封装数据
        Map<String, List<Catalog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1.查出每一个一级分类的二级分类
            List<CategoryEntity> categoryLevel2List = getParent_cid(selectList, v.getParentCid());
            //2.封装上面的结果
            List<Catalog2Vo> catelog2Vos = null;
            if (categoryLevel2List != null) {
                catelog2Vos = categoryLevel2List.stream().map((level2) -> {
                    Catalog2Vo catelog2Vo = new Catalog2Vo(v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                    //1.找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> categoryLevel3List = getParent_cid(selectList, level2.getParentCid());
                    if (categoryLevel3List != null) {
                        List<Catalog2Vo.Catalog3VO> catalog3VOS = categoryLevel3List.stream().map((level3) -> {
                            Catalog2Vo.Catalog3VO catalog3VO = new Catalog2Vo.Catalog3VO(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catalog3VO;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catalog3VOS);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return parentCid;
    }

    public Map<String, List<Catalog2Vo>> getCatalogJson2() {
        /**
         * 1.空结果缓存，解决缓存穿透
         * 2.设置过期时间(加随机值)：解决缓存雪崩
         * 3.加锁，解决缓存穿透
         */
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        //1.如果缓存中没有数据则从数据库中进行查询并放入缓存中(缓存中存的数据是json字符串)
        if (StringUtils.isEmpty(catalogJSON)) {
            System.out.println("缓存不命中，查询数据库。。。。。。。。。。。。。。。。。。。。。。。");
            Map<String, List<Catalog2Vo>> catalogJsonFromDB = getCatalogJsonFromDBWithRedisLock();
            return catalogJsonFromDB;
        }
        System.out.println("缓存命中，不用查询数据库。。。。。。。。。。。。。。。。。。。。。。。");

        //2.将存入的json对象转换为指定的类型
        Map<String, List<Catalog2Vo>> stringListMap = JSON.parseObject(catalogJSON,
                new TypeReference<Map<String, List<Catalog2Vo>>>() {
                });
        return stringListMap;
    }

    /**
     * 使用redisson的分布式锁
     * 缓存里面的数据如何和数据库保持一致？
     * 缓存数据一致性：
     * 1）、双写模式
     * 2）、失效模式
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedissonLock() {
        //锁的名字。锁的粒度，越细越快。
        //锁的粒度，具体缓存的是某个数据：11-号商品：product-11-lock    product-12-lock
        RLock lock = redissonClient.getLock("catalogJSON-lock");
        lock.lock();
        //1.占分布式锁，去redis占坑
        System.out.println("获取分布式锁成功..........................");

        try {
            //执行业务
            Map<String, List<Catalog2Vo>> dataFromDB = getDataFromDB();
            return dataFromDB;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 使用redis的分布式锁
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        //1.占分布式锁，去redis占坑
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功..........................");
            //1.加锁成功...执行业务
            //2.设置锁的过期时间，避免造成死锁现象 (设置过期时间，必须和加锁是同步的，原子性)
            //redisTemplate.expire("lock",30,TimeUnit.SECONDS);
            try {
                Map<String, List<Catalog2Vo>> dataFromDB = getDataFromDB();
                return dataFromDB;
            } finally {
                //获取值对比+对比成功删除=原子操作 lua脚本解锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                //删除锁，1删除成功，0删除失败
                Long deleteLock = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
                if (deleteLock == 1) {
                    System.out.println("删除分布式锁成功..........................");
                }
            }
//            String lockVlaue = redisTemplate.opsForValue().get("lock");
//            if (uuid.equals(lockVlaue)){
//                //业务执行完，删除自己的锁
//                redisTemplate.delete("lock"); //删除锁
//            }

        } else {
            //加锁失败...重试。synchronized()，休眠100毫秒重试
            System.out.println("获取分布式锁失败，等待重试..................");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDBWithRedisLock();//自旋的方式
        }
    }

    /**
     * 使用本地锁
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithLocalLock() {
        /**
         * 只要是同一把锁，就能锁住需要这个锁的所有线程
         * 1.synchronized (this):SpringBoot所有的组件在容器中都是单例的。
         * 2.TODO 本地锁：synchronized，JUC(Lock)，在分布式情况下，想要锁住所有，必须使用分布式锁
         */
        synchronized (this) {
            //得到锁之后，我们需要再去缓存中确定一次，如果缓存中没有数据才需要继续查询
            return getDataFromDB();
        }

    }

    /**
     * 从数据库查询并封装整个分类数据的方法
     */
    private Map<String, List<Catalog2Vo>> getDataFromDB() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            //将存入的json对象转换为指定的类型，缓存不为null直接返回
            Map<String, List<Catalog2Vo>> stringListMap = JSON.parseObject(catalogJSON,
                    new TypeReference<Map<String, List<Catalog2Vo>>>() {
                    });
            return stringListMap;
        }
        System.out.println(Thread.currentThread().getName() + "线程锁：查询了数据库.......................");
        /**
         * 将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1.查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2.封装数据
        Map<String, List<Catalog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1.查出每一个一级分类的二级分类
            List<CategoryEntity> categoryLevel2List = getParent_cid(selectList, v.getParentCid());
            //2.封装上面的结果
            List<Catalog2Vo> catelog2Vos = null;
            if (categoryLevel2List != null) {
                catelog2Vos = categoryLevel2List.stream().map((level2) -> {
                    Catalog2Vo catelog2Vo = new Catalog2Vo(v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                    //1.找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> categoryLevel3List = getParent_cid(selectList, level2.getParentCid());
                    if (categoryLevel3List != null) {
                        List<Catalog2Vo.Catalog3VO> catalog3VOS = categoryLevel3List.stream().map((level3) -> {
                            Catalog2Vo.Catalog3VO catalog3VO = new Catalog2Vo.Catalog3VO(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catalog3VO;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catalog3VOS);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        //3.将对象转为JSON放在缓存中，json跨语言，跨平台兼容
        //!!!注意：给缓存中放json字符串，拿出的json字符串，还要逆转为能用的对象类型，【序列化与反序列化的过程】
        String jsonString = JSON.toJSONString(parentCid);
        redisTemplate.opsForValue().set("catalogJSON", jsonString, 1, TimeUnit.DAYS);
        return parentCid;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

}