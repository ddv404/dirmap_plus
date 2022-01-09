# -*- coding: utf-8 -*-
import sqlite3
import datetime

# 创建数据库文件，并初始化表结构
conn = sqlite3.connect('ddv.db')
print("Opened database successfully")

cursor = conn.cursor()


# 表结构
# 表1
#   id
#   目标的地址（需要做唯一键）
#   状态，0表示未进行扫描，1表示正在进行扫描，2表示已经扫描结束了
#   时间戳
#   cookie
#   project_name
sql = 'CREATE table if not exists target_urls(id integer PRIMARY KEY autoincrement, url varchar(512) unique, state integer, time integer, cookie varchar(512),project_name  varchar(512) )'
cursor.execute(sql)

ts = datetime.datetime.now().timestamp()

# try:
#     cursor.execute('INSERT INTO target_urls (url,state,time,cookie,project_name) VALUES (?,?,?,?,?)',
#                ('tan-hua.com',0,int(ts), "cookie:cookiesdfdsfsdfsdf","test_project"))
#     print(cursor.lastrowid)
# except Exception:
#     print("url yicunzai")


values = cursor.execute("select * from target_urls ")
values = list(map(lambda x:x,values))
print(values)               
 

# 表2
#   id
#   表1的id
#   扫描到的状态码
#   扫描到的返回类型
#   扫描的返回的内容大小
#   扫描的地址
sql = 'CREATE table if not exists result_urls(id integer PRIMARY KEY autoincrement, target_urls_id integer, state integer, result_type varchar(100) , size varchar(200), result_url varchar(512) unique)'
cursor.execute(sql)

# cursor.execute('INSERT INTO result_urls (target_urls_id,state,result_type,size,result_url) VALUES (?,?,?,?,?)',
            #    (1,200,"text", "500kb","http://test.com/a/b/c"))

values = cursor.execute("select * from result_urls")
# url = 'tan-hua.com'
# values = cursor.execute("select id from target_urls where url like ?",("%"+url+"%",))
values = list(map(lambda x:x,values))
print(values)               
 
# 提交事物
conn.commit()

conn.close()