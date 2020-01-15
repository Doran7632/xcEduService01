<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
<#--Hello ${name}!-->
<br>
遍历数据模型中的list学成信息，（数据模型中的名称为stus）
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
    <#--<td>出生日期</td>-->
    </tr>
     <#list stus as stu>
     <tr>
         <td>${stu_index+1}</td>
         <td>${stu.name}</td>
         <td>${stu.age}</td>
     <#--<td>出生日期</td>-->
     </tr>
     </#list>
</table>
<br>
遍历数据模型中的stuMap（Map中的小map,在中括号中简写key）
<br>
姓名：${stuMap["stu1"].name}<br>
年龄：${stuMap["stu1"].age}<br>
姓名：${stuMap.stu1.name}<br>
年龄：${stuMap.stu1.age}<br>
</body>
</html>