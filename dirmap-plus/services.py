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

    # 查询一下该地址是否已经目录爆破过
    # 在目标表target_urls中查看
    try:
        ids = cursor.execute("select id from target_urls where url like ?",("%"+url+"%",))
        ids = list(map(lambda x:x[0],ids))
        # 表示待检测url已经存在
        if len(ids) > 0:
            return {
                'status': 'fail',
                'message': 'url is exist'
            }
    except Exception:
        return {
            'status': 'fail',
            'message': 'select target_urls error'
        }

    # 在返回表result_urls中查看
    try:
        ids = cursor.execute("select id from result_urls where result_url like ?",("%"+url+"%",))
        ids = list(map(lambda x:x[0],ids))
        # 表示待检测url已经存在
        if len(ids) > 0:
            return {
                'status': 'fail',
                'message': 'url is exist'
            }
    except Exception:
        return {
            'status': 'fail',
            'message': 'select result_urls error'
        }

    ts = datetime.datetime.now().timestamp()
    # 如果目标地址不存在 就插入到数据库中
    try:
        cursor.execute('INSERT INTO target_urls (url,state,time,cookie,project_name) VALUES (?,?,?,?,?)',
               (url,0,int(ts), cookie,project_name))
    except Exception:
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

    conn = sqlite3.connect(db_file_name)
    cursor = conn.cursor()


    pn = ''
    ps = ("%"+url+"%",)
    if len(project_name) > 0:
        pn = "target_urls_id in (select id from target_urls where project_name = ?) and"
        ps = (project_name, "%"+url+"%",)


    # 在返回表result_urls中查看
    try:
        values = cursor.execute("select state,result_type,size,result_url from result_urls where "+ pn +" result_url like ? ",ps)
        values = list(map(lambda x:{
            'state':x[0],
            'result_type':x[1],
            'size':x[2],
            'result_url':x[3]
        },values))
    except Exception as e:
        print(e)
        return {
            'status':'fail',
            'message':'select result_urls error'
        }

    conn.commit()
    conn.close()


    return {
            'status': 'success',
            'message': values
        }






def start_run():
    # try:
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