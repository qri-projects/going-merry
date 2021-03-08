# going-merry 设计
## 概览
![https://pic.ggemo.com/going-merry.png](https://pic.ggemo.com/going-merry.png)  
设计图源文件(下载后draw.io打开): [OneDrive链接](https://1drv.ms/u/s!AvbOqvo5jCvum3_EWuG1-e4GFQnj?e=AnybjO)  

## 设计中的问题
### 如何传递策略使用的condition?
going-merry选择 显式地将condition作为selectService方法的参数传递  

### condition的匹配规则
condition的类型不同 匹配方式不同  

- 枚举  
    按值匹配
- Map  
    按kv匹配(权重计算)
- Set  
    equals方法匹配, 建议使用HashSet  
- 类对象  
    按字段匹配(权重计算)
  
类对象, map的匹配中, 会进行一个权重的计算, 每命中一个字段 权重增加1, 取权重最高者返回  

