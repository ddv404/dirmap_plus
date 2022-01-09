#!/usr/bin/env python3
# -*- coding: utf-8 -*-

'''
@Author: xxlin
@LastEditors: xxlin
@Date: 2019-04-10 13:27:58
@LastEditTime: 2019-05-01 20:19:40
'''

import gevent
import sys
import time
import traceback
from lib.core.data import conf,paths,th,tasks
from lib.core.common import outputscreen
from lib.core.enums import BRUTER_RESULT_STATUS
from lib.utils.console import getTerminalSize
from lib.controller.bruter import bruter

import sqlite3

def initEngine():
    # init control parameter
    th.result = []
    th.thread_num = conf.thread_num
    th.target = conf.target
    #是否继续扫描标志位
    th.is_continue = True
    #控制台宽度
    th.console_width = getTerminalSize()[0] - 2
    #记录开始时间
    th.start_time = time.time()
    msg = '[+] Set the number of thread: %d' % th.thread_num
    outputscreen.success(msg)

def scan(db_file_name):
    # print("scan")
    # print(db_file_name)
    while True:
        #协程模式
        if th.target.qsize() > 0 and th.is_continue:
            target = str(th.target.get(timeout=1.0))
        else:
            break
        try:
            # 将url的扫描状态修改为正在运行状态
            # if len(db_file_name) > 0:
            #     conn = sqlite3.connect(db_file_name)
            #     cursor = conn.cursor()
            #     cursor.execute('UPDATE target_urls set state = 1 where url = ?', (target,))
            #     conn.commit()
            #     conn.close()
            #对每个target进行检测
            bruter(db_file_name,target)
            tasks.task_count = 0
            # 将url的扫描状态修改为已经完成状态
            if len(db_file_name) > 0:
                try:
                    conn = sqlite3.connect(db_file_name)
                    cursor = conn.cursor()
                    cursor.execute('UPDATE target_urls set state = 2 where url = ?', (target,))
                    conn.commit()
                    conn.close()
                
                except Exception as e:
                    outputscreen.error(str(e))
                    pass

        except Exception:
            #抛出异常时，添加errmsg键值
            th.errmsg = traceback.format_exc()
            th.is_continue = False

def run(args):
    initEngine()
    # Coroutine mode
    outputscreen.success('[+] Coroutine mode')

    db_file_name = ''
    if args.target_db:
        db_file_name = args.target_db

    #     while True:
    #         gevent.joinall([gevent.spawn(scan,db_file_name) for i in range(0, th.thread_num)])
    #         if 'errmsg' in th:
    #             outputscreen.error(th.errmsg)
    #         time.sleep(10)
    #         print("12")
    # else:

    gevent.joinall([gevent.spawn(scan,db_file_name) for i in range(0, th.thread_num)])
    if 'errmsg' in th:
        outputscreen.error(th.errmsg)

    
