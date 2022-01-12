# -*- coding: utf-8 -*-
import sqlite3
import datetime

# 创建数据库文件，并初始化表结构
conn = sqlite3.connect('ddv.db')
print("Opened database successfully")

cursor = conn.cursor()


# 如果项目表不存在则创建项目表
sql = 'CREATE table if not exists projects(id integer PRIMARY KEY autoincrement, project_name varchar(256) )'
cursor.execute(sql)


# 如果中间关联表不存在就创建表
sql = 'CREATE table if not exists project_target_url(id integer PRIMARY KEY autoincrement, project_id integer, target_url_id integer )'
cursor.execute(sql)

sql = 'CREATE table if not exists target_urls(id integer PRIMARY KEY autoincrement, url varchar(512) unique, state integer, time integer, cookie varchar(512))'
cursor.execute(sql)

sql = 'CREATE table if not exists result_urls(id integer PRIMARY KEY autoincrement, target_urls_id integer, state integer, result_type varchar(100) , size varchar(200), result_url varchar(512) unique)'
cursor.execute(sql)






ts = datetime.datetime.now().timestamp()

# try:
#     cursor.execute('INSERT INTO target_urls (url,state,time,cookie,project_name) VALUES (?,?,?,?,?)',
#                ('tan-hua.com',0,int(ts), "cookie:cookiesdfdsfsdfsdf","test_project"))
#     print(cursor.lastrowid)
# except Exception:
#     print("url yicunzai")


# values = cursor.execute("select * from projects ")

values = cursor.execute("select * from target_urls ")
values = cursor.execute("select * from project_target_url ")

# values = cursor.execute("select * from result_urls ")
values = list(map(lambda x:x,values))
# print(values)    
for value in values:
    print(value)           
 

#  project


# print("-------------------------")

# # cursor.execute('INSERT INTO result_urls (target_urls_id,state,result_type,size,result_url) VALUES (?,?,?,?,?)',
#             #    (1,200,"text", "500kb","http://test.com/a/b/c"))

# values = cursor.execute("select * from result_urls")
# # url = 'tan-hua.com'
# valuessss = cursor.execute("select state from target_urls where project_name = ? and url = ? ",('project', 'https://wappass.baidu.com/static/machine/js/api') )
# values = list(map(lambda x:x,values))
# print(values)               
 
# 提交事物
conn.commit()

conn.close()

