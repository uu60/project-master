import pika
import yfinance as yf
import json
from utils.r import R


def callback(ch, method, properties, body):
    r = json.loads(body)
    code = r['code']
    sh = yf.Ticker(code)
    hist = sh.history(period="25h", interval="1h")
    a = []
    for index, row in hist.iterrows():
        d = row.to_dict()
        d['Time'] = str(index)
        a.append(d)
    if len(hist) > 0:
        r = R.ok(code).set_data(a)
        ch.basic_publish(exchange='', routing_key="k-data-result", body=json.dumps(r))
    ch.basic_ack(delivery_tag=method.delivery_tag)


def main():
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()
    channel.basic_consume(queue='k-data-query', on_message_callback=callback, auto_ack=False)
    channel.start_consuming()


if __name__ == '__main__':
    main()
