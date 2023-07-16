import pymysql
from sshtunnel import SSHTunnelForwarder
import pandas as pd
import stock_predictor

def InsertPredictResult(df):
    # connect to server3 using ssh protocol
    server = SSHTunnelForwarder(
        ssh_address_or_host=('47.109.79.121', 22),  # 指定ssh登录的跳转机的address
        ssh_username='root',  # 跳转机的用户
        ssh_password='Gjnw3765',  # 跳转机的密码
        remote_bind_address=('localhost', 3306))
    server.start()

    # then connect to mysql
    db = 'stock'
    myDB = pymysql.connect(
        user="root",
        passwd="1234",
        host="127.0.0.1",  # 此处必须是 127.0.0.1
        db=db,
        port=server.local_bind_port)
    cursor = myDB.cursor()

    tableName = "t_k_data_predicted"
    for index,row in df.iterrows():
        #print(row.Index, row.code, row.time)
        code=row['code']
        time=row['time']
        daily=row['daily']
        open=row['open']
        high=row['high']
        low=row['low']
        close=row['close']
        volume=row['volume']
        dividens=row['dividends']
        stock_splits=row['stock_splits']
        sql = f"INSERT INTO {tableName} (code,time,daily,open,high,low,close,volume,dividends,stock_splits) VALUES ('{code}','{time}',{daily},{open},{high},{low},{close},{volume},{dividens},{stock_splits})"
        cursor.execute(sql)
        print(cursor.fetchall())
    # cursor.execute('select * from {tableName} where id=10;')

    # myDB.commit()
    server.stop()
    cursor.close()
    myDB.close()


def GetCompanyList():
    server = SSHTunnelForwarder(
        ssh_address_or_host=('47.109.56.80', 22),  # 指定ssh登录的跳转机的address
        ssh_username='root',  # 跳转机的用户
        ssh_password='Gjnw3765',  # 跳转机的密码
        remote_bind_address=('localhost', 3306))
    server.start()

    # then connect to mysql
    db = 'stock'
    myDB = pymysql.connect(
        user="root",
        passwd="1234",
        host="127.0.0.1",  # 此处必须是 127.0.0.1
        db=db,
        port=server.local_bind_port)
    cursor = myDB.cursor()

    cursor.execute("SELECT DISTINCT code FROM t_k_data")
    results = cursor.fetchall()
    codeList = [row[0] for row in results]

    # myDB.commit()
    server.stop()
    cursor.close()
    myDB.close()
    return codeList

def InsertProbPredictResult(df):
    # connect to server3 using ssh protocol
    server = SSHTunnelForwarder(
        ssh_address_or_host=('47.109.79.121', 22),  # 指定ssh登录的跳转机的address
        ssh_username='root',  # 跳转机的用户
        ssh_password='Gjnw3765',  # 跳转机的密码
        remote_bind_address=('localhost', 3306))
    server.start()

    # then connect to mysql
    db = 'stock'
    myDB = pymysql.connect(
        user="root",
        passwd="1234",
        host="127.0.0.1",  # 此处必须是 127.0.0.1
        db=db,
        port=server.local_bind_port)
    cursor = myDB.cursor()

    tableName = "t_up_prediction"
    for index,row in df.iterrows():
        code=row['code']
        field=row['field']
        up=row['up']
        time=row['time']
        correct=row['correct']
        sql = f"INSERT INTO {tableName} (code,field,up,time,correct) VALUES ('{code}','{field}','{up}','{time}','{correct}')"
        cursor.execute(sql)
        print(cursor.fetchall())
    # cursor.execute('select * from {tableName} where id=10;')

    # myDB.commit()
    server.stop()
    cursor.close()
    myDB.close()