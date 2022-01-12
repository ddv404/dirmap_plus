#!/usr/bin/env python
# -*- coding: utf-8 -*-
import threading
import time
import datetime

from fastapi import FastAPI
from starlette.requests import Request
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
import sqlite3


from pydantic import BaseModel

# 实例化一个FastAPI实例
app = FastAPI()

# 设置允许访问的域名
origins = ["*"]  #也可以设置为"*"，即为所有。

# 设置跨域传参
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,  # 设置允许的origins来源
    allow_credentials=True,
    allow_methods=["*"],  # 设置允许跨域的http方法，比如 get、post、put等。
    allow_headers=["*"])  # 允许跨域的headers，可以用来鉴别来源等作用。


auth_key = "ddv"
db_file_name = "ddv.db"



@app.get("/")
async def read_root():
    return "走开"






class Project(BaseModel):
    # 待扫描的目标地址
    url: str
    # cookie值
    cookie: str
    # 所属项目
    project_name: str
    # 认证key
    key: str

# 保存推荐方案
@app.post("/add_scan_url")
async def save_plan( project: Project):
    url = project.url
    cookie = project.cookie
    project_name = project.project_name
    key = project.key

    print(project)

    # 可在这个地方加入识别web站点指纹的信息

    # 认证key不能为空
    if len(key.replace(' ','')) == 0:
        return {
            'status': 'fail',
            'message': 'key is empty'
        }
    # 确认认证key
    if key != auth_key:
        return {
            'status': 'fail',
            'message': 'key is invalid'
        }

    # 判断待检测地址是否为空
    # 如果url地址为空则表示是测试请求
    if len(url) == 0 or url.find('.') == -1:
        return {
            'status': 'success',
            'message': 'connect ok'
        }

    conn = sqlite3.connect(db_file_name)
    cursor = conn.cursor()


    # # 如果项目表不存在则创建项目表
    # sql = 'CREATE table if not exists projects(id integer PRIMARY KEY autoincrement, project_name varchar(256) )'
    # cursor.execute(sql)


    # 判断项目已经存在，如存储就获取id，如果不存在就创建并获取id
    select_result_project_ids = cursor.execute("select id from projects where project_name = ?",(project_name,))   
    select_result_project_ids = list(map(lambda x:x[0],select_result_project_ids)) 
    if len(select_result_project_ids) == 0:
        cursor.execute("INSERT INTO projects (project_name) values (?)",(project_name,)) 
        project_id = cursor.lastrowid
    else:
        project_id = select_result_project_ids[0]


    # # 如果中间关联表不存在就创建表
    # sql = 'CREATE table if not exists project_target_url(id integer PRIMARY KEY autoincrement, project_id integer, target_url_id integer )'
    # cursor.execute(sql)


    # 查询一下该地址是否已经目录爆破过
    # 在目标表target_urls中查看
    try:
        ids = cursor.execute("select id from target_urls where url = ?",(url,))
        ids = list(map(lambda x:x[0],ids))
        # 表示待检测url已经存在
        if len(ids) > 0:
            target_url_id = ids[0]
            cursor.execute("INSERT INTO project_target_url (project_id,target_url_id) values (?,?)",(project_id,target_url_id)) 
            return {
                'status': 'fail',
                'message': 'url is exist'
            }
        
    except Exception as e:
        print(e)
        return {
            'status': 'fail',
            'message': 'select target_urls error'
        }

    # 在返回表result_urls中查看
    # try:
    #     ids = cursor.execute("select id from result_urls where result_url like ?",("%"+url+"%",))
    #     ids = list(map(lambda x:x[0],ids))
    #     # 表示待检测url已经存在
    #     if len(ids) > 0:
    #         return {
    #             'status': 'fail',
    #             'message': 'url is exist'
    #         }
    # except Exception:
    #     return {
    #         'status': 'fail',
    #         'message': 'select result_urls error'
    #     }

    ts = datetime.datetime.now().timestamp()
    # 如果目标地址不存在 就插入到数据库中
    try:
        cursor.execute('INSERT INTO target_urls (url,state,time,cookie) VALUES (?,?,?,?)',
               (url,0,int(ts), cookie))
        target_url_id = cursor.lastrowid
        # print(project_id)
        # print(target_url_id)
        cursor.execute("INSERT INTO project_target_url (project_id,target_url_id) values (?,?)",(project_id,target_url_id)) 

    except Exception as e:
        print(e)
        return {
            'status': 'fail',
            'message': 'insert target_urls error'
        }

    conn.commit()
    conn.close()

    return {
            'status': 'success',
            'message': 'ok'
        }



class SelectProject(BaseModel):
    # 待扫描的目标地址
    url: str
    # 所属项目
    project_name: str
    # 认证key
    key: str

# 保存推荐方案
@app.post("/select_scan_result")
async def select_scan_result( project: SelectProject):
    url = project.url
    project_name = project.project_name
    key = project.key

    # 认证key不能为空
    if len(key.replace(' ','')) == 0:
        return {
            'status': 'fail',
            'message': 'key is empty'
        }
    # 确认认证key
    if key != auth_key:
        return {
            'status': 'fail',
            'message': 'key is invalid'
        }

    # 判断待检测地址是否为空
    if len(url) == 0 or url.find('.') == -1:
        return {
            'status': 'fail',
            'message': 'url is invalid'
        }


    pn = ''
    ps = ("%"+url+"%",)
    if len(project_name) > 0:
       
        # pn = "target_urls_id in (select id from target_urls where project_name = ?) and"
        pn = "target_urls_id in (  select target_url_id from project_target_url where project_id in ( select id from projects where project_name = ? ) ) and"
        ps = (project_name, "%"+url+"%",)


    # 在返回表result_urls中查看
    try:
            
        conn = sqlite3.connect(db_file_name)
        cursor = conn.cursor()

        values = cursor.execute("select state,result_type,size,result_url from result_urls where "+ pn +" result_url like ? ",ps)
        values = list(map(lambda x:{
            'result_url':x[3],
            'size':x[2],
            'result_type':x[1],
            'state':x[0]
        },values))

            
        if len(values) == 0:
                
            # 查询一下该目标地址的状态，如果状态已经结束 还没有数据则表明该地址没有扫描到结果
            # print(project_name)
            # print(url)
            target_urls = cursor.execute("select state from target_urls where url = ? ",( url,) )
            # print(list(valuessss))
            target_urls = list(map(lambda x:x[0],target_urls))
            # target_urls = list(target_urls)
            # print(target_urls)
            if len(target_urls) != 0:
                state = target_urls[0]
                if state == 0:
                    values.append("当前没有查询到结果,如长时间没有返回值，请查看dirmap是否正在运行，或目标地址过多请稍后再再次查询")
                else:
                    values.append("当前地址没有扫描到有效结果")
            else:
                values.append("当前地址没有扫描到有效结果1")
                        
        conn.commit()
        conn.close()
    except Exception as e:
        print(e)
        return {
            'status':'fail',
            'message':'select result_urls error'
        }





    return {
            'status': 'success',
            'message': values
        }



class LoadProject(BaseModel):
    # 所属项目
    project_name: str
    # 认证key
    key: str

# 保存推荐方案
@app.post("/load_project_data")
async def load_project_data( project: LoadProject):
    print(project)
    project_name = project.project_name
    key = project.key

    # 认证key不能为空
    if len(key.replace(' ','')) == 0:
        return {
            'status': 'fail',
            'message': 'key is empty'
        }
    # 确认认证key
    if key != auth_key:
        return {
            'status': 'fail',
            'message': 'key is invalid'
        }

   
    conn = sqlite3.connect(db_file_name)
    cursor = conn.cursor()


    
    # 在返回表result_urls中查看  select target_url_id from project_target_url where project_id in ( select id from projects where project_name = ? )
    try:
        # 如果查询的项目为空，则查询最近插入的100条数据
        if len(project_name)  == 0:
            values = cursor.execute("select url from target_urls ORDER BY id DASC limit 100  ")
        else:
            values = cursor.execute("select url from target_urls where id in ( select target_url_id from project_target_url where project_id in ( select id from projects where project_name = ? )) ",(project_name,))
        target_urls = list(map(lambda x:x[0],values))
    except Exception as e:
        print(e)
        return {
            'status':'fail',
            'message':'select project url error'
        }

    conn.commit()
    conn.close()
    

    return {
            'status': 'success',
            'message': target_urls
        }









# 初始化数据库表
def init_db():
    conn = sqlite3.connect('ddv.db')
    print("Opened database successfully")
    cursor = conn.cursor()
    # 如果项目表不存在则创建项目表
    sql = 'CREATE table if not exists projects(id integer PRIMARY KEY autoincrement, project_name varchar(256) )'
    cursor.execute(sql)
    # 如果中间关联表不存在就创建表
    sql = 'CREATE table if not exists project_target_url(id integer PRIMARY KEY autoincrement, project_id integer, target_url_id integer )'
    cursor.execute(sql)
    # 表结构
    sql = 'CREATE table if not exists target_urls(id integer PRIMARY KEY autoincrement, url varchar(512) unique, state integer, time integer, cookie varchar(512))'
    cursor.execute(sql)
    sql = 'CREATE table if not exists result_urls(id integer PRIMARY KEY autoincrement, target_urls_id integer, state integer, result_type varchar(100) , size varchar(200), result_url varchar(512) unique)'
    cursor.execute(sql)
    conn.commit()
    conn.close()


def start_run():
    # try:
        init_db()
        uvicorn.run(app='services:app', host="0.0.0.0", port=10011)
    # except KeyboardInterrupt:
    #     pass



if __name__ == '__main__':   
    start_run()        

# def run_services():
#     start_run()

# def run_test():
#     print("run_test")


# print("开始")
# # daemon_task = threading.Thread(name='daemon', target=daemon, daemon=True) #设置为首护线程
# non_daemon_task = threading.Thread(name='start_run', target=start_run)
# non_daemon_task.setDaemon(True)

# non_daemon_task.start()
# time.sleep(2)
# print("结束")
# time.sleep(5)