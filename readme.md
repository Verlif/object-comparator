# ObjectComparator

对象比对工具，用来对比两个对象的所有属性差别，可以是不同类。
非常适合做数据版本对比。

## 使用

```java
Person old = new Person();
Student now = new Student();
now.setBirthday(new Date());
old.setPet(new Pet("小猫"));
Different different = new CompareCore().compare(old, now);
```

最终所得的`Different`则是对比结果，包括了所有相同与不同的项。一般情况下你可以这样使用：

```java
List<DiffValue> changedValues = different.getChangedValues();
```

得到以下结果：

```json
[
  {
    name='score',
    old=null,
    now=0.0,
    type=NEW_FIELD
  },
  {
    name='生日',
    old=null,
    now=Tue Mar 21 14:21:30 GMT+08:00 2023,
    type=MODIFY_FILL
  },
  {name='pet',
    old=idea.verlif.test.domain.Pet@4fca772d,
    now=idea.verlif.test.domain.Pet@9807454,
    type=MODIFY_VALUE
  },
  {
    name='pet.name',
    old=小猫,
    now=小狗,
    type=MODIFY_VALUE
  }
]
```

开发者也可以通过`CompareCore.addEqualJudge()`来添加对应类的判断方法。

## 说明

通过`@CompareField`注解来控制属性是否被忽略或是否递归对比，在上述例子中的 __Pet__ 属性被标记了`@CompareField(deep = true)`，所以得到了`pet.name`的对比结果。

通过`EqualJudge`接口控制值对比逻辑。

`Type`有7个值，分别表示了7种对比状态：

- __NEW_FIELD__ - 新属性，从没有属性到有属性
- __DROPPED_FIELD__ - 无属性，从有属性到没有属性
- __MODIFY_VALUE__ - 值修改
- __MODIFY_FILL__ - 旧值为空
- __MODIFY_NULL__ - 新值为空
- __MODIFY_TYPE__ - 属性类型修改
- __NO__ - 未修改

## 添加依赖

1. 添加Jitpack仓库源

   maven

    ```xml
    <repositories>
       <repository>
           <id>jitpack.io</id>
           <url>https://jitpack.io</url>
       </repository>
    </repositories>
    ```

   Gradle

    ```text
    allprojects {
      repositories {
          maven { url 'https://jitpack.io' }
      }
    }
    ```

2. 添加依赖

   __lastVersion__ [![](https://jitpack.io/v/Verlif/object-comparator.svg)](https://jitpack.io/#Verlif/object-comparator)

   maven

   ```xml
      <dependencies>
          <dependency>
              <groupId>com.github.Verlif</groupId>
              <artifactId>object-comparator</artifactId>
              <version>lastVersion</version>
          </dependency>
      </dependencies>
   ```

   Gradle

   ```text
   dependencies {
     implementation 'com.github.Verlif:object-comparator:lastVersion'
   }
   ```
