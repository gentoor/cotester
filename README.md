# cotester<br>
test伴侣 --- 数据驱动测试<br>
<br>
当前仅支持excel驱动的测试。<br>
功能支持：目前可以从excel中读取数据自动装配的数据类型包括：<br>
1） 基础数据类型：int, short, long ,byte, double, float, boolean 及其封装类 <br>
2)  常用简单类型：Date、String, StringBuilder, StringBuffer<br>
3)  Collection及Map下面的接口及类<br>
4） 自定义的类， 一般是实体类，毕竟是测试数据<br>
依赖：伟大的testng。（如果你用junit，要么更换testng，要么绕行：））<br>
使用方法：<br>
1.testcase中书写<br>
    @Test(dataProvider = "ExcelDataProvider", dataProviderClass = ExcelDataProvider.class)<br>
    public void testListUsers(String name, List<Users> expected) throws Exception {<br>
        List<Users> actual = cs.listUsers(name);<br>
        Assert.assertNotEquals(actual, expected);<br>
    }<br>
2.excel文件放在testcase同目录或者src/test/resources/与测试类相同目录下面，注意以下几点<br>
2.1） excel的sheet名称采用与测试方法同名，如上例就是 testListUsers<br>
2.2） 参数为基础数据类型、常用简单类型，首行按顺序写上参数名称， 下面数据部分直接书写<br>
2.3） 参数为自定义类时，对应参数及其关心的字段分列填写在首行中如 user.id, user.name， 数据部分直接书写<br>
2.4） 参数为Collection及Map时，首行写参数名；此时需要创建引用sheet页， 如上例就是创建名称为$expected的sheet页；<br>
      在本列下面的数据中要写明数据范围为$expected页中的哪些行， 支持格式为1-3, 1-, -3, 2等<br>
2.5） 如果将excel放在testcase同目录，需要注意在maven下面增加以下配置<br>
    <build><br>
        <testResources><br>
            <testResource><br>
                <directory>src/test/java</directory><br>
                <includes><br>
                    <include>**/*.xls</include><br>
                    <include>**/*.xlsx</include><br>
                </includes><br>
            </testResource><br>
        </testResources><br>
      </build><br>
3. 数据填写到excel中后，就可以运行testc了<br>
