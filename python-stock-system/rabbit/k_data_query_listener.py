import pika
import yfinance as yf
import json
import sys
import os
import time

current_dir = os.path.dirname(os.path.abspath(__file__))
utils_dir = os.path.join(current_dir, '..')
sys.path.append(utils_dir)

from utils.r import R

host = '47.109.56.80'


def callback(ch, method, properties, body):
    data = json.loads(body)['data']
    code = data['code']
    last = data['last']
    daily = data['daily']
    sh = yf.Ticker(code)
    hist = sh.history(start=last, end=int(time.time()), period="max", interval="1d")
    a = []
    for index, row in hist.iterrows():
        if index.timestamp() <= last:
            continue
        row = row.apply(lambda x: str(x))
        d = row.to_dict()
        d['Time'] = str(index)
        d['Volume'] = int(float(d['Volume']))
        a.append(d)
    if len(hist) > 0:
        r = R.ok().set_data({
            'code': code,
            'daily': daily,
            'data': a
        })
        ch.basic_publish(exchange='', routing_key="k-data-result", body=json.dumps(r))
    ch.basic_ack(delivery_tag=method.delivery_tag)


def main():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host, credentials=pika.PlainCredentials('admin', 'admin')))
    channel = connection.channel()
    channel.basic_consume(queue='k-data-query', on_message_callback=callback, auto_ack=False)
    channel.start_consuming()


if __name__ == '__main__':
    main()
