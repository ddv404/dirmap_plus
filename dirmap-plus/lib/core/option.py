#!/usr/bin/env python3
# -*- coding: utf-8 -*-

'''
@Author: xxlin
@LastEditors: ttttmr
@Date: 2019-04-10 13:27:58
@LastEditTime: 2019-05-29 16:52:42
'''

import imp
import os
import queue
import sys
import time
import ipaddress


from lib.controller.bruter import loadConf
from lib.core.common import parseTarget, outputscreen
from lib.core.data import conf, paths
from thirdlib.IPy.IPy import IP

import sqlite3
from urllib.parse import urlparse

def initOptions(args):
    EngineRegister(args)
    BruterRegister(args)
    TargetRegister(args)


def EngineRegister(args):
    """
    加载并发引擎模块
    """
    conf.engine_mode = 'coroutine'

    #设置线程数
    if args.thread_num > 200 or args.thread_num < 1:
        msg = '[*] Invalid input in [-t](range: 1 to 200), has changed to default(30)'
        outputscreen.warning(msg)
        conf.thread_num = 30
        return
    conf.thread_num = args.thread_num

def BruterRegister(args):
    """
    配置bruter模块
    """

    if args.load_config_file:
        #加载配置文件
        loadConf()
    else:
        outputscreen.error("[+] Function development, coming soon!please use -lcf parameter")
        if args.debug:
            conf.debug = args.debug
        else:
            conf.debug = args.debug
        sys.exit()

def TargetRegister(args):
    """
    加载目标模块
    """
    msg = '[*] Initialize targets...'
    outputscreen.warning(msg)

    #初始化目标队列
    conf.target = queue.Queue()

    # 用户输入入队
    if args.target_input:
        # 尝试解析目标地址
        try:
            lists=parseTarget(args.target_input)
        except:
            helpmsg = "Invalid input in [-i], Example: -i [http://]target.com or 192.168.1.1[/24] or 192.168.1.1-192.168.1.100"
            outputscreen.error(helpmsg)
            sys.exit()
        # 判断处理量
        if (len(lists))>100000:
            warnmsg = "[*] Loading %d targets, Maybe it's too much, continue? [y/N]" % (len(lists))
            outputscreen.warning(warnmsg)
            flag =input()
            if flag in ('Y', 'y', 'yes', 'YES','Yes'):
                pass
            else:
                msg = '[-] User quit!'
                outputscreen.warning(msg)
                sys.exit()
        msg = '[+] Load targets from: %s' % args.target_input
        outputscreen.success(msg)
        # save to conf
        for target in lists:
            conf.target.put(target)
        conf.target_nums = conf.target.qsize()

    # 文件读入入队
    elif args.target_file:
        if not os.path.isfile(args.target_file):
            msg = '[-] TargetFile not found: %s' % args.target_file
            outputscreen.error(msg)
            sys.exit()
        msg = '[+] Load targets from: %s' % args.target_file
        outputscreen.success(msg)
        with open(args.target_file, 'r', encoding='utf-8') as f:
            targets = f.readlines()
            for target in targets:
                target=target.strip('\n')
                parsed_target=parseTarget(target)
                for i in parsed_target:
                    conf.target.put(i)
        conf.target_nums = conf.target.qsize()

    elif args.target_db:
          # 如果文件不存在，则需要创建文件
        db_file_name = args.target_db
        conn = sqlite3.connect(db_file_name)
        cursor = conn.cursor()

        # 判断文件是否村存在
        # if not os.path.isfile(args.target_db):
        try:
            # 初始化表
            # sql = 'CREATE table if not exists target_urls(id integer PRIMARY KEY autoincrement, url varchar(512) unique, state integer, time integer, cookie varchar(512),project_name  varchar(512) )'
            # cursor.execute(sql)
            # sql = 'CREATE table if not exists result_urls(id integer PRIMARY KEY autoincrement, target_urls_id integer, state integer, result_type varchar(100) , size varchar(200), result_url varchar(512))'
            # cursor.execute(sql)

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
            
        except Exception as e:
            # outputscreen.error(122)
            outputscreen.error(str(e))
            exit(0)

        conn.commit()
        conn.close()

        msg = '[+] Create db file: %s' % args.target_db
        outputscreen.success(msg)

        # 加载指定得db文件
        msg = '[+] Load targets from: %s' % args.target_db
        outputscreen.success(msg)
        # 从db中读取最多30个目标地址
        conn = sqlite3.connect(args.target_db)
        cursor = conn.cursor()
        try:
            target_urls = cursor.execute("select url from target_urls where state = 0")
            target_urls = list(map(lambda x:x[0],target_urls))
            # 为防止目标系统崩溃，同一个host:port的地址，一次最多只保留两个
            temp_target_urls = {}
            for target_url in target_urls:  
                # url = 'http://www.baidu.com:81'
                _url = urlparse(target_url)
                hostname = _url.hostname
                port = _url.port
                
                key = hostname
                if port:
                    key += str(port)

                # 如果数据已经存在，并且长度达到两个
                if key in temp_target_urls and len(temp_target_urls[key]) == 2:
                    continue
                elif key in temp_target_urls and len(temp_target_urls[key]) < 2:
                    temp_target_urls[key].append(target_url)
                elif key not in temp_target_urls:
                    temp_target_urls[key] = [target_url]

            # 组装目标地址
            run_target_urls = []
            for key in temp_target_urls:
                run_target_urls.extend(temp_target_urls[key])


        except Exception as e:
            outputscreen.error(str(e))
            exit(0)
             
        # print(target_urls)
        for target in run_target_urls:
            target=target.strip('\n')
            parsed_target=parseTarget(target)
            for i in parsed_target:
                conf.target.put(i)
        conf.target_nums = conf.target.qsize()



    #验证目标数量
    if conf.target.qsize() == 0  and not args.target_db:
        errormsg = msg = '[!] No targets found.Please load targets with [-i|-iF]'
        outputscreen.error(errormsg)
        sys.exit()