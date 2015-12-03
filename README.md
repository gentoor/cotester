# cotester
test伴侣 --- 数据驱动测试

当前仅支持excel驱动的测试。
功能支持：目前可以从excel中读取数据自动装配的数据类型包括：
1） 基础数据类型：int, short, long ,byte, double, float, boolean 及其封装类 
2)  常用简单类型：Date、String, StringBuilder, StringBuffer
3)  Collection及Map下面的接口及类
4） 自定义的类， 一般是实体类，毕竟是测试数据
依赖：伟大的testng。（如果你用junit，要么更换testng，要么绕行：））
使用方法：
1.testcase中书写
    @Test(dataProvider = "ExcelDataProvider", dataProviderClass = ExcelDataProvider.class)
    public void testListUsers(String name, List<Users> expected) throws Exception {
        List<Users> actual = cs.listUsers(name);
        Assert.assertNotEquals(actual, expected);
    }
2.excel文件放在testcase同目录或者src/test/resources/与测试类相同目录下面，注意以下几点
2.1） excel的sheet名称采用与测试方法同名，如上例就是 testListUsers
2.2） 参数为基础数据类型、常用简单类型，首行按顺序写上参数名称， 下面数据部分直接书写
2.3） 参数为自定义类时，对应参数及其关心的字段分列填写在首行中如 user.id, user.name， 数据部分直接书写
2.4） 参数为Collection及Map时，首行写参数名；此时需要创建引用sheet页， 如上例就是创建名称为$expected的sheet页；
      在本列下面的数据中要写明数据范围为$expected页中的哪些行， 支持格式为1-3, 1-, -3, 2等
2.5） 如果将excel放在testcase同目录，需要注意在maven下面增加以下配置
    <build>
        <testResources>
            <testResource>
                <directory>src/test/java</directory>
                <includes>
                    <include>**/*.xls</include>
                    <include>**/*.xlsx</include>
                </includes>
            </testResource>
        </testResources>
      </build>
3. 数据填写到excel中后，就可以运行testc了

